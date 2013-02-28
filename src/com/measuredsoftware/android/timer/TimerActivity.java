package com.measuredsoftware.android.timer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.measuredsoftware.android.library2.utils.DateTools;
import com.measuredsoftware.android.library2.utils.NetTools;
import com.measuredsoftware.android.library2.utils.http.HttpTools;
import com.measuredsoftware.android.timer.views.TimerView;
import com.measuredsoftware.android.timer.views.TopBar;

/**
 * Main activity for app.
 * 
 * @author neil
 * 
 */
public class TimerActivity extends Activity implements TimerView.OnEventListener, View.OnClickListener
{
    /** the alarm ringing variable name for the intent*/
    public static final String INTENT_VAR_ALARM_RINGING = "alarmringing";
    
    /** the device asleep variable name for the intent*/
    public static final String INTENT_VAR_DEVICE_ASLEEP = "deviceasleep";

    private static final int ALARM_ID = 103494;

    private static final int NOTIFICATION_ID = 1;

    private static final long DAY_MS = 24 * 60 * 60 * 1000;
    private static final long UPLOAD_EVERY = 5 * DAY_MS;

    private static String deviceId;
    private static String deviceModel;
    private static String deviceOsVersion;

    private static final String PREFS_VAL_SENDSTATS = "sendstats";
    private static final String PREFS_VAL_USE_NOTIFICATIONS = "usenotifications";
    
    private static final String PREFS_VAL_ENDTIME = "endtime";
    private static final String PREFS_VAL_INSTALLDATE = "installdate";
    private static final String PREFS_VAL_CLICKEDMMS = "clickedmms"; 

    /* average length of time they set timers for */
    private static final String PREFS_VAL_AVTIMELEN = "avtimelen";
    
    /* how many times have the set the timer */
    private static final String PREFS_VAL_USAGECOUNT = "timercount";
    
    private static final String PREFS_VAL_LASTUPLOAD = "laststatsupload";

    private static final String STATS_URL = "https://www.measuredsoftware.co.uk/timer/anonstats.php";

    private static String notificationTitle;
    private static String notificationExTitle;
    private static String notificationExDesc;

    private TimerView mDial;
    private ImageView mMainBG;

    private Animation mFadeInSlightAnim;
    private Animation mFadeOutSlightAnim;

    private TickThread mTickThread = null;

    private boolean mAlarmRinging;

    private SharedPreferences mPrefs;
    private long mEndTimeMS;

    private boolean mFirstChange; // first change of timer since DOWN action
    
    private static class TickHandler extends Handler
    {
        private final WeakReference<TimerActivity> mParent;

        public TickHandler(final TimerActivity parent)
        {
            mParent = new WeakReference<TimerActivity>(parent);
        }

        @Override
        public void handleMessage(Message msg)
        {
            mParent.get().getDial().updateTime();
        }
    }

    private static TickHandler tickHandler;

    private static class NetHandler extends Handler
    {
        private final WeakReference<TimerActivity> mParent;

        public NetHandler(final TimerActivity parent)
        {
            mParent = new WeakReference<TimerActivity>(parent);
        }

        @Override
        public void handleMessage(Message msg)
        {
            final int code = msg.what;
            try
            {
                switch (code)
                {
                    case ERROR_NET_SUCCESS:
                        mParent.get().setLastStatsUpload(System.currentTimeMillis());
                        break;
                }
            }
            catch (Exception e)
            {
            }
        }
    }
    
    private NetHandler netHandler;

    // stats vars from prefs
    private String mInstallDate;
    private int mClickedMMSCount;
    private int mAvTimeLen;
    private int mUsageCount;
    private long mLastStatsUpload;
    private boolean mUploadStats;
    private boolean mShowNotification;

    private boolean mThreadRun;

    private static int mAppVersion;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        tickHandler = new TickHandler(this);
        netHandler = new NetHandler(this);
        
