package com.measuredsoftware.android.library2.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.measuredsoftware.android.library2.utils.CoordTools;

public class RotatableCanvasSprite extends TiledCanvasSprite {
  
  private PointF mDrawOffset;
  private PointF mTempCalc;
  
  private final float mPivotRadius;  

  public RotatableCanvasSprite(Bitmap bitmap, int numImages, int numCols, int tileWidth, int tileHeight, int imageWidth, int imageHeight, float pivotRadius) {
    super(bitmap, numImages, numCols, tileWidth, tileHeight, imageWidth, imageHeight);
    
    mDrawOffset = new PointF();
    mTempCalc = new PointF();
    
    mPivotRadius = pivotRadius;    
  }

  protected void calcDrawOffset() {
    final float angle = (mIntendedAngle+180)%360;
    CoordTools.calculatePivotOffset(angle, mPivotRadius, mTempCalc);
    mDrawOffset.x = mHalfContainerWidth+mTempCalc.x;
    mDrawOffset.y = mHalfContainerHeight-mTempCalc.y;
  }

  @Override
  public void draw(Canvas canvas) {
    final float angle = mIntendedAngle%360;
    
    float rem = angle%10;    
    int imgIndex = ((int)angle)/10;
    if (rem > 5)
      ++imgIndex;
    
    if (imgIndex >= mNumImages)
      imgIndex = 0;
    
    calcDrawOffset();
    
    final float adjX = mRelX-mDrawOffset.x;
    final float adjY = mRelY-mDrawOffset.y;
    
    super.draw(canvas, imgIndex, adjX, adjY);
  }
}
