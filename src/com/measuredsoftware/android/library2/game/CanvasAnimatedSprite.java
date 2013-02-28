package com.measuredsoftware.android.library2.game;

import android.graphics.Bitmap;

public class CanvasAnimatedSprite extends CanvasSprite {
  public Bitmap[] mBitmaps;
  private int mCurrentIndex;
  private int mSize;
  public long mStepTime;
  private long mLastTime;
  
  public CanvasAnimatedSprite(Bitmap[] bitmaps, long stepTime) {
    super(bitmaps[0]);
    
    mBitmaps = bitmaps;
    mStepTime = stepTime;
    
    mSize = bitmaps.length;
    mCurrentIndex = 0;
  }
  
  public void time(long time) {
    long diff = (time-mLastTime) / mStepTime;
    if (diff > 0) {
      mCurrentIndex+=diff;      
      if (mCurrentIndex >= mSize)
        mCurrentIndex = 0;
      
      mBitmap = mBitmaps[mCurrentIndex];
      
      mLastTime = time;
    }
  }
}
