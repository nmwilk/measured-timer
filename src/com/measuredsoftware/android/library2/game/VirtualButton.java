package com.measuredsoftware.android.library2.game;

import android.graphics.RectF;

public class VirtualButton extends CompoundGLSprite {
  
  private final int mResIndexInactive;
  private final int mResIndexActive;
  private final RectF mActiveZone;
  
  private final float mCentreX;
  private final float mCentreY;
  
  public  final boolean mRightSide;
  
  public VirtualButton(Builder b, int indexInactive, int indexActive, float paddingWidth, float paddingHeight, float halfScreenX, float viewportDensity, float screenPosScaling) {
    super(b);
    mResIndexInactive = indexInactive;
    mResIndexActive   = indexActive;
    
    // for convenience
    GLSprite g = mSprites[0];
    mCentreX = g.getRelX()*viewportDensity;
    mCentreY = g.getRelY()*viewportDensity;

    final float hotWidth  = g.mHalfContainerWidth + paddingWidth;
    final float hotHeight = g.mHalfContainerHeight + paddingHeight;
    mActiveZone = new RectF(mCentreX - hotWidth,
                            mCentreY - hotHeight,
                            mCentreX + hotWidth,
                            mCentreY + hotHeight);
    
    //Log.d("wh","set thrust button hotzone to " + mActiveZone.left + " -> " + mActiveZone.right + ", " + mActiveZone.top + " -> " + mActiveZone.bottom);
    
    mRightSide = (mCentreX < halfScreenX);
  }
  
  public boolean inHotZone(float x, float y) {
    final boolean in = mActiveZone.contains(x, y); 

    return in;
  }
  
  public void setActive(boolean active) {
    this.mSprites[0].mCurrentTexIndex = (active) ? mResIndexActive : mResIndexInactive;
  }  
}
