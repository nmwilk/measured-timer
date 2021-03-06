package com.measuredsoftware.android.timer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.measuredsoftware.android.timer.data.EndTimes;
import com.measuredsoftware.android.timer.data.EndTimes.Alarm;
import com.measuredsoftware.android.timer.viewgroups.ContainerView;
import com.measuredsoftware.android.timer.views.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Main activity for app.
 *
 * @author neil
 */
public class TimerActivity extends Activity implements TimerView.OnEventListener, View.OnClickListener, Colourable,
        OnSeekBarChangeListener, OnDismissListener
{
    /**
     * the alarm ringing variable name for the intent
     */
    public static final String INTENT_VAR_ALARM_RINGING = "alarmringing";

    /**
     * the device asleep variable name for the intent
     */
    public static final String INTENT_VAR_DEVICE_ASLEEP = "deviceasleep";

    private static final int NOTIFICATION_ID = 1;

    private static final String PREFS_VAL_ENDTIMES = "endtimes";

    /* how many times have the set the timer */
    private static final String PREFS_VAL_USAGECOUNT = "timercount";

    private static final String PREFS_VAL_HUE = "hue";
    public static final int BACKGROUND_FADE_DURATION = 100;
    public static final float BACKGROUND_ALPHA_DIM = 0.75f;
    public static final float BACKGROUND_ALPHA_FULL = 1.0f;

    private static String notificationTitle;
    private static String notificationExTitle;
    private static String notificationExDesc;

    private float currentHue;
    private int usageCount;
    private boolean showNotification;

    private ContainerView contentContainer;
    private TimerView dial;
    private ActiveTimerListView activeTimers;
    private StopButton stopButton;
    private TopBar topBar;

    private View hueButton;

    private final List<Colourable> colourableViews = new ArrayList<Colourable>();

    private TickThread tickThread;

    private HueChooser hueChooser;
    private boolean alarmRinging;
    private boolean deviceWasAsleep;

    private boolean spaceInList = true;

    private SharedPreferences prefs;
    private final EndTimes endTimes = new EndTimes();

    private boolean firstChange; // first change of timer since DOWN action

    private static class TickHandler extends Handler
    {
        private WeakReference<TimerActivity> parent;

        public TickHandler(final TimerActivity parent)
        {
            this.parent = new WeakReference<TimerActivity>(parent);
        }

        public void stop()
        {
            this.parent = null;
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (parent != null && parent.get() != null)
            {
                parent.get().getDial().updateNowTime();
                parent.get().getTimerList().tickAlarms();
            }
        }
    }

    private static TickHandler tickHandler;

    private final Handler spareHandler = new Handler();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Globals.init(getResources());

        tickHandler = new TickHandler(this);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        notificationTitle = getString(R.string.notification_title);
        notificationExTitle = getString(R.string.notification_extitle);
        notificationExDesc = getString(R.string.notification_exdesc);

        alarmRinging = false;
        deviceWasAsleep = false;

        final Intent intent = getIntent();
        if (intent != null)
        {
            alarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
        }

        setContentView(R.layout.app);

        contentContainer = (ContainerView) findViewById(R.id.content_container);

        dial = contentContainer.getTimerView();
        dial.setOnSetValueChangedListener(this);
        dial.setDisabled(true, false);

        activeTimers = contentContainer.getListView();

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnClickListener(this);

        hueButton = topBar.findViewById(R.id.hue_button);

        final ViewGroup stopButtonContainer = (ViewGroup) contentContainer.getStopView();
        stopButton = (StopButton)stopButtonContainer.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);

        firstChange = true;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        readEndTimesMS();

        activeTimers.setCancelClickListener(this);
        activeTimers.setAlarms(endTimes);
        activeTimers.updateAlarms();

        currentHue = prefs.getFloat(PREFS_VAL_HUE, Globals.DEFAULT_HUE_VALUE);
        usageCount = prefs.getInt(PREFS_VAL_USAGECOUNT, 0);
        loadPrefsOptions();

        final ViewGroup containerView = (ViewGroup) findViewById(R.id.container_view);
        buildColourableList(containerView);
    }

    private void loadPrefsOptions()
    {
        showNotification = prefs.getBoolean(getResources().getString(R.string.prefs_key_usenotifications), true);
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

    protected void checkRemainingSpaceInList()
    {
        final int containerHeight = activeTimers.getMeasuredHeight();

        final int itemHeight = activeTimers.getTimerHeight();
        final int listHeight = activeTimers.getChildCount() * itemHeight;
        if (containerHeight == 0)
        {
            throw new RuntimeException("container height is zero.");
        }
        spaceInList = (listHeight + itemHeight) < containerHeight;
    }

    protected TimerView getDial()
    {
        return dial;
    }

    protected ActiveTimerListView getTimerList()
    {
        return activeTimers;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        tickHandler.stop();
        if (alarmRinging)
        {
            stopAlarmRinging();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        dial.stopGlowAnimation();

        deviceWasAsleep = false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        onColourSet(currentHue);

        // if the alarm's ringing, the flag was either set in onNewIntent (if Activity alive) or onCreate.
        if (alarmRinging)
        {
            showStopButton();
            dial.setAlarmIsRinging(true);
        }

        closeActiveHueChooser();

        if (!alarmRinging)
        {
            final Runnable animator = new Runnable()
            {
                @Override
                public void run()
                {
                    if (spaceInList)
                    {
                        final ObjectAnimator glowAnimation = ObjectAnimator.ofFloat(dial, "dotAnimate", 0f, 1f);
                        glowAnimation.setDuration(3000);
                        glowAnimation.setRepeatMode(ObjectAnimator.RESTART);
                        glowAnimation.setRepeatCount(2);
                        glowAnimation.start();
                        dial.setGlowAnimation(glowAnimation);
                    }
                }
            };
            spareHandler.postDelayed(animator, 1000);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        // called when activity is alive.
        alarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);

        deviceWasAsleep = intent.getBooleanExtra(INTENT_VAR_DEVICE_ASLEEP, false);

        intent.removeExtra(INTENT_VAR_ALARM_RINGING);
        intent.removeExtra(INTENT_VAR_DEVICE_ASLEEP);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
        {
            checkRemainingSpaceInList();
            spareHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (dial.isDisabled() == spaceInList)
                    {
                        dial.setDisabled(!spaceInList, true);
                    }
                }
            }, 250);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        startTickThread();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (alarmRinging)
        {
            stopAlarmRinging();
            hideStopButton();
        }

        stopTickThread();
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
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.otherswlink)));

                try
                {
                    startActivity(intent);
                }
                catch (final Exception e)
                {
                    // ignore.
                }
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
                    hueButton.setSelected(true);
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
                if (alarmRinging && deviceWasAsleep)
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
            loadPrefsOptions();

            if (!showNotification)
            {
                removeNotificationItem(null);
            }
        }
    }

    @Override
    public void valueChanged(int angle)
    {
        if (firstChange)
        {
            fadeBackground(false);
            firstChange = false;
        }
    }

    @Override
    public void started(final int seconds)
    {
        firstChange = true;
        fadeBackground(true);
        if (seconds > 0)
        {
            ++usageCount;

            final Long endTime;
            if (Globals.DEBUG_QUICK_TIME)
            {
                endTime = Globals.getTime() + (seconds * 30);
            }
            else
            {
                endTime = Globals.getTime() + (seconds * 1000);
            }

            endTimes.addEndTime(endTime, usageCount);
            setupAlarm(endTime, usageCount);

            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREFS_VAL_USAGECOUNT, usageCount);
            editor.apply();
        }

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
        checkRemainingSpaceInList();

        if (dial.isDisabled() == spaceInList)
        {
            dial.setDisabled(!spaceInList, true);
            dial.stopGlowAnimation();
        }
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

        stopService(new Intent(this, AlarmBuzzer.class));
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
        if (!this.showNotification)
        {
            return false;
        }

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

    private void writeFloatToPrefs(final String pref, final float value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(pref, value);
        editor.apply();
    }

    private void writeToPrefs(final String pref, final String value)
    {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(pref, value);
        editor.apply();
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

    private void fadeBackground(final boolean fadeIn)
    {
        final float from;
        final float to;

        if (fadeIn)
        {
            from = BACKGROUND_ALPHA_DIM;
            to = BACKGROUND_ALPHA_FULL;
        }
        else
        {
            from = BACKGROUND_ALPHA_FULL;
            to = BACKGROUND_ALPHA_DIM;
        }

        final ObjectAnimator animator = ObjectAnimator.ofFloat(this, "backgroundAlpha", from, to);
        animator.setDuration(BACKGROUND_FADE_DURATION);
        animator.start();
    }

    @SuppressWarnings("UnusedDeclaration") // used via property animator.
    public void setBackgroundAlpha(final float alpha)
    {
        contentContainer.getBackground().setAlpha((int) (255 * alpha));
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
