package com.measuredsoftware.android.library2.game;


public abstract class BaseGameThread extends Thread {
  protected boolean mRun;
  protected boolean mRunning;
  protected boolean mEnabled;
  protected boolean mPaused;  // paused flag
  protected boolean mWaiting; // paused status
  protected long mLoadTime; // time thread enabled
  
  protected void initVars() {
    mRun = true;
    mRunning = false;
    mEnabled = false;
    mPaused = true;
    mLoadTime = 0;  
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
