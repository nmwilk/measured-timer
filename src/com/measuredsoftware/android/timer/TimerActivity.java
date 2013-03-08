package com.measuredsoftware.android.timer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
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
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.measuredsoftware.android.library2.utils.DateTools;
import com.measuredsoftware.android.library2.utils.NetTools;
import com.measuredsoftware.android.library2.utils.http.HttpTools;
import com.measuredsoftware.android.timer.data.EndTimes;
import com.measuredsoftware.android.timer.data.EndTimes.Alarm;
import com.measuredsoftware.android.timer.views.ActiveTimerListView;
import com.measuredsoftware.android.timer.views.ActiveTimerListView.LayoutListener;
import com.measuredsoftware.android.timer.views.ActiveTimerView;
import com.measuredsoftware.android.timer.views.StopButton;
import com.measuredsoftware.android.timer.views.TimerView;
import com.measuredsoftware.android.timer.views.TopBar;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Main activity for app.
 * 
 * @author neil
 * 
 */
public class TimerActivity extends Activity implements TimerView.OnEventListener, View.OnClickListener, Colourable,
        OnSeekBarChangeListener, OnDismissListener
{
    /** the alarm ringing variable name for the intent */
    public static final String INTENT_VAR_ALARM_RINGING = "alarmringing";

    /** the device asleep variable name for the intent */
    public static final String INTENT_VAR_DEVICE_ASLEEP = "deviceasleep";

    private static final int NOTIFICATION_ID = 1;

    private static final long DAY_MS = 24 * 60 * 60 * 1000;
    private static final long UPLOAD_EVERY = 5 * DAY_MS;

    private static String deviceId;
    private static String deviceModel;
    private static int deviceOsVersion;

    private static final String PREFS_VAL_SENDSTATS = "sendstats";
    private static final String PREFS_VAL_USE_NOTIFICATIONS = "usenotifications";

    private static final String PREFS_VAL_ENDTIMES = "endtimes";
    private static final String PREFS_VAL_INSTALLDATE = "installdate";
    private static final String PREFS_VAL_CLICKEDMMS = "clickedmms";

    /* average length of time they set timers for */
    private static final String PREFS_VAL_AVTIMELEN = "avtimelen";

    /* how many times have the set the timer */
    private static final String PREFS_VAL_USAGECOUNT = "timercount";

    private static final String PREFS_VAL_LASTUPLOAD = "laststatsupload";

    private static final String PREFS_VAL_HUE = "hue";

    private static final String STATS_URL = "https://www.measuredsoftware.co.uk/timer/anonstats.php";

    private static String notificationTitle;
    private static String notificationExTitle;
    private static String notificationExDesc;

    private float currentHue;

    private TimerView dial;
    private ImageView mainBg;
    private ActiveTimerListView activeTimers;
    private StopButton stopButton;
    private TopBar topBar;
    private View hueButton;

    private final List<Colourable> colourableViews = new ArrayList<Colourable>();

    private Animation fadeInBackground;
    private Animation fadeOutBackground;

    private TickThread tickThread = null;

    private HueChooser hueChooser;

    private boolean alarmRinging;
    private boolean startedByIntent;
    private boolean deviceAsleep;

    private boolean spaceInList = true;

    private SharedPreferences prefs;
    private final EndTimes endTimes = new EndTimes();

    private boolean firstChange; // first change of timer since DOWN action

    private static class TickHandler extends Handler
    {
        private final WeakReference<TimerActivity> parent;

        public TickHandler(final TimerActivity parent)
        {
            this.parent = new WeakReference<TimerActivity>(parent);
        }

        @Override
        public void handleMessage(Message msg)
        {
            parent.get().getDial().updateNowTime();
            parent.get().getTimerList().tickAlarms();
        }
    }

    private static TickHandler tickHandler;

    private static class NetHandler extends Handler
    {
        private final WeakReference<TimerActivity> parent;

        public NetHandler(final TimerActivity parent)
        {
            this.parent = new WeakReference<TimerActivity>(parent);
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
                        parent.get().setLastStatsUpload(Globals.getTime());
                        break;
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    private NetHandler netHandler;
    
    private ObjectAnimator glowAnimation;
    private final Handler spareHandler = new Handler();

    // stats vars from prefs
    private String installDate;
    private int clickedMMSCount;
    private int avTimeLen;
    private int usageCount;
    private long lastStatsUpload;
    private boolean uploadStats;
    private boolean showNotification;

    private boolean threadRun;

    private static int appVersion;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Globals.init(getResources());

        tickHandler = new TickHandler(this);
        netHandler = new NetHandler(this);

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
            appVersion = pInfo.versionCode;
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

        deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        deviceModel = Build.MODEL.toLowerCase();
        deviceOsVersion = Build.VERSION.SDK_INT;

        notificationTitle = getString(R.string.notification_title);
        notificationExTitle = getString(R.string.notification_extitle);
        notificationExDesc = getString(R.string.notification_exdesc);

        alarmRinging = false;
        startedByIntent = false;

        Intent intent = getIntent();
        if (intent != null)
        {
            alarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
        }

        setContentView(R.layout.app);

        // create animations
        fadeInBackground = AnimationUtils.loadAnimation(this, R.anim.fade_in_slight);
        fadeOutBackground = AnimationUtils.loadAnimation(this, R.anim.fade_out_slight);

        mainBg = (ImageView) findViewById(R.id.back);

        dial = (TimerView) findViewById(R.id.the_dial);
        dial.setOnSetValueChangedListener(this);

        activeTimers = (ActiveTimerListView) findViewById(R.id.timer_list);

        activeTimers.setLayoutListener(new LayoutListener()
        {
            @Override
            public void wasLayedOut(final boolean layoutChanged)
            {
                checkRemainingSpaceInList(layoutChanged);
            }
        });

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnClickListener(this);

        hueButton = topBar.findViewById(R.id.hue_button);

        stopButton = (StopButton) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);

        tickThread = null;

        firstChange = true;

        // mPrefs = getSharedPreferences(PREFS_LOC, MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        readEndTimesMS();

        activeTimers.setCancelClickListener(this);
        activeTimers.setAlarms(endTimes);
        activeTimers.updateAlarms();

        // stats
        installDate = prefs.getString(PREFS_VAL_INSTALLDATE, "");
        lastStatsUpload = prefs.getLong(PREFS_VAL_LASTUPLOAD, Globals.getTime());
        clickedMMSCount = prefs.getInt(PREFS_VAL_CLICKEDMMS, 0);
        avTimeLen = prefs.getInt(PREFS_VAL_AVTIMELEN, 0);
        usageCount = prefs.getInt(PREFS_VAL_USAGECOUNT, 0);
        currentHue = prefs.getFloat(PREFS_VAL_HUE, 0.5f);

        loadUserPrefs();

        if (installDate.length() == 0)
        {
            final SharedPreferences.Editor editor = prefs.edit();
            installDate = DateTools.getYYYYMMDD();
            lastStatsUpload = Globals.getTime() - (4 * DAY_MS);
            editor.putString(PREFS_VAL_INSTALLDATE, installDate);
            editor.putLong(PREFS_VAL_LASTUPLOAD, lastStatsUpload);
            editor.commit();
        }

        buildColourableList((ViewGroup) findViewById(R.id.container_view));
        
        final DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        Log.d(Globals.TAG, "Display: " + dm.widthPixels + "x" + dm.heightPixels + " @ " + dm.density);
    }

    private void buildColourableList(final ViewGroup container)
    {
        for (int i = 0; i < container.getChildCount(); i++)
        {
            final View child = container.getChildAt(i);
            if (child instanceof Colourable)
            {
                colourableViews.add((Colourable) child);
            }

            if (child instanceof ViewGroup && !(child instanceof ActiveTimerListView))
            {
                buildColourableList((ViewGroup) child);
            }
        }
    }

    protected void checkRemainingSpaceInList(final boolean layoutChanged)
    {
        final int itemHeight = activeTimers.getTimerHeight();
        final int containerHeight = activeTimers.getTotalHeight();
        final int listHeight = activeTimers.getChildCount() * itemHeight;
        spaceInList = containerHeight == 0 ? true : (listHeight + itemHeight) < containerHeight;

        if (dial.isEnabled() != spaceInList)
        {
            dial.setEnabled(spaceInList);
        }
    }

    protected void setLastStatsUpload(final long currentTimeMillis)
    {
        lastStatsUpload = currentTimeMillis;
        writeToPrefs(PREFS_VAL_LASTUPLOAD, lastStatsUpload);
    }

    protected TimerView getDial()
    {
        return dial;
    }

    protected ActiveTimerListView getTimerList()
    {
        return activeTimers;
    }

    private void loadUserPrefs()
    {
        uploadStats = prefs.getBoolean(PREFS_VAL_SENDSTATS, true);
        showNotification = prefs.getBoolean(PREFS_VAL_USE_NOTIFICATIONS, true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (alarmRinging)
        {
            stopAlarmRinging();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        stopTickThread();
        threadRun = false;

        startedByIntent = false;
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        alarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
        deviceAsleep = intent.getBooleanExtra(INTENT_VAR_DEVICE_ASLEEP, false);
        
        Log.d(Globals.TAG, "onNewIntent: ar " + alarmRinging + ", da " + deviceAsleep);

        intent.removeExtra(INTENT_VAR_ALARM_RINGING);
        intent.removeExtra(INTENT_VAR_DEVICE_ASLEEP);

        startedByIntent = deviceAsleep;

        // if (alarmRinging)
        {
            showStopButton();
            dial.setAlarmIsRinging(alarmRinging);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.d(Globals.TAG, "onResume");

        onColourSet(currentHue);

        startTickThread();

        threadRun = true;

        if (alarmRinging)
        {
            showStopButton();
            dial.setAlarmIsRinging(true);
        }

        closeActiveHueChooser();

        dial.setEnabled(spaceInList);

        startNetThread();
        
        if (!alarmRinging)
        {
            final Runnable animator = new Runnable()
            {
                @Override
                public void run()
                {
                    glowAnimation = ObjectAnimator.ofFloat(dial, "dotAnimate", 0f, 1f);
                    glowAnimation.setDuration(3000);
                    glowAnimation.setRepeatMode(ObjectAnimator.RESTART);
                    glowAnimation.setRepeatCount(2);
                    glowAnimation.start();
                    dial.setGlowAnimation(glowAnimation);
                }
            };
            spareHandler.postDelayed(animator, 1000);
        }
    }

    private void closeActiveHueChooser()
    {
        if (hueChooser != null)
        {
            final HueChooser copy = hueChooser;
            hueChooser = null;
            copy.close();
        }

        hueButton.setSelected(false);
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
                writeToPrefs(TimerActivity.PREFS_VAL_CLICKEDMMS, ++clickedMMSCount);
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.otherswlink)));

                startActivity(intent);
                break;
            }
            case R.id.settings_button:
            {
                final Intent intent = new Intent(this, TimerPrefs.class);
                startActivityForResult(intent, SHOW_PREFERENCES_RESULT_CODE);
                break;
            }
            case R.id.hue_button:
                if (hueChooser == null)
                {
                    hueChooser = new HueChooser(topBar, currentHue, this, this);
                }
                else
                {
                    onDismiss();
                }
                break;
            case R.id.stop_button:
            {
                if (!alarmRinging)
                {
                    break;
                }
            }
            //$FALL-THROUGH$
            case R.id.active_timer_view:
            {
                hideStopButton();

                if (view instanceof ActiveTimerView)
                {
                    final Alarm alarm = ((ActiveTimerView) view).getAlarm();
                    alarm.ms = 0;
                }
                else
                {
                    final Collection<Alarm> expired = endTimes.getExpiredAlarms();
                    for (final Alarm alarm : expired)
                    {
                        alarm.ms = 0;
                    }
                }

                boolean shutdown = false;
                if (alarmRinging && startedByIntent)
                {
                    shutdown = true;
                }

                stopAlarmRinging();

                if (shutdown)
                {
                    finish();
                }
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
            final boolean lastSN = this.showNotification;
            this.loadUserPrefs();
            if (!showNotification)
            {
                removeNotificationItem(null);
            }
            else if (!lastSN && showNotification && endTimes.timersActive()) createNotificationItem(usageCount);
        }
    }

    @Override
    public void valueChanged(int angle)
    {
        if (firstChange)
        {
            this.fadeOutBackground();
            firstChange = false;
        }
    }

    @Override
    public void started(final int seconds)
    {
        firstChange = true;
        this.fadeInBackground();
        if (seconds > 0)
        {
            ++usageCount;

            final Long endTime;
            if (Globals.DEBUG_QUICK_TIME)
            {
                endTime = Long.valueOf(Globals.getTime() + (seconds * 30));
            }
            else
            {
                endTime = Long.valueOf(Globals.getTime() + (seconds * 1000));
            }

            endTimes.addEndTime(endTime, usageCount);
            setupAlarm(endTime, usageCount);

            final int timerLenSecs = (int) (endTime - Globals.getTime()) / 1000;
            avTimeLen = ((avTimeLen + timerLenSecs) / usageCount);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREFS_VAL_AVTIMELEN, avTimeLen);
            editor.putInt(PREFS_VAL_USAGECOUNT, usageCount);
            editor.commit();
        }
        // else
        // {
        // final Alarm lastAdded = endTimes.removeLast();
        // Alarms.disableAlert(this, lastAdded.uid);
        // removeNotificationItem(lastAdded.uid);
        // }

        updateAlarms();
    }

    @Override
    public void cancelled()
    {
        firstChange = true;
        updateAlarms();
    }

    private void updateAlarms()
    {
        writeEndTimesToPrefs();
        activeTimers.updateAlarms();
    }

    private void stopAlarmRinging()
    {
        final Collection<Alarm> expiredAlarms = endTimes.getExpiredAlarms();
        Alarms.disableExpiredAlerts(this, expiredAlarms);
        alarmRinging = false;
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        endTimes.removeExpiredTimers();

        if (endTimes.count() == 0)
        {
            removeNotificationItem(nm);
        }

        stopService(new Intent(Alarms.ALARM_ALERT_ACTION));
        dial.setAlarmIsRinging(false);

        updateAlarms();
    }

    private void setupAlarm(final long endTime, final int uidAlarm)
    {
        Alarms.enableAlert(this, endTime, uidAlarm);
        createNotificationItem(uidAlarm);
    }

    private boolean createNotificationItem(final int uidAlarm)
    {
        if (!this.showNotification) return false;

        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notify = new Notification(R.drawable.statusicon, notificationTitle, Globals.getTime());
        final Intent intent = new Intent(this, TimerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pi = PendingIntent.getActivity(this, uidAlarm, intent, 0);
        notify.setLatestEventInfo(this, notificationExTitle, notificationExDesc, pi);

        nm.notify(NOTIFICATION_ID, notify);
        return true;
    }

    private void removeNotificationItem(final NotificationManager nmanager)
    {
        final NotificationManager nm = (nmanager == null) ? (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)
                : nmanager;
        nm.cancel(NOTIFICATION_ID);
    }

    private void writeEndTimesToPrefs()
    {
        writeToPrefs(PREFS_VAL_ENDTIMES, endTimes.removeExpiredTimers().toString());
    }

    private void readEndTimesMS()
    {
        endTimes.load(prefs.getString(PREFS_VAL_ENDTIMES, ""));
    }

    private void writeToPrefs(final String pref, final int value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(pref, value);
        editor.commit();
    }

    private void writeFloatToPrefs(final String pref, final float value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(pref, value);
        editor.commit();
    }

    private void writeToPrefs(final String pref, final long value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(pref, value);
        editor.commit();
    }

    private void writeToPrefs(final String pref, final String value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(pref, value);
        editor.commit();
    }

    private void startTickThread()
    {
        if (tickThread == null)
        {
            tickThread = new TickThread();
            tickThread.start();
        }
    }

    private void stopTickThread()
    {
        if (tickThread != null)
        {
            tickThread.quit();
            tickThread = null;
        }
    }

    private void showStopButton()
    {
        if (stopButton.getVisibility() == View.INVISIBLE)
        {
            stopButton.setVisibility(View.VISIBLE);
            final ObjectAnimator fader = ObjectAnimator.ofFloat(stopButton, "alpha", 0f, 1f);
            fader.setDuration(100);
            fader.setInterpolator(new DecelerateInterpolator());
            fader.start();
        }
    }

    private void hideStopButton()
    {
        if (stopButton.getVisibility() == View.VISIBLE)
        {
            final ObjectAnimator fader = ObjectAnimator.ofFloat(stopButton, "alpha", 1f, 0f);
            fader.setDuration(100);
            fader.setInterpolator(new DecelerateInterpolator());
            fader.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    stopButton.setVisibility(View.INVISIBLE);
                }
            });
            fader.start();
        }
    }

    private void fadeOutBackground()
    {
        mainBg.startAnimation(fadeOutBackground);
    }

    private void fadeInBackground()
    {
        mainBg.startAnimation(fadeInBackground);
    }

    private boolean needToUpload()
    {
        if (!uploadStats) return false;

        return (Globals.getTime() > (lastStatsUpload + UPLOAD_EVERY));
    }

    private void startNetThread()
    {
        // only do this every 7 days
        if (!needToUpload())
        {
            return;
        }

        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                final long start = Globals.getTime();

                NetworkInfo.State netState = NetTools.getConnectivityState(TimerActivity.this);

                // wait for connected
                while (netState != NetworkInfo.State.CONNECTED)
                {
                    // wait 10 seconds then give up
                    if ((Globals.getTime() - start) > 10000) return;

                    try
                    {
                        sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                    }

                    if (!threadRun) return;

                    netState = NetTools.getConnectivityState(TimerActivity.this);
                }

                {
                    final Message msg = new Message();
                    msg.what = 22;
                    netHandler.sendMessage(msg);
                }

                // CONNECTED
                final int r = postDeviceDetails(deviceId, deviceModel, deviceOsVersion, installDate, appVersion,
                        usageCount, avTimeLen, clickedMMSCount);

                {
                    final Message msg = new Message();
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

    protected static int postDeviceDetails(final String deviceId, final String modelID, final int osVersion,
            final String installDate, final int versionCode, final int usageCount, final int avTimeLen,
            final int clickedMMSCount)
    {
        int res = 0;

        int caughtError = -1;

        // create the list containing the vars
        final List<NameValuePair> vars = new ArrayList<NameValuePair>(6);
        vars.add(new BasicNameValuePair("deviceid", deviceId));
        vars.add(new BasicNameValuePair("modelid", modelID));
        vars.add(new BasicNameValuePair("osv", String.valueOf(osVersion)));
        vars.add(new BasicNameValuePair("id", installDate));
        vars.add(new BasicNameValuePair("uc", String.valueOf(usageCount)));
        vars.add(new BasicNameValuePair("atl", String.valueOf(avTimeLen)));
        vars.add(new BasicNameValuePair("cmmsc", String.valueOf(clickedMMSCount)));
        vars.add(new BasicNameValuePair("v", String.valueOf(versionCode)));

        try
        {
            final HttpResponse resp = HttpTools.doPost(STATS_URL, vars);
            if (resp != null)
            {
                final int httpCode = resp.getStatusLine().getStatusCode();

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
        catch (final ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (final IOException e)
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
                catch (final InterruptedException e)
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

    @Override
    public void onColourSet(final float colour)
    {
        for (final Colourable view : colourableViews)
        {
            view.onColourSet(colour);
        }
    }

    @Override
    public void onDismiss()
    {
        hueButton.setSelected(false);
        hueChooser = null;
    }

    private int lastProgress = -1;
    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
    {
        if (lastProgress != progress)
        {
            lastProgress = progress;
            currentHue = progress / (float) HueChooser.SEEK_MAX;
            onColourSet(currentHue);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar)
    {
        closeActiveHueChooser();
        writeFloatToPrefs(PREFS_VAL_HUE, currentHue);
    }
}
