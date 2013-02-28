package com.measuredsoftware.android.library2.game;

import android.os.SystemClock;

import com.measuredsoftware.android.library2.dbg.ProfileRecorder;

public abstract class BaseGameLogic extends BaseGameThread {
  
  public interface Status {
    public final static int COMPLETE_RESULT_NONE        = -1;
    public final static int COMPLETE_RESULT_SUCCESS     =  0;
    public final static int COMPLETE_RESULT_FAILED      =  1;
    public final static int COMPLETE_RESULT_OUT_OF_FUEL =  2;
    public final static int COMPLETE_PLAYBACK_COMPLETE  =  3;
    public final static int COMPLETE_DEMO_COMPLETE      =  4;

    public void levelSetup();
    public void levelLoaded();
    public void levelComplete(int result);
    public void levelStarted(); // player tapped screen
    public void levelRestarted(); 
  }

  // use in the big switch to set end result 
  protected Status mStatusCallback;
  
  protected final long START_DELAY;
  
  protected SoundHandler mSound;
  protected BaseInputHandler mInput;
  protected BasePhysicsHandler mPhysics;
  protected BaseCameraHandler mCamera;
  protected BaseGameState mGameState;
  
  protected boolean mSentLevelComplete;
  protected boolean mLevelStarted;
  protected long mLastTime;
  protected long mTimeLevelFinished;
  
  protected boolean mLevelLoadMsgSent; // flagged after renderer has drawn its first frame
    
  public BaseGameLogic(BaseInputHandler   inputHandler, 
                   BasePhysicsHandler physicsHandler, 
                   BaseCameraHandler  cameraHandler,
                   BaseGameState      gameState,
                   SoundHandler   soundHandler,
                   long           startDelay) {
    mInput = inputHandler;
    mPhysics = physicsHandler;
    mCamera = cameraHandler;
    mGameState = gameState;
    mSound = soundHandler;
    
    START_DELAY = startDelay;
    
    initVars();
  }
  
  public void setStatusCallback(Status cb) {
    mStatusCallback = cb;
  }
  
  public void enable() {
    mLoadTime = SystemClock.uptimeMillis();
    mEnabled = true;
  }
  
  protected void initVars() {
    mRun = true;
    mRunning = false;
    mEnabled = false;
    mLoadTime = 0;  
    mLevelStarted = false;
    
    mSentLevelComplete = false;
    mLastTime = 0;
    mPaused = false;
    mTimeLevelFinished = 0;    
    
    mGameState.mStartTime = 0;
    
    mLevelLoadMsgSent = false;
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
  
  protected abstract void bigSwitch(int state) throws InterruptedException;
  protected abstract void startGame();
  
  protected void handleRestart() {    
    mGameState.mRestarted = false; // reset the flag cos we handled it
    mLoadTime = SystemClock.uptimeMillis();
    if (mPhysics != null)
      mPhysics.setupPhysics();
    mInput.restart();
    mLevelStarted = false;
    //mStatusCallback.levelRestarted();
  }

  
  @Override
  public void run() {
    mRunning = true;
    
    // initialise level
    long now = SystemClock.uptimeMillis();
    mGameState.mStateCode = BaseGameState.STATE_START;
    
    mCamera.moveCamera();
    
    mStatusCallback.levelSetup();
    // wait here while thread not enabled
    /*while(!mEnabled) {
      try {
        sleep(50);
      } catch (InterruptedException e) {
      }      
      if (!mRun)
        return;
    }*/
    
    try {
      long pausedAt = 0;
      while(mRun) {
        long waited = 0;
        if (mPaused) {
          pausedAt = SystemClock.uptimeMillis();
          synchronized(this) {
            mWaiting = true;
            mSound.onPause();
            mInput.pause();
            mGameState.pause();
            wait();
            mGameState.resume();
            mWaiting = false;
            if (!mRun)
              throw new QuitException();
            
            // was the level restarted whilst we were paused?
            if (mGameState.mRestarted) {
              handleRestart();
            } else {
              waited = SystemClock.uptimeMillis()-pausedAt;
              if (mLevelStarted && !mGameState.levelComplete()) {
                if (mPhysics != null)
                  mPhysics.onResume();
                mGameState.setPausedFor(waited);
              }
            } 
          }
        }
        
        if (mGameState.mRestarted)
          handleRestart();
        
        now = SystemClock.uptimeMillis();
        
        if (!mLevelLoadMsgSent) {
          if (mEnabled) {
            mStatusCallback.levelLoaded();
            mLevelLoadMsgSent = true;
          }
        }

        if (!mLevelStarted) {
          if (waited != 0) {
            mLoadTime += waited;
            if (mLoadTime > now) // cos starttime gets reset when onPause occurs (call etc)
              mLoadTime = now;   // and we ended up adding this time TOO - resulting in having to wait ages for the race to start.
          }
          
          if ((now - mLoadTime) > START_DELAY) {
            mLevelStarted = true;
            mLoadTime = now;
            startGame();
          }
        }
        ProfileRecorder.sSingleton.start(ProfileRecorder.PROFILE_FRAME);
        
        bigSwitch(mGameState.mStateCode);
        
        synchronized(this) {
          if (!mPaused) {
            now = SystemClock.uptimeMillis();
            if (mLastTime != 0) {
              long wait = mGameState.TARGET_FRAME_RATE_DELAY_LONG-(now-mLastTime);
              if (wait > 0) {
                sleep(wait);
              }
            }
          }
        }

        mLastTime = SystemClock.uptimeMillis();

        ProfileRecorder.sSingleton.stop(ProfileRecorder.PROFILE_FRAME, mLastTime);
      }
    } catch (InterruptedException e) {
    } catch (QuitException e) {
    } catch(Exception e) {
      mSound.shutdown();
      e.printStackTrace();
    } 
    
    // cleanup
    mInput.shutdown();
    if (mPhysics != null)
      mPhysics.shutdown();
    mSound.shutdown();
    mCamera.shutdown();
  
    mRunning = false;
  }  
  
  // used to quit loop
  public class QuitException extends Exception {
    private static final long serialVersionUID = 1L;
  }
}
