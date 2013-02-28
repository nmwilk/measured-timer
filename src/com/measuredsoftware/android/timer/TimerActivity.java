package com.measuredsoftware.android.timer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.measuredsoftware.android.timer.TimerView.OnEventListener;
import com.measuredsoftware.android.library2.utils.DateTools;
import com.measuredsoftware.android.library2.utils.NetTools;
import com.measuredsoftware.android.library2.utils.http.HttpTools;

public class TimerActivity extends Activity implements TimerView.OnEventListener {
  public static final String ALARM_ACTION         = "com.measuredsoftware.android.countdown.ALARM";
  
  public static final String FONT_STRING          = "creative.ttf";
  
  public static final int    ALARM_ID             = 103494;

  public static final int MENU_OTHER_SW               = 0;
  public static final int MENU_PREFS                  = 1;
  public static final int MENU_HELP                   = 2;

  public static final int NOTIFICATION_ID             = 1;

  private static final int DIALOG_HELP                = 1;
  
  private static final String  COLOUR_ORANGE          = "#fff97f04";
  private static final String  COLOUR_DGREY           = "#ff777777";
  private static final String  COLOUR_MGREY           = "#ffbbbbbb";
  private static final String  COLOUR_ENDTIME_ACTIVE  = "#ffefefef";
  
  public static final boolean  DEBUG_MODE_SMALLTIME      = false;
  public static final boolean  DEBUG_MODE_UPLOAD_ALWAYS  = false;
  public static final boolean  DEBUG_MODE_WELCOME_ALWAYS = false;
  
  private static final long    DAY_MS                   = 24*60*60*1000;
  private static final long    UPLOAD_EVERY             = 5*DAY_MS;
  
  public static int N_COLOUR_ORANGE;
  public static int N_COLOUR_DGREY;
  public static int N_COLOUR_MGREY;
  public static int N_COLOUR_ENDTIME_ACTIVE;

  public static String  DEVICE_ID;
  public static String  DEVICE_MODEL;
  public static String  DEVICE_OS_VERSION;
  
  public static TimerActivity mCurrentActivity;
  
  public final static int DEFAULT_SCREEN_RES_X = 480;
  public final static int DEFAULT_SCREEN_RES_Y = 320;
  public static final String INTENT_VAR_ALARM_RINGING = "alarmringing";
  public static final String INTENT_VAR_DEVICE_ASLEEP = "deviceasleep";
  public static final String INTENT_SCREEN_OFF        = "screenoff";

  public static final String PREFS_LOC                   = "measuredtimer";
  public static final String PREFS_VAL_SENDSTATS         = "sendstats";
  public static final String PREFS_VAL_USE_NOTIFICATIONS = "usenotifications";
  private static final String PREFS_VAL_ENDTIME          = "endtime";
  private static final String PREFS_VAL_INSTALLDATE      = "installdate";
  private static final String PREFS_VAL_CLICKEDMMS       = "clickedmms"; // have they clicked on mms?
  private static final String PREFS_VAL_AVTIMELEN        = "avtimelen";  // average length of time they set timers for
  private static final String PREFS_VAL_USAGECOUNT       = "timercount"; // how many times have they set a timer?
  private static final String PREFS_VAL_LASTUPLOAD       = "laststatsupload";
  
  private static final String STATS_URL                 = "https://www.measuredsoftware.co.uk/timer/anonstats.php";
  
  private static String       CANCEL;
  private static String       STOP;
  private static String       NOTIFICATION_TITLE;
  private static String       NOTIFICATION_EXTITLE;
  private static String       NOTIFICATION_EXDESC;
  
  public static Typeface mFont;
  
  private TimerView mDial;
  private TextView mCancelButton;
  private ImageView mMainBG;
  private OnClickListener mCancelOnClickListener;
  private OnTouchListener mCancelOnTouchListener;
  
