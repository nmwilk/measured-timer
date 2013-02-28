package com.measuredsoftware.android.library2.game;

import android.graphics.RectF;

public class VirtualControlPad extends CompoundGLSprite {

  public static final int TYPE_LEFT_RIGHT = 0;
  public static final int TYPE_UP_DOWN    = 1;
  public static final int TYPE_4WAY       = 2;
  public static final int TYPE_8WAY       = 3;

  public static final int DIR_NONE        = 0;
  public static final int DIR_UP          = 1;
  public static final int DIR_UP_RIGHT    = 2;
  public static final int DIR_RIGHT       = 3;
  public static final int DIR_DOWN_RIGHT  = 4;
  public static final int DIR_DOWN        = 5;
  public static final int DIR_DOWN_LEFT   = 6;
  public static final int DIR_LEFT        = 7;
  public static final int DIR_UP_LEFT     = 8;

  private final int mType;
  private int mCurrentDir;
  
  private final RectF mActiveZone;
  private final RectF mDeadZone;
  
  private final float mCentreX;
  private final float mCentreY;

  /**
   * 
   * @param b
   * @param indexInactive
   * @param indexActive
   * @param paddingWidth the extra padding for the stick's control X zone, in addition to the sprite's width
   * @param paddingHeight  the extra padding for the stick's control Y zone, in addition to the sprite's height
   */
  public VirtualControlPad(Builder b, int type, float paddingWidth, float paddingHeight, float deadWidth, float deadHeight) {
    super(b);
    
    mType = type;
    
    // for convenience
    GLSprite g = mSprites[0];
    mCentreX = g.getRelX()*GLSprite.mDeviceScale;
    mCentreY = g.getRelY()*GLSprite.mDeviceScale;

    final float hotWidth  = g.mHalfContainerWidth + paddingWidth;
    final float hotHeight = g.mHalfContainerHeight + paddingHeight;
    mActiveZone = new RectF(mCentreX - hotWidth,
                            mCentreY - hotHeight,
                            mCentreX + hotWidth,
                            mCentreY + hotHeight);

    if (deadWidth != 0 && deadHeight != 0) {
      mDeadZone = new RectF(mCentreX - deadWidth,
                            mCentreY - deadHeight,
                            mCentreX + deadWidth,
                            mCentreY + deadHeight);
    } else {
      mDeadZone = null;
    }
  }

  public int getDirection() {
    return mCurrentDir;
  }
  
  public boolean inHotZone(float x, float y) {
    // check dead zone (if any) first
    if (mDeadZone != null) {
      if (mDeadZone.contains(x, y))
        return false;
        //////
    }
    
    final boolean in = mActiveZone.contains(x, y);
    if (in) {
      switch(mType) {
      case TYPE_LEFT_RIGHT:
        if (x < mCentreX)
          mCurrentDir = DIR_LEFT;
        else
          mCurrentDir = DIR_RIGHT;
        break;
      case TYPE_4WAY:
        final boolean deadX = mDeadZone.contains(x, mCentreY);
        if (x < mCentreX && !deadX)
          mCurrentDir = DIR_LEFT;
        else if (x > mCentreX && !deadX)
          mCurrentDir = DIR_RIGHT;
        else if (y < mCentreY)
          mCurrentDir = DIR_DOWN;
        else if (y > mCentreY)
          mCurrentDir = DIR_UP;
        break;
      }
    }
    
    return in;
  }
}
