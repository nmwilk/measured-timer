package com.measuredsoftware.android.library2.game;

import android.graphics.PointF;
import android.graphics.RectF;

import com.measuredsoftware.android.library2.utils.CoordTools;

public class VirtualControlStick extends CompoundGLSprite {

  private float mCurrentAngle;
  
  // sprite index (in super.mSprites) to show when stick is inactive (not pressed)
  private final int mSpriteIndexInactive;
  // sprite index to show when stick is active (pressed)
  private final int mSpriteIndexActive;
  
  private final RectF mActiveZone;
  
  private final PointF mTempPoint;
  
  private final PointF  mCentre;
  private final float   mCentreX;
  private final float   mCentreY;
  
  private final float mRadius;
  
  public  final boolean mRightSide;
  
  private boolean mActive;
  
  /**
   * 
   * @param b
   * @param indexInactive
   * @param indexActive
   * @param size - the width and height of the image. used to calculate where to place the control knob.
   * @param paddingWidth the extra padding for the stick's control X zone, in addition to the sprite's width
   * @param paddingHeight  the extra padding for the stick's control Y zone, in addition to the sprite's height
   */
  public VirtualControlStick(Builder b, int indexInactive, int indexActive, PointF position, float size, float paddingWidth, float paddingHeight, float halfScreenX) {
    super(b);
    mSpriteIndexInactive = indexInactive;
    mSpriteIndexActive   = indexActive;
    
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
    
    
    mRightSide = (mCentreX < halfScreenX);    
    
    mRadius = size/3.3f;
    mCentre = new PointF(position.x, position.y);
    
    mTempPoint = new PointF();
  }

  public float getAngle() {
    return mCurrentAngle;
  }
  
  public float setPosition(float x, float y) {
    if (mActive && !mSprites[mSpriteIndexActive].mDraw)
      mSprites[mSpriteIndexActive].mDraw = true;
    
    mCurrentAngle = CoordTools.getAngleFromVelocity(-(mCentreX-x), -(mCentreY-y));
    this.mSprites[mSpriteIndexActive].mIntendedAngle = mCurrentAngle;
    
    setAngle(mCurrentAngle);
    
    return mCurrentAngle;
  }
  
  public void setAngle(float angle) {
    if (!mSprites[mSpriteIndexActive].mDraw)
      mSprites[mSpriteIndexActive].mDraw = true;
    if (!mSprites[mSpriteIndexInactive].mDraw)
      mSprites[mSpriteIndexInactive].mDraw = true;

    mCurrentAngle = angle;
    
    CoordTools.getVelocityFromAngleAndSpeed(mCurrentAngle, mRadius, mTempPoint);
    mTempPoint.x += mCentre.x;
    mTempPoint.y += mCentre.y;
    this.mSprites[mSpriteIndexActive].mIntendedAngle = mCurrentAngle;
    this.mSprites[mSpriteIndexInactive].setRelPos(mTempPoint);
  }
  
  public boolean inHotZone(float x, float y) {
    return mActiveZone.contains(x, y);
  }
  
  public void setActive(boolean active) {
    if (active == mActive)
      return;
    
    mActive = active;
    
    // hide/show stick bottom
    mSprites[mSpriteIndexActive].mDraw = active;
    if (!active)
      mSprites[mSpriteIndexInactive].setRelPos(mCentre);
  }
  
  @Override
  public void show(boolean hideShow) {
    super.show(hideShow);
    this.mSprites[mSpriteIndexActive].mDraw = false;
  }  
}