  private Animation mFadeInAnim;
  private Animation mFadeOutAnim;
  private Animation mFadeInSlightAnim;
  private Animation mFadeOutSlightAnim;
  
  //private SoundPool mSoundPool;
  //private AudioManager mAudioManager;
  //private int mSoundClickId;
  //private boolean mSoundOn;
  
  private TickThread mTickThread = null;
  
  private boolean mAlarmRinging;
  private boolean mDeviceAsleep;
  
  private SharedPreferences mPrefs;
  private long              mEndTimeMS;
  
  private boolean mStartedByIntent;
  
  private boolean mFirstChange;  // first change of timer since DOWN action 
  
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      mDial.updateTime();
    }
  };

  private Handler mNetHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      final int code = msg.what;
      try {
        switch(code) {
          case ERROR_NET_SUCCESS:
            mLastStatsUpload = System.currentTimeMillis();
            writeToPrefs(PREFS_VAL_LASTUPLOAD, mLastStatsUpload);
            break;
        } 
      }catch (Exception e) {
      }
    }
  };
  
  // stats vars from prefs
  private String  mInstallDate;
  private int     mClickedMMSCount;
  private int     mAvTimeLen;
  private int     mUsageCount;
  private long    mLastStatsUpload;
  private boolean mUploadStats;
  private boolean mShowNotification;
  
  private boolean mFirstRun = false;
  
  private boolean mThreadRun;

  private AnimationListener mResetCancelButton;
  
  public static int mAppVersion;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Window win = getWindow();
    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
                 WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                 //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   | 
                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   |
                 WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    
    PackageInfo pInfo = null;
    try {
      pInfo = getPackageManager().getPackageInfo("com.measuredsoftware.android.timer", PackageManager.GET_META_DATA);
      mAppVersion = pInfo.versionCode;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    
    DEVICE_ID         = Secure.getString(getContentResolver(), Secure.ANDROID_ID);     
    DEVICE_MODEL      = Build.MODEL.toLowerCase();
    DEVICE_OS_VERSION = Build.VERSION.SDK;
    
    CANCEL = getString(R.string.cancel);
    STOP   = getString(R.string.stop);
    NOTIFICATION_TITLE = getString(R.string.notification_title);
    NOTIFICATION_EXTITLE = getString(R.string.notification_extitle);
    NOTIFICATION_EXDESC = getString(R.string.notification_exdesc);
    
    N_COLOUR_MGREY          = Color.parseColor(COLOUR_MGREY);
    N_COLOUR_DGREY          = Color.parseColor(COLOUR_DGREY);
    N_COLOUR_ORANGE         = Color.parseColor(COLOUR_ORANGE);
    N_COLOUR_ENDTIME_ACTIVE = Color.parseColor(COLOUR_ENDTIME_ACTIVE);

    mCurrentActivity = this;
    mStartedByIntent = false;

    mAlarmRinging = false;
    Intent intent = getIntent();
    if (intent != null)
      mAlarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
    
    mFont = Typeface.createFromAsset(this.getAssets(), TimerActivity.FONT_STRING);
    
    setTitle(R.string.title_bar);
    
    int layout = R.layout.main;
    setContentView(layout);
    
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    
    int defaultWidth = DEFAULT_SCREEN_RES_X;
    int defaultHeight = DEFAULT_SCREEN_RES_Y;
    if (dm.widthPixels != defaultWidth) {
      float ratio =((float)dm.widthPixels) / dm.heightPixels;
      defaultWidth = (int)(defaultHeight * ratio);
    }
    
    // create animations
    mFadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);    
    mFadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);    
    mFadeInSlightAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slight);    
    mFadeOutSlightAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out_slight);    

    mCancelButton = (TextView)findViewById(R.id.cancel_button);
    mCancelOnClickListener = new OnClickListener() {

      @Override
      public void onClick(View v) {
        mEndTimeMS = 0;
        mDial.setEndTime(0);
        hideCancelButton();
        boolean shutdown = false;
        if (mAlarmRinging) {
          if (mStartedByIntent)
            shutdown = true;
        }
        stopAlarmRinging();
        writeEndTimeToPrefs();
        
        if (shutdown)
          shutdown();
      }
      
    };    

    mCancelOnTouchListener = new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          mCancelButton.setTextColor(N_COLOUR_ENDTIME_ACTIVE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          mCancelButton.setTextColor(N_COLOUR_MGREY);
        }
        
        return false;
      }
      
    };
    
    mResetCancelButton = new AnimationListener() {

      @Override
      public void onAnimationEnd(Animation animation) {
        mCancelButton.setTextColor(N_COLOUR_MGREY);
        mCancelButton.setText(CANCEL);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationStart(Animation animation) {
      }
      
    };
   

    mCancelButton.setOnTouchListener(mCancelOnTouchListener);
    mCancelButton.setOnClickListener(mCancelOnClickListener);
    mCancelButton.setTypeface(mFont);
    mCancelButton.setTextSize(45);
    mCancelButton.setTextColor(N_COLOUR_MGREY);
    mCancelButton.setVisibility(View.INVISIBLE);
    
    mMainBG = (ImageView)findViewById(R.id.back);
    
    mDial = (TimerView)findViewById(R.id.the_dial);
    mDial.setOnSetValueChangedListener(this);
    
    mTickThread = null;

    mFirstChange = true;
    
    //mPrefs = getSharedPreferences(PREFS_LOC, MODE_PRIVATE);
    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    mEndTimeMS        = mPrefs.getLong(PREFS_VAL_ENDTIME, 0);
    
    // stats
    mInstallDate      = mPrefs.getString(PREFS_VAL_INSTALLDATE, "");
    mLastStatsUpload  = mPrefs.getLong(PREFS_VAL_LASTUPLOAD, System.currentTimeMillis());
    mClickedMMSCount  = mPrefs.getInt(PREFS_VAL_CLICKEDMMS, 0);
    mAvTimeLen        = mPrefs.getInt(PREFS_VAL_AVTIMELEN, 0);
    mUsageCount       = mPrefs.getInt(PREFS_VAL_USAGECOUNT, 0);
    
    loadUserPrefs();
    
    if (mInstallDate.length() == 0) {
      mFirstRun = true;
      SharedPreferences.Editor editor = mPrefs.edit();
      mInstallDate = DateTools.getYYYYMMDD();
      mLastStatsUpload = System.currentTimeMillis()-(4*DAY_MS);
      editor.putString(PREFS_VAL_INSTALLDATE, mInstallDate);
      editor.putLong(PREFS_VAL_LASTUPLOAD,  mLastStatsUpload);
      editor.commit();
    }
    
    if (DEBUG_MODE_UPLOAD_ALWAYS)
      mLastStatsUpload = 0;

    //mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);    
    //mSoundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0);
    
    /*AssetFileDescriptor afdClick;
    
    try {
      afdClick = getAssets().openFd( "click.ogg" );
      mSoundClickId = mSoundPool.load(afdClick, 1);
    } catch (IOException e) {
    }*/
  }
  
  private void loadUserPrefs() {
    mUploadStats      = mPrefs.getBoolean(PREFS_VAL_SENDSTATS, true);
    mShowNotification = mPrefs.getBoolean(PREFS_VAL_USE_NOTIFICATIONS, true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mAlarmRinging) {
      mEndTimeMS = 0;
      mDial.setEndTime(0);      
      hideCancelButton();
      stopAlarmRinging();
      writeEndTimeToPrefs();
    }
    mCurrentActivity = null;
  }

  @Override
  protected void onPause() {
    super.onPause();

    stopTickThread();
    mThreadRun = false;

    mStartedByIntent = false;
  }

  @Override
  protected void onStart() {
    super.onStart();
    //Log.d("mtimer","onStart");
  }
  
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    
    mAlarmRinging = intent.getBooleanExtra(INTENT_VAR_ALARM_RINGING, false);
    mDeviceAsleep = intent.getBooleanExtra(INTENT_VAR_DEVICE_ASLEEP, false);
    intent.removeExtra(INTENT_VAR_ALARM_RINGING);
    intent.removeExtra(INTENT_VAR_DEVICE_ASLEEP);
    
    mStartedByIntent = mDeviceAsleep;
    //Log.d("mtimer","onNewIntent: alarmRinging " + mAlarmRinging);
  }

  @Override
  protected void onResume() {
    super.onResume();

    startTickThread();
    
    mThreadRun = true;

    if (mAlarmRinging && mEndTimeMS > 0) {
      showStopBuzzerButton();
      mDial.setAlarmRinging(true);
    }
    
    int now = (int)(System.currentTimeMillis()/1000);

    if (mEndTimeMS > now) {
      // if there was a saved timer and it not expired, start it
      mDial.setEndTime(mEndTimeMS);
      showCancelButton();
    } else if (mEndTimeMS == 0) {
      hideCancelButton();
      mDial.setEndTime(0);
    }
    
    
    startNetThread();
    
    if (mFirstRun || DEBUG_MODE_WELCOME_ALWAYS) {
      showDialog(TimerActivity.DIALOG_HELP);
      mFirstRun = false;
    }
  }
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) 
  {
    return super.onPrepareOptionsMenu(menu);
  }  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    boolean bRet = super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_OTHER_SW, 0, R.string.othersw);
    menu.add(1, MENU_PREFS,    0, R.string.prefs);
    menu.add(2, MENU_HELP,    0, R.string.help);
    return bRet;
  }
  
  private static final int SHOW_PREFERENCES = 1;
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    switch(item.getItemId())
    {
      case MENU_OTHER_SW: {
        writeToPrefs(TimerActivity.PREFS_VAL_CLICKEDMMS, ++mClickedMMSCount);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.otherswlink)));
  
        startActivity(intent);
        
        return true;
      }
      case MENU_PREFS: {
        Intent intent = new Intent(this, TimerPrefs.class);
        startActivityForResult(intent, SHOW_PREFERENCES);
        return true;
      }
      case MENU_HELP: {
        showDialog(DIALOG_HELP);
        return true;
      }
    }
    return super.onOptionsItemSelected(item);
  }    
  
  
  
  @Override
  public Dialog onCreateDialog(int nDialogType)
  {
    Dialog dialog = null;
    switch(nDialogType) {
      case TimerActivity.DIALOG_HELP:
        dialog = new HelpDialog(this); 
        break;
    }
    return dialog;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SHOW_PREFERENCES) {
      boolean lastSN = this.mShowNotification;
      this.loadUserPrefs();
      if (!mShowNotification)
        removeNotificationItem();
      else if (!lastSN && mShowNotification && this.mEndTimeMS > 0)
        createNotificationItem();        
    }
  }
  
  @Override
  public void onEvent(int nResult, int value) {
    switch(nResult) {
      case OnEventListener.RESULT_VALUE_CHANGE:
        if (mFirstChange) {
          this.fadeOutBackground();
          mFirstChange = false;
        }
        break;
      case OnEventListener.RESULT_START:
        mFirstChange = true;
        this.fadeInBackground();
        if (value > 0) {
          showCancelButton();
          mEndTimeMS = System.currentTimeMillis()+(value*1000);
          setupAlarm(mEndTimeMS);
          ++mUsageCount;
          int timerLenSecs = (int)(mEndTimeMS-System.currentTimeMillis())/1000;
          mAvTimeLen = ((mAvTimeLen+timerLenSecs)/mUsageCount);
          SharedPreferences.Editor editor = mPrefs.edit();
          editor.putInt(PREFS_VAL_AVTIMELEN, mAvTimeLen);
          editor.putInt(PREFS_VAL_USAGECOUNT, mUsageCount);
        } else {
          mEndTimeMS = 0;
          Alarms.disableAlert(this);
          removeNotificationItem();
        }
        writeEndTimeToPrefs();
        break;
      case OnEventListener.RESULT_CANCEL:
        mFirstChange = true;
        hideCancelButton();
        writeEndTimeToPrefs();
        break;
    }
  }
  
  private void setupAlarm(long endTime) {
    Alarms.enableAlert(this, endTime);
    createNotificationItem();
  }

  private void stopAlarmRinging() {
    removeNotificationItem();
    Alarms.disableAlert(this);
    mAlarmRinging = false;
    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    nm.cancel(ALARM_ID);
    stopService(new Intent(Alarms.ALARM_ALERT_ACTION));
    mDial.setAlarmRinging(false);
  }
  
  private boolean createNotificationItem() {
    if (!this.mShowNotification)
      return false;
    
    NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notify = new Notification(R.drawable.statusicon, NOTIFICATION_TITLE, System.currentTimeMillis());
    Intent intent = new Intent(this, TimerActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP );
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
    notify.setLatestEventInfo(this, NOTIFICATION_EXTITLE, NOTIFICATION_EXDESC, pi);
    
    nm.notify(TimerActivity.NOTIFICATION_ID, notify);
    return true;
  }

  private void removeNotificationItem() {
    NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancel(TimerActivity.NOTIFICATION_ID);
  }
  
  private void shutdown() {
    finish();
  }
  
  private void writeEndTimeToPrefs() {
    SharedPreferences.Editor editor = mPrefs.edit();
    editor.putLong(PREFS_VAL_ENDTIME, mEndTimeMS);
    editor.commit();
  }
  
  private void writeToPrefs(String pref, int value) {
    SharedPreferences.Editor editor = mPrefs.edit();
    editor.putInt(pref, value);
    editor.commit();
  }
  
  private void writeToPrefs(String pref, long value) {
    SharedPreferences.Editor editor = mPrefs.edit();
    editor.putLong(pref, value);
    editor.commit();
  }
  
  private void startTickThread() {
    mTickThread = new TickThread();
    mTickThread.start();
  }
  
  private void stopTickThread() {
    if (mTickThread != null) {
      mTickThread.quit();
      mTickThread = null;
    }
  }
    
  private void showStopBuzzerButton() {
    mCancelButton.setText(STOP);
    mCancelButton.setTextColor(N_COLOUR_ORANGE);
    showCancelButton();
  }
  
  private void fadeOutBackground() {
    mMainBG.startAnimation(mFadeOutSlightAnim);
  }

  private void fadeInBackground() {
    mMainBG.startAnimation(mFadeInSlightAnim);
  }

  private void showCancelButton() {
    if (mCancelButton.getVisibility() != View.INVISIBLE)
      return;
    mCancelButton.startAnimation(mFadeInAnim);
    mCancelButton.setVisibility(View.VISIBLE);
  }

  private void hideCancelButton() {
    if (mCancelButton.getVisibility() != View.VISIBLE)
      return;
    mFadeOutAnim.setAnimationListener(mResetCancelButton);
    mCancelButton.startAnimation(mFadeOutAnim);
    mCancelButton.setVisibility(View.INVISIBLE);    
  }