        Globals.init(getResources());

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                |
                // WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        PackageInfo pInfo = null;
        try
        {
            pInfo = getPackageManager().getPackageInfo("com.measuredsoftware.android.timer",
                    PackageManager.GET_META_DATA);
            mAppVersion = pInfo.versionCode;
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

        deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        deviceModel = Build.MODEL.toLowerCase();
        deviceOsVersion = Build.VERSION.SDK;

        notificationTitle = getString(R.string.notification_title);
        notificationExTitle = getString(R.string.notification_extitle);
        notificationExDesc = getString(R.string.notification_exdesc);

        mAlarmRinging = false;
        Intent intent = getIntent();
        if (intent != null)
        {
            mAlarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
        }

        setContentView(R.layout.app);
        
        // create animations
        mFadeInSlightAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slight);
        mFadeOutSlightAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out_slight);

        mMainBG = (ImageView) findViewById(R.id.back);

        mDial = (TimerView) findViewById(R.id.timer);
        mDial.setOnSetValueChangedListener(this);
        
        final TopBar topBar = (TopBar)findViewById(R.id.top_bar);
        topBar.setOnClickListener(this);

        mTickThread = null;

        mFirstChange = true;

        // mPrefs = getSharedPreferences(PREFS_LOC, MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEndTimeMS = mPrefs.getLong(PREFS_VAL_ENDTIME, 0);

        // stats
        mInstallDate = mPrefs.getString(PREFS_VAL_INSTALLDATE, "");
        mLastStatsUpload = mPrefs.getLong(PREFS_VAL_LASTUPLOAD, System.currentTimeMillis());
        mClickedMMSCount = mPrefs.getInt(PREFS_VAL_CLICKEDMMS, 0);
        mAvTimeLen = mPrefs.getInt(PREFS_VAL_AVTIMELEN, 0);
        mUsageCount = mPrefs.getInt(PREFS_VAL_USAGECOUNT, 0);

        loadUserPrefs();

        if (mInstallDate.length() == 0)
        {
            final SharedPreferences.Editor editor = mPrefs.edit();
            mInstallDate = DateTools.getYYYYMMDD();
            mLastStatsUpload = System.currentTimeMillis() - (4 * DAY_MS);
            editor.putString(PREFS_VAL_INSTALLDATE, mInstallDate);
            editor.putLong(PREFS_VAL_LASTUPLOAD, mLastStatsUpload);
            editor.commit();
        }
    }

    protected void setLastStatsUpload(final long currentTimeMillis)
    {
        mLastStatsUpload = currentTimeMillis;
        writeToPrefs(PREFS_VAL_LASTUPLOAD, mLastStatsUpload);
    }

    protected TimerView getDial()
    {
        return mDial;
    }

    private void loadUserPrefs()
    {
        mUploadStats = mPrefs.getBoolean(PREFS_VAL_SENDSTATS, true);
        mShowNotification = mPrefs.getBoolean(PREFS_VAL_USE_NOTIFICATIONS, true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mAlarmRinging)
        {
            mEndTimeMS = 0;
            mDial.setEndTime(0);
            stopAlarmRinging();
            writeEndTimeToPrefs();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        stopTickThread();
        mThreadRun = false;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Log.d("mtimer","onStart");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        mAlarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
        intent.removeExtra(INTENT_VAR_ALARM_RINGING);
        intent.removeExtra(INTENT_VAR_DEVICE_ASLEEP);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        startTickThread();

        mThreadRun = true;

        if (mAlarmRinging && mEndTimeMS > 0)
        {
            showStopBuzzerButton();
            mDial.setAlarmIsRinging(true);
        }

        int now = (int) (System.currentTimeMillis() / 1000);

        if (mEndTimeMS > now)
        {
            // if there was a saved timer and it not expired, start it
            mDial.setEndTime(mEndTimeMS);
        }
        else if (mEndTimeMS == 0)
        {
            mDial.setEndTime(0);
        }

        startNetThread();
    }

    private static final int SHOW_PREFERENCES_RESULT_CODE = 1;

    @Override
    public void onClick(final View view)
    {
        final int viewId = view.getId();

        switch (viewId)
        {
            case R.id.measured_button:
            {
                writeToPrefs(TimerActivity.PREFS_VAL_CLICKEDMMS, ++mClickedMMSCount);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.otherswlink)));

                startActivity(intent);
                break;
            }
            case R.id.settings_button:
            {
                Intent intent = new Intent(this, TimerPrefs.class);
                startActivityForResult(intent, SHOW_PREFERENCES_RESULT_CODE);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCES_RESULT_CODE)
        {
            final boolean lastSN = this.mShowNotification;
            this.loadUserPrefs();
            if (!mShowNotification)
            {
                removeNotificationItem();
            }
            else if (!lastSN && mShowNotification && this.mEndTimeMS > 0) createNotificationItem();
        }
    }

    @Override
    public void valueChanged(int angle)
    {
        if (mFirstChange)
        {
            this.fadeOutBackground();
            mFirstChange = false;
        }
    }
    
    @Override
    public void started(int millisecs)
    {
        mFirstChange = true;
        this.fadeInBackground();
        if (millisecs > 0)
        {
            // showCancelButton();
            mEndTimeMS = System.currentTimeMillis() + (millisecs * 1000);
            setupAlarm(mEndTimeMS);
            ++mUsageCount;
            final int timerLenSecs = (int) (mEndTimeMS - System.currentTimeMillis()) / 1000;
            mAvTimeLen = ((mAvTimeLen + timerLenSecs) / mUsageCount);
            final SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PREFS_VAL_AVTIMELEN, mAvTimeLen);
            editor.putInt(PREFS_VAL_USAGECOUNT, mUsageCount);
            editor.commit();
        }
        else
        {
            mEndTimeMS = 0;
            Alarms.disableAlert(this);
            removeNotificationItem();
        }
        writeEndTimeToPrefs();
    }
    
    @Override
    public void cancelled()
    {
        mFirstChange = true;
        writeEndTimeToPrefs();
    }

    private void setupAlarm(long endTime)
    {
        Alarms.enableAlert(this, endTime);
        createNotificationItem();
    }

    private void stopAlarmRinging()
    {
        removeNotificationItem();
        Alarms.disableAlert(this);
        mAlarmRinging = false;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(ALARM_ID);
        stopService(new Intent(Alarms.ALARM_ALERT_ACTION));
        mDial.setAlarmIsRinging(false);
    }

    private boolean createNotificationItem()
    {
        if (!this.mShowNotification) return false;

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification(R.drawable.statusicon, notificationTitle, System.currentTimeMillis());
        Intent intent = new Intent(this, TimerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        notify.setLatestEventInfo(this, notificationExTitle, notificationExDesc, pi);

        nm.notify(TimerActivity.NOTIFICATION_ID, notify);
        return true;
    }

    private void removeNotificationItem()
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(TimerActivity.NOTIFICATION_ID);
    }

    private void writeEndTimeToPrefs()
    {
        writeToPrefs(PREFS_VAL_ENDTIME, mEndTimeMS);
    }

    private void writeToPrefs(String pref, int value)
    {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(pref, value);
        editor.commit();
    }

    private void writeToPrefs(String pref, long value)
    {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(pref, value);
        editor.commit();
    }

    private void startTickThread()
    {
        mTickThread = new TickThread();
        mTickThread.start();
    }

    private void stopTickThread()
    {
        if (mTickThread != null)
        {
            mTickThread.quit();
            mTickThread = null;
        }
    }

    private void showStopBuzzerButton()
    {
        /* TODO: */
    }

    private void fadeOutBackground()
    {
        mMainBG.startAnimation(mFadeOutSlightAnim);
    }

    private void fadeInBackground()
    {
        mMainBG.startAnimation(mFadeInSlightAnim);
    }

    /*
     * private void updateTimeViews() { //if (!mCountdownActive) // return;
     * //////
     * 
     * int timeLeft = 0; final long now = System.currentTimeMillis(); if
     * (mEndTimeMS > now) timeLeft = (int)(mEndTimeMS-now)/1000;
     * 
     * mDial.setSecsRemaining(timeLeft); mDial.invalidate();
     * 
     * setDigitalTimeTo((int)timeLeft); }
     */

    private boolean needToUpload()
    {
        // Log.d("mtimer","upload stats is " + mUploadStats);
        if (!mUploadStats) return false;

        return (System.currentTimeMillis() > (mLastStatsUpload + UPLOAD_EVERY));
    }

    private void startNetThread()
    {
        // only do this every 7 days
        if (!needToUpload())
        {
            return;
        }

        // Log.d("mtimer","uploading");

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                long start = SystemClock.uptimeMillis();
                NetworkInfo.State netState = NetTools.getConnectivityState(TimerActivity.this);

                // wait for connected
                while (netState != NetworkInfo.State.CONNECTED)
                {
                    // wait 10 seconds then give up
                    if ((SystemClock.uptimeMillis() - start) > 10000) return;
                    // /////

                    try
                    {
                        sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                    }

                    if (!mThreadRun) return;

                    netState = NetTools.getConnectivityState(TimerActivity.this);
                }

                {
                    Message msg = new Message();
                    msg.what = 22;
                    netHandler.sendMessage(msg);
                }

                // CONNECTED
                int r = postDeviceDetails(deviceId, deviceModel, deviceOsVersion, mInstallDate, mAppVersion,
                        mUsageCount, mAvTimeLen, mClickedMMSCount);

                // Log.d("mtimer","post returned");

                {
                    Message msg = new Message();
                    msg.what = r;
                    netHandler.sendMessage(msg);
                }
            }
        };

        t.start();
    }

    private static final int ERROR_NET_SUCCESS = 0;
    private static final int ERROR_NET_UNKNOWN = 1;
    private static final int ERROR_NET_NO_ROUTE = 2;

    protected static int postDeviceDetails(String deviceId, String modelID, String osVersion, String installDate,
            int versionCode, int usageCount, int avTimeLen, int clickedMMSCount)
    {
        int res = 0;

        int caughtError = -1;

        // create the list containing the vars
        List<NameValuePair> vars = new ArrayList<NameValuePair>(6);
        vars.add(new BasicNameValuePair("deviceid", deviceId));
        vars.add(new BasicNameValuePair("modelid", modelID));
        vars.add(new BasicNameValuePair("osv", osVersion));
        vars.add(new BasicNameValuePair("id", installDate));
        vars.add(new BasicNameValuePair("uc", "" + usageCount));
        vars.add(new BasicNameValuePair("atl", "" + avTimeLen));
        vars.add(new BasicNameValuePair("cmmsc", "" + clickedMMSCount));
        vars.add(new BasicNameValuePair("v", "" + versionCode));

        try
        {
            HttpResponse resp = HttpTools.doPost(STATS_URL, vars);
            if (resp != null)
            {
                int httpCode = resp.getStatusLine().getStatusCode();

                if (httpCode == HttpURLConnection.HTTP_OK)
                {
                    // save the data
                    // String content = EntityUtils.toString(resp.getEntity());
                    res = ERROR_NET_SUCCESS;
                }
                else
                {
                    res = ERROR_NET_UNKNOWN;
                }
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            caughtError = ERROR_NET_NO_ROUTE;
        }

        // caught error?
        if (caughtError != -1) res = caughtError;

        return res;
    }

    protected class TickThread extends Thread
    {
        private boolean mRun;
        private boolean mPaused;

        @Override
        public void start()
        {
            mPaused = false;
            super.start();
        }

        @Override
        public void run()
        {
            mRun = true;
            mPaused = false;

            while (mRun)
            {
                try
                {
                    sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (!mPaused)
                {
                    tickHandler.sendMessage(new Message());
                }
            }
        }

        public void quit()
        {
            mRun = false;
        }

        public void pause()
        {
            mPaused = true;
        }

        public void unpause()
        {
            mPaused = false;
        }
    }
}
