package com.measuredsoftware.android.timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.measuredsoftware.android.library2.utils.ImageTools;
import com.measuredsoftware.android.library2.utils.MathTools;

public class TimerView extends RotatableImageView {
  public static interface OnEventListener
  {
    public static final int RESULT_VALUE_CHANGE = 0;
    public static final int RESULT_START        = 1;
    public static final int RESULT_CANCEL       = 2;

    abstract void onEvent(int nResult, int value);
  }
  
  protected static final float mMaxTotalAngle = (360*72)-1;
  public static final String  TIME_FORMAT     = "%H:%M:%S";


  protected OnEventListener mListener;
  protected int mCurrentTimeSecs;
  
  // for formatting
  private Time mTimeLeft; 
  private Time mEndTime;
  
  private boolean mCountdownActive = false;
  
  private float mPrevAngle;
  private float mTotalAngle; // includes if we've looped around
  
  private Paint mTextLargeOrange;
  private Paint mTextSmallMGray;
  private Paint mTextSmallWhite;
  private Paint mTextLarge;
  private Paint mTextSmall;

  private String msCountdownTime;
  private String msEndTime;
  
  private long   mEndTimeMS;
  private int    mSecsLeftPrev;
  
  private boolean mSettingTime;
  
  private boolean mAlarmRinging;
  
  private final int TIMER_POS_X;
  private final int TIMER_POS_Y;
  private final int ENDTIME_POS_X;
  private final int ENDTIME_POS_Y;

  public TimerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    mListener = null;
    
    super.setIncrement(6);
    
    mEndTimeMS    = 0;
    mSecsLeftPrev = -1;
    mCurrentTimeSecs = 0;
    mSettingTime = false;
    mAlarmRinging = false;
    
    Drawable back = this.getBackground();
    Rect dialSize = new Rect(0,0,back.getIntrinsicWidth(), back.getIntrinsicHeight());
    
    mTextLargeOrange = new Paint();
    mTextLargeOrange.setTypeface(TimerActivity.mFont);
    mTextLargeOrange.setTextSize(Math.round(dialSize.right/9.1666667));
    mTextLargeOrange.setAntiAlias(true);
    mTextLargeOrange.setTextAlign(Align.CENTER);
    mTextLargeOrange.setColor(TimerActivity.N_COLOUR_ORANGE);    

    mTextSmallMGray = new Paint();
    mTextSmallMGray.setTypeface(TimerActivity.mFont);
    mTextSmallMGray.setTextSize(Math.round(dialSize.right/12.941));
    mTextSmallMGray.setAntiAlias(true);
    mTextSmallMGray.setTextAlign(Align.CENTER);
    mTextSmallMGray.setColor(TimerActivity.N_COLOUR_DGREY);    

    mTextSmallWhite = new Paint();
    mTextSmallWhite.setTypeface(TimerActivity.mFont);
    mTextSmallWhite.setTextSize(Math.round(dialSize.right/12.941));
    mTextSmallWhite.setAntiAlias(true);
    mTextSmallWhite.setTextAlign(Align.CENTER);
    mTextSmallWhite.setColor(TimerActivity.N_COLOUR_ENDTIME_ACTIVE);    
    
    mTextLarge = this.mTextLargeOrange;
    mTextSmall = this.mTextSmallMGray;
    
    TIMER_POS_X   = (dialSize.right/2);
    TIMER_POS_Y   = (dialSize.bottom/2);
    ENDTIME_POS_X = (dialSize.right/2);
    ENDTIME_POS_Y = (dialSize.bottom/2)+(dialSize.bottom/10);    

    mTimeLeft = new Time();
    mEndTime  = new Time();  

    mCountdownActive = false;

    // Set our bitmaps to 16-bit, 565 format.
    BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    
    int maskId = R.drawable.dial_mask_320;
    if (dialSize.right > 320)
      maskId = R.drawable.dial_mask_480;

    super.setIgnoreMask(ImageTools.loadBitmap(context, maskId, sBitmapOptions), true);
    