/*  private void updateTimeViews() {
    //if (!mCountdownActive)
    //  return;
      //////

    int timeLeft = 0;
    final long now = System.currentTimeMillis();
    if (mEndTimeMS > now)
      timeLeft = (int)(mEndTimeMS-now)/1000;
    
    mDial.setSecsRemaining(timeLeft);
    mDial.invalidate();
    
    setDigitalTimeTo((int)timeLeft);
  }*/

  private boolean needToUpload() {
//    Log.d("mtimer","upload stats is " + mUploadStats);
    if (!mUploadStats)
      return false;     
    
    return (System.currentTimeMillis() > (mLastStatsUpload+UPLOAD_EVERY));
  }
  
  private void startNetThread() {
    // only do this every 7 days
    if (!needToUpload()) {
      return;
    }

//    Log.d("mtimer","uploading");
    
    Thread t = new Thread() {
      @Override
      public void run() {
        long start = SystemClock.uptimeMillis(); 
        NetworkInfo.State netState = NetTools.getConnectivityState(TimerActivity.this);
        
        // wait for connected
        while(netState != NetworkInfo.State.CONNECTED) {
          // wait 10 seconds then give up
          if ((SystemClock.uptimeMillis()-start) > 10000)
            return;
            ///////
          
          try {
            sleep(100);
          } catch (InterruptedException e) {
          }
          
          if (!mThreadRun)
            return;
          
          netState = NetTools.getConnectivityState(TimerActivity.this);
        }

        {
          Message msg = new Message();
          msg.what = 22;
          mNetHandler.sendMessage(msg);
        }
        
        // CONNECTED
        int r = postDeviceDetails(DEVICE_ID, DEVICE_MODEL, DEVICE_OS_VERSION, mInstallDate, mAppVersion, mUsageCount, mAvTimeLen, mClickedMMSCount);

        //Log.d("mtimer","post returned");

        {
          Message msg = new Message();
          msg.what = r;
          mNetHandler.sendMessage(msg);
        }
      }
    };
    
    t.start();
  }
  
  private static final int ERROR_NET_SUCCESS  = 0;
  private static final int ERROR_NET_UNKNOWN  = 1;
  private static final int ERROR_NET_NO_ROUTE = 2;
  
  public static int postDeviceDetails(String deviceId, String modelID, String osVersion, String installDate, int versionCode, int usageCount, int avTimeLen, int clickedMMSCount) {
    int res = 0;
    
    int caughtError = -1;
    
    // create the list containing the vars
    List<NameValuePair> vars = new ArrayList<NameValuePair>(6);
    vars.add(new BasicNameValuePair("deviceid",deviceId));
    vars.add(new BasicNameValuePair("modelid",modelID));
    vars.add(new BasicNameValuePair("osv",osVersion));
    vars.add(new BasicNameValuePair("id",installDate));
    vars.add(new BasicNameValuePair("uc",""+usageCount));
    vars.add(new BasicNameValuePair("atl",""+avTimeLen));
    vars.add(new BasicNameValuePair("cmmsc",""+clickedMMSCount));
    vars.add(new BasicNameValuePair("v",""+versionCode));
    
    try {
      HttpResponse resp = HttpTools.doPost(STATS_URL, vars);
      if (resp != null) {
        int httpCode = resp.getStatusLine().getStatusCode();

        if (httpCode == HttpURLConnection.HTTP_OK) {
          // save the data
          //String content = EntityUtils.toString(resp.getEntity());
          res = ERROR_NET_SUCCESS;
        } else {
          res = ERROR_NET_UNKNOWN;
        }
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
      caughtError = ERROR_NET_NO_ROUTE;
    }

    // caught error?
    if (caughtError != -1)
      res = caughtError;
    
    return res;
  }      
  
  public class TickThread extends Thread {
    private boolean mRun; 
    private boolean mPaused; 
    
    @Override
    public void start() {
      mPaused = false;
      super.start();
    }
    
    @Override
    public void run() {
      mRun = true;
      mPaused = false;
      
      while(mRun) {
        try {
          sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
        if (!mPaused) {
          //Log.d("mtimer","Thread: tick " + System.currentTimeMillis());
        
          mHandler.sendMessage(new Message());
        }
      }
    }
    
    public void quit() {
      mRun = false;
    }
    
    public void pause() {
      mPaused = true;
    }

    public void unpause() {
      mPaused = false;
    }
  }
}
