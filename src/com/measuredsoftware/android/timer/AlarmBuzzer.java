package com.measuredsoftware.android.timer;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class AlarmBuzzer extends Service {
  private static final int ALARM_TIMEOUT_SECONDS = 3 * 60;

  private static final long[] sVibratePattern = new long[] { 600, 400 };

  private boolean mPlaying = false;
  private Vibrator mVibrator;
  private MediaPlayer mMediaPlayer;
  private long mStartTime;
  private TelephonyManager mTelephonyManager;
  private int mInitialCallState;

  // Internal messages
  private static final int KILLER = 1000;
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case KILLER:
          sendKillBroadcast();
          stopSelf();
          break;
      }
    }
  };

  private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
    @Override
    public void onCallStateChanged(int state, String ignored) {
      if (state != TelephonyManager.CALL_STATE_IDLE && state != mInitialCallState) {
        sendKillBroadcast();
        stopSelf();
      }
    }
  };

  @Override
  public void onCreate() {
    mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    AlarmAlertWakeLock.acquireCpuWakeLock(this);
  }

  @Override
  public void onDestroy() {
    stop();
    mTelephonyManager.listen(mPhoneStateListener, 0);
    AlarmAlertWakeLock.releaseCpuLock();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      stopSelf();
      return START_NOT_STICKY;
    }

    play();
    mInitialCallState = mTelephonyManager.getCallState();

    return START_STICKY;
  }

  private void sendKillBroadcast() {
    long millis = System.currentTimeMillis() - mStartTime;
    int minutes = (int) Math.round(millis / 60000.0);
    Intent alarmKilled = new Intent(Alarms.ALARM_KILLED);
    alarmKilled.putExtra(Alarms.ALARM_KILLED_TIMEOUT, minutes);
    sendBroadcast(alarmKilled);
  }

  private static final float IN_CALL_VOLUME = 0.125f;

  private void play() {
    stop();

    mMediaPlayer = new MediaPlayer();
    mMediaPlayer.setOnErrorListener(new OnErrorListener() {
      public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.stop();
        mp.release();
        mMediaPlayer = null;
        return true;
      }
    });

    try {
      AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);    
      if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
        if (mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
          mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
        } else {
          float ringVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
          AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.alarm_sound); 
          if (afd != null) {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength()); 
            mMediaPlayer.setVolume(ringVol, ringVol);
          }
        }
        startAlarm(mMediaPlayer);
      }
    } catch (Exception ex) {
      try {
        mMediaPlayer.reset();
        startAlarm(mMediaPlayer);
      } catch (Exception ex2) {
      }
    }
    mVibrator.vibrate(sVibratePattern, 0);

    enableKiller();
    mPlaying = true;
    mStartTime = System.currentTimeMillis();
  }

  private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {
    final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
      player.setAudioStreamType(AudioManager.STREAM_ALARM);
      player.setLooping(true);
      player.prepare();
      player.start();
    }
  }

  public void stop() {
    if (mPlaying) {
      mPlaying = false;

      Intent alarmDone = new Intent(Alarms.ALARM_DONE_ACTION);
      sendBroadcast(alarmDone);

      if (mMediaPlayer != null) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
      }

      mVibrator.cancel();
    }
    disableKiller();
  }

  private void enableKiller() {
    mHandler.sendMessageDelayed(mHandler.obtainMessage(KILLER, 0), 1000 * ALARM_TIMEOUT_SECONDS);
  }

  private void disableKiller() {
    mHandler.removeMessages(KILLER);
  }
}
