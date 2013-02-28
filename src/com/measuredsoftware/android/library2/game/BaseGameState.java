package com.measuredsoftware.android.library2.game;

import android.os.SystemClock;

public abstract class BaseGameState {
  // generic states. 
  public static final int STATE_START        = 0;
  public static final int STATE_PLAYING      = 1;
  public static final int STATE_CUSTOM_START = 100;
  
  public final int TARGET_FRAME_RATE;
  public final float TARGET_FRAME_RATE_DELAY;
  public final long TARGET_FRAME_RATE_DELAY_LONG;
  
  public int mStateCode;
  public Renderable[] mRenderables;
  protected boolean mUserPlaying;
  public int mScreenX;
  public int mScreenY;
  public int mScreenHalfX;
  public int mScreenHalfY;
  public float mScreenRatio;
  public float mScreenPosScalingRatio;
  protected boolean mLevelStarted;
  
  public boolean mControlsEnabled;
  
  public boolean mRestarted;
  
  public boolean mSlowMo; // slow motion (reduces game time)
  
  public long mStartTime; // start of *playing* 
  public long mGameTime;  // duration, with any time paused removed 
  public long mPauseTime; // time this game was paused for.

  public BaseGameState(int targetFrameRate) {
    mStateCode = 0;
    mUserPlaying = false;
    mRestarted = false;
    mLevelStarted = false;
    
    TARGET_FRAME_RATE = targetFrameRate;
    TARGET_FRAME_RATE_DELAY = ((float)1000/TARGET_FRAME_RATE);
    TARGET_FRAME_RATE_DELAY_LONG = (long)TARGET_FRAME_RATE_DELAY;
  }
  
  public void setScreenRes(int x, int y) {
    mScreenX = x;
    mScreenY = y;
    mScreenRatio = ((float)x)/((float)y);
    mScreenHalfX = x/2;
    mScreenHalfY = y/2;
    mScreenPosScalingRatio = 1.0f;
    if (y > 480) {
      mScreenPosScalingRatio = (y/1.5f)/(float)320;
    }
    
  }
  
  public boolean userPlaying() {
    synchronized(this) {
      return mUserPlaying;
    }
  }
  
  public long getGameTime(long now) {
    if (mStartTime == 0)
      return 0;
    
    return (now-mStartTime)-mPauseTime; 
  }
  
  public void userPlaying(boolean b) {
    synchronized(this) {
      mUserPlaying = b;
    }
  }
  
  public boolean levelStarted() {
    return mLevelStarted;
  }

  public void setPausedFor(long pausedForMS) {
    mPauseTime += pausedForMS;
    mGameTime = (SystemClock.uptimeMillis()-mStartTime)-mPauseTime; 
  }
  
  public void enableControls(boolean b) {
    mControlsEnabled = b;
  }
    
  public abstract boolean levelComplete();  
  public abstract void initLevelVars();
  public abstract void pause();
  public abstract void resume();
}
