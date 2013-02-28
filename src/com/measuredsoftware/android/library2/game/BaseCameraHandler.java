package com.measuredsoftware.android.library2.game;

public abstract class BaseCameraHandler {
  public abstract void moveCamera();
  public abstract void shutdown();
  
  public static float translateX(float x) {
    return -x;
  }
}
