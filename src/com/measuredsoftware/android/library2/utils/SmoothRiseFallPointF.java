package com.measuredsoftware.android.library2.utils;

public class SmoothRiseFallPointF extends SmoothMovePointF {
  
  @Override
  protected float calculateFunction(float progress) {
    if (progress > 0.5f)
      progress = 1-progress;
    
    return ((2*progress)-(progress*progress))/2;
  }
}
