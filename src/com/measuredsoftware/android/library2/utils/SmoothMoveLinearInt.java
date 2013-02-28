package com.measuredsoftware.android.library2.utils;

public class SmoothMoveLinearInt extends SmoothMoveInt {

  @Override
  protected float calculateFunction(float progress) {
    return progress;
  }
}
