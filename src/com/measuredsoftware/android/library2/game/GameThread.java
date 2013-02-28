package com.measuredsoftware.android.library2.game;

import android.os.SystemClock;

public abstract class GameThread extends Thread {
  protected boolean mRun;
  protected boolean mRunning;
  protected boolean mEnabled;
  protected boolean mPaused;  // paused flag
  protected boolean mWaiting; // paused status
  protected long mStartTime; // time thread enabled

  protected SoundHandler mSound;
  
  public void setSound(SoundHandler sound) {
    mSound = sound;
  }
  
  public void enable() {
    mStartTime = SystemClock.uptimeMillis();
    mEnabled = true;
  }
  
  protected void initVars() {
    mRun = true;
    mRunning = false;
    mEnabled = false;
    mPaused = true;
    mStartTime = 0;  
  }
  
  public void setRun(boolean run) {
    mRun = run;
  }  
  
  @Override
  public synchronized void start() {
    mRun = true;
    super.start();
  }
  
  public boolean getRunning() {
    return mRunning;
  }
  
  public void onPause() {
    mPaused = true;
  }
  
  public void onResume() {
    mPaused = false;
    synchronized(this) {
      if (mWaiting)
        notify();
    }
  }
  
  public void onShutdown() {
    synchronized(this) {
      if (mWaiting)
        notify();
    }
  }
    
  public boolean isEnabled() {
    return mEnabled;
  }  
}
