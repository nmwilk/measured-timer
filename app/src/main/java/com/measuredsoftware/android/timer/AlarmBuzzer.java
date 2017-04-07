package com.measuredsoftware.android.timer;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Service to ring the buzzer.
 * 
 * @author neil
 * 
 */
public class AlarmBuzzer extends Service
{
    private static final int ALARM_TIMEOUT_SECONDS = 3 * 60;

    private static final long[] vibratePattern = new long[]
    { 600, 400 };

    private boolean playing = false;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private long startTime;
    private TelephonyManager telephonyManager;
    private int initialCallState;

    // Internal messages
    private static final int KILLER = 1000;
    private static StopHandler stopHandler;

    private static class StopHandler extends Handler
    {
        private final WeakReference<AlarmBuzzer> parent;

        public StopHandler(final AlarmBuzzer buzzer)
        {
            parent = new WeakReference<AlarmBuzzer>(buzzer);
        }

        public void handleMessage(final Message msg)
        {
            switch (msg.what)
            {
                case KILLER:
                    parent.get().sendKillBroadcast();
                    parent.get().stopSelf();
                    break;
            }
        }
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener()
    {
        @Override
        public void onCallStateChanged(int state, String ignored)
        {
            if (state != TelephonyManager.CALL_STATE_IDLE && state != initialCallState)
            {
                sendKillBroadcast();
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate()
    {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        AlarmAlertWakeLock.acquireCpuWakeLock(this);
        
        stopHandler = new StopHandler(this);
    }

    @Override
    public void onDestroy()
    {
        stop();
        telephonyManager.listen(phoneStateListener, 0);
        AlarmAlertWakeLock.releaseCpuLock();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent == null)
        {
            stopSelf();
            return START_NOT_STICKY;
        }

        play();
        initialCallState = telephonyManager.getCallState();

        return START_STICKY;
    }

    private void sendKillBroadcast()
    {
        long millis = System.currentTimeMillis() - startTime;
        int minutes = (int) Math.round(millis / 60000.0);
        Intent alarmKilled = new Intent(Alarms.ALARM_KILLED);
        alarmKilled.putExtra(Alarms.ALARM_KILLED_TIMEOUT, minutes);
        sendBroadcast(alarmKilled);
    }

    private static final float IN_CALL_VOLUME = 0.125f;

    private void play()
    {
        stop();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(new OnErrorListener()
        {
            public boolean onError(MediaPlayer mp, int what, int extra)
            {
                mp.stop();
                mp.release();
                mediaPlayer = null;
                return true;
            }
        });

        try
        {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            {
                if (telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE)
                {
                    mediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
                }
                else
                {
                    float ringVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.alarm_sound);
                    if (afd != null)
                    {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mediaPlayer.setVolume(ringVol, ringVol);
                    }
                }
                startAlarm(mediaPlayer);
            }
        }
        catch (Exception ex)
        {
            try
            {
                mediaPlayer.reset();
                startAlarm(mediaPlayer);
            }
            catch (Exception ex2)
            {
            }
        }
        vibrator.vibrate(vibratePattern, 0);

        enableKiller();
        playing = true;
        startTime = System.currentTimeMillis();
    }

    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException,
            IllegalStateException
    {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
        {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            player.start();
        }
    }

    private void stop()
    {
        if (playing)
        {
            playing = false;

            Intent alarmDone = new Intent(Alarms.ALARM_DONE_ACTION);
            sendBroadcast(alarmDone);

            if (mediaPlayer != null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            vibrator.cancel();
        }
        disableKiller();
    }

    private static void enableKiller()
    {
        stopHandler.sendMessageDelayed(stopHandler.obtainMessage(KILLER, 0), 1000 * ALARM_TIMEOUT_SECONDS);
    }

    private static void disableKiller()
    {
        stopHandler.removeMessages(KILLER);
    }
}
