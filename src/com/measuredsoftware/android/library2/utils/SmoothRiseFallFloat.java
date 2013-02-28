package com.measuredsoftware.android.library2.utils;

public class SmoothRiseFallFloat extends SmoothMoveFloat {

  @Override
  protected float calculateFunction(float progress) {
    if (progress > 0.5f)
      progress = 1-progress;
    
    progress *= 2;
    
    return ((2*progress)-(progress*progress));
  }
  
  /**
   * used to re-use after it's expired
   * @param newStart
   */
  public void updateStart(long newStart) {
    this.mStartTime = newStart;
    this.mEndTime   = newStart+this.mDurationTime;
  }
  
  /**
   * use to re-use after it's expired, and change the end value (start value the same)
   * @param newStart
   * @param newValue
   */
  public void updateStartAndMaxValue(long newStart, float newValue) {
    this.updateStart(newStart);
    mMoveValue  = newValue-mStartValue;
  }
}
