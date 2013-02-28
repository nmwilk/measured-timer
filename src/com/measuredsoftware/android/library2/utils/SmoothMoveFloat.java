package com.measuredsoftware.android.library2.utils;

public class SmoothMoveFloat extends SmoothMove {

  protected float mStartValue;
  protected float mMoveValue;

  public void init(long startTime, long endTime) {
    super.init(startTime, endTime);
    
    mStartValue = -1;
    mMoveValue  = -1;
  }
  
  public void init(long startTime, long endTime, float startValue, float endValue) {
    super.init(startTime, endTime);
    
    mStartValue = startValue;
    mMoveValue  = endValue-startValue;
  }
  
  public float move(long currentTime) {
    final float progressMove = getProgress(currentTime);
    if (mStartValue == -1)
      return progressMove;

    return mStartValue+(mMoveValue*progressMove);
  }

  public void updateEnd(long currentTime, float newEnd) {
    // get the current progress
    final float progressMove = getProgress(currentTime);
    
    if (mStartValue != -1) {
      // change the start position using previous mover info
      mStartValue += (mMoveValue*progressMove);
    }
    
    super.update(currentTime);
    
    if (mStartValue != -1) {
      // update the mover with the new positions
      mMoveValue = newEnd-mStartValue;
    }
  }

  public void updateEnd(long currentTime) {
    super.update(currentTime);
  }
  
  @Override
  public void move(long currentTime, Object instance) {
    // NOT USED
  }

}
