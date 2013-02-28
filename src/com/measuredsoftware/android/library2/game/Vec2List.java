package com.measuredsoftware.android.library2.game;

public class Vec2List {
  private Vec2[] mList;
  private boolean[] mActive;
  public int mCurrentSize;
  private final int mMaxSize;
  
  public Vec2List(int maxSize) {
    mList = new Vec2[maxSize];
    mActive = new boolean[maxSize];
    for(int i=0; i < maxSize; i++)  {
      mActive[i] = false;
    }
    mMaxSize = maxSize;
    mCurrentSize = 0;
  }
  
  public boolean add(Vec2 vec) {
    if (mCurrentSize == mMaxSize)
      return false;
    
    mList[mCurrentSize].set(vec);
    mActive[mCurrentSize++] = true;
    
    return true;
  }
  
  public Vec2 get(int i, boolean autoRemove) {
    if (i < 0 || i >= mCurrentSize)
      return null;
    
    Vec2 ret = mList[i];
    if (autoRemove)
      mActive[i] = false;
    
    return ret;
  }
  
  public boolean getActive(int i) {
    if (i < 0 || i >= mCurrentSize)
      return false;
    
    return mActive[i];
  }

  public void setActive(int i, boolean flag) {
    if (i < 0 || i >= mCurrentSize)
      return;
    
    mActive[i] = flag;
  }
}
