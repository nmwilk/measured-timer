package com.measuredsoftware.android.library2.utils;

public abstract class SmoothMove {
  protected long mStartTime;
  protected long mEndTime;
  protected long mDurationTime;

  public SmoothMove() {
  }
  
  protected void init(long startTime, long endTime) {
    mStartTime    = startTime;
    mEndTime      = endTime;
    mDurationTime = endTime-startTime;
  }

  protected void update(long startTime) {
    mStartTime    = startTime;
    mDurationTime = mEndTime-startTime;
    if (mDurationTime == 0)
      ++mDurationTime;
  }
  
  public abstract void move(long currentTime, Object instance);
  
  /**
   * override this to change the function, which by default is (2*x)-((x*x))
   * @param progress
   * @return
   */
  protected float calculateFunction(float progress) {
    return (2*progress)-(progress*progress);
  }
  
  protected float getProgress(long currentTime) {
    if (currentTime > mEndTime) {
      return 1.0f;
      //////
    } 
    
    if (currentTime < mStartTime) {
      return 0.0f;
      //////
    }
    
    // how far are we through the time (0.0->1.0)
    final float progressTime = (currentTime-mStartTime)/(float)mDurationTime;
    // from that, calculate how far we are through the move (0.0->1.0) using the function
    return calculateFunction(progressTime);
  }
  
  public boolean expired(long currentTime) {
    return (currentTime > this.mEndTime);
  }
  
  public void expire() {
    this.mEndTime = 0;
  }
}
