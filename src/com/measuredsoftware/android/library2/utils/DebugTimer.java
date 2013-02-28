package com.measuredsoftware.android.library2.utils;

import android.os.SystemClock;

public class DebugTimer
{
  private long mStartTime;
  private long mElapsed;
  private String mLabel;
  
  public DebugTimer()
  {
    reset();
  }
  
  private void reset()
  {
    mStartTime = 0;
    mLabel = "";
    mElapsed = 0;
  }
  
  public void start(String label)
  {
    mElapsed = 0;
    mLabel = label;
    mStartTime = SystemClock.uptimeMillis();
  }
  
  public void end()
  {
    mElapsed = SystemClock.uptimeMillis() - mStartTime;
  }
  
  public String toString()
  {
    return mLabel + " took " + mElapsed + "ms";
  }
}
