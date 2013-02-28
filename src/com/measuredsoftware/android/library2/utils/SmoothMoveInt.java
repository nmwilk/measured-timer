package com.measuredsoftware.android.library2.utils;

public class SmoothMoveInt extends SmoothMove {

  protected int mStartValue;
  protected int mMoveValue;
  
  public void init(long startTime, long endTime, int startValue, int endValue) {
    super.init(startTime, endTime);
    
    mStartValue = startValue;
    mMoveValue  = endValue-startValue;
  }
  
  public int move(long currentTime) {
    return (int)(mStartValue+(mMoveValue*getProgress(currentTime)));
  }

  public void updateEnd(long currentTime, int newEnd) {
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