    setDigitalTimeTo(0);
  }
  
  public void setOnSetValueChangedListener(OnEventListener l) {
    mListener = l;
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // don't allow moving of timer if ringing
    if (mAlarmRinging)
      return true;
    
    boolean b = super.onTouchEvent(event);
    // parent handled it?
    if (b)
      return true;
    
    int msgRes = -1;
    int msgValue = -1;

    final int action = event.getAction();
    switch(action) {
      case MotionEvent.ACTION_DOWN:
//        mCurrentTimeSecs = 0;
        mCurrentTimeSecs = convertAngleToSecs(mTotalAngle); 
        msgRes = OnEventListener.RESULT_CANCEL;
        setCountdownActive(false);
        mSettingTime = true;
        mPrevAngle = mAngle;
        break;
      case MotionEvent.ACTION_UP:
        if (!mMultitouchActive) {
          mSettingTime = false;
          // start the timer
          msgRes = OnEventListener.RESULT_START;
          
          long endTime = 0;
          // set the end time
          if (mTotalAngle > 0) {
            msgValue = this.mCurrentTimeSecs;
            endTime = System.currentTimeMillis()+(msgValue*1000);
          }
          setEndTime(endTime);
          mCurrentTimeSecs = 0;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        // no change?
        if (mPrevAngle == mAngle)
          break;

        // calculate difference since last MOVE (complicated due to passing 359->1)
        final float angleDiff;
        // passed 359->1?
        if (mPrevAngle > 270 && (mAngle > 0 && mAngle < 90))
          angleDiff = (mAngle+360)-mPrevAngle;
        // passed 1>359?
        else if ((mPrevAngle >= 0 && mPrevAngle < 90) && mAngle > 270)
          angleDiff = mAngle-(mPrevAngle+360);
        else 
          angleDiff = mAngle-mPrevAngle;
        
        mTotalAngle += angleDiff;
        if (mTotalAngle > mMaxTotalAngle) {
          mTotalAngle = mMaxTotalAngle;
          mAngle = (mTotalAngle%360);
        } else if (mTotalAngle < 0) {
          mTotalAngle = 0;
          mAngle = 0;
        }

        mCurrentTimeSecs = convertAngleToSecs(mTotalAngle); 
        msgValue = mCurrentTimeSecs;
        msgRes   = OnEventListener.RESULT_VALUE_CHANGE;
        
        this.setDigitalTimeTo(mCurrentTimeSecs);
        
        mPrevAngle = mAngle;
        break;
    }    
    
    if (mListener != null && msgRes != -1)
      mListener.onEvent(msgRes, msgValue);
    
    return true;
  }
  
  public void setEndTime(long endTimeMS) {
    setCountdownActive(endTimeMS > 0);
    
    mEndTimeMS = endTimeMS;
    if (endTimeMS == 0) {
      mSecsLeftPrev = -1;
      setSecsRemaining(0);    
    } else {
      setSecsRemaining((int)(mEndTimeMS-System.currentTimeMillis())/1000);    
    }
    
    if (mCountdownActive) {
      this.setEndClockTo((int)((mEndTimeMS-System.currentTimeMillis())/1000));
    }
    
    updateTime();
  }
  
  // recalc time and invalidate if changed.
  public void updateTime() {
    if (!mCountdownActive) {
      this.setEndClockTo(mCurrentTimeSecs);
    }

    if (!mSettingTime) {
      int secsLeft = 0;
      if (mEndTimeMS > 0) 
        secsLeft = (int)((mEndTimeMS-System.currentTimeMillis())/1000);
      
      if (secsLeft < 0) {
        secsLeft = 0;
        mEndTimeMS = 0;
      }
  
      if (secsLeft != mSecsLeftPrev) {
        mSecsLeftPrev = secsLeft;
        setSecsRemaining(secsLeft);
      }
    }

    this.invalidate();
  }
  
  private void setCountdownActive(boolean b) {
    mCountdownActive = b;
    if (b) {
      setDigitalEndTimeActive();
    } else {
      setDigitalEndTimeInactive();
    }
  }
  
  protected int convertAngleToSecs(float angle) {
    final int time;

    if (TimerActivity.DEBUG_MODE_SMALLTIME)
      time = MathTools.roundToNearest((int)(angle),6);
    else
      time = MathTools.roundToNearest((int)(angle*10),60);
    
    return time;
  }

  protected float convertSecsToAngle(int time) {
    return (time/10);
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    // draw digital times
    canvas.drawText(msCountdownTime, TIMER_POS_X, TIMER_POS_Y, this.mTextLarge);
    canvas.drawText(msEndTime, ENDTIME_POS_X, ENDTIME_POS_Y, this.mTextSmall);
  }

  private void setSecsRemaining(int secs) {
    if (secs == 0) {      
      mAngle = 0;
      mTotalAngle = 0;
      mPrevAngle = 0;
    } else {
      mTotalAngle = convertSecsToAngle(secs);
      mPrevAngle = mTotalAngle;
      mAngle = mTotalAngle%360;
    }
    
    this.setDigitalTimeTo(secs);
  }

  private void setDigitalTimeTo(int value) {
    if (value < 0)
      value = 0;
    final int secs  = (value%60);
    final int mins  = (value/60)%60;
    final int hours = (value/3600);
    mTimeLeft.set(secs, mins, hours, 1, 1, 1999);
    setDigitalCountdown(mTimeLeft.format(TIME_FORMAT));
    
    if (!mCountdownActive) {
      mEndTime.set(System.currentTimeMillis()+(value*1000));
      setDigitalEndTime(mEndTime.format(TIME_FORMAT));
    }
  }
  
  public void setAlarmRinging(boolean b) {
    mAlarmRinging = b;
  }
  
  private void setEndClockTo(int value) {
    mEndTime.set(System.currentTimeMillis()+(value*1000));
    setDigitalEndTime(mEndTime.format(TIME_FORMAT));
  }
  
  private void setDigitalCountdown(String format) {
    this.msCountdownTime = format;
  }

  private void setDigitalEndTime(String format) {
    this.msEndTime = format;
  }

  private void setDigitalEndTimeInactive() {
    this.mTextSmall = this.mTextSmallMGray;
  }

  private void setDigitalEndTimeActive() {
    this.mTextSmall = this.mTextSmallWhite;
  }
}

