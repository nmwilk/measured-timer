package com.measuredsoftware.android.library2.utils;

public class FaderFloat {
  public static final boolean FADE_OUT = false;
  public static final boolean FADE_IN  = true;

  protected long    mEndTime;
  protected long    mFadeTime;
  protected long    mFlaggedTime;
  protected boolean mFlaggedToStop;
  protected float   mValue;
  protected final boolean mOutIn; // default is Fade Out

  public FaderFloat(boolean outIn) {
    mOutIn = outIn;
  }
  
  public void setValue(float value) {
    mValue = value;
    mFlaggedToStop = false;
  }
  
  public void setEnded(long now) {
    mFlaggedToStop = true;
    mFlaggedTime = now;
    mEndTime = now+mFadeTime;
  }
  
  public float getCurrentValue(long now) {
    if (!mFlaggedToStop) {
      if (mOutIn)
        return 0;
      else
        return mValue;
    }
    
    final float since = (float)(now-mFlaggedTime);
    final float ratio = since/(float)mFadeTime;
    
    if (mOutIn)
      return (mValue*ratio);
    else
      return mValue-(mValue*ratio);//((float)(now-mFlaggedTime)/(float)mFadeTime);
  }

  public void setFadeTime(long t) {
    mFadeTime = t;
  }
  
  public boolean expired(long now) {
    return (now > mEndTime);
  }
}
