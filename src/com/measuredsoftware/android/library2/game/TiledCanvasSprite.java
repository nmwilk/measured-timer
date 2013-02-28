package com.measuredsoftware.android.library2.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class TiledCanvasSprite extends CanvasSprite {

  protected final int mNumImages; // the number of separate images in the tiled graphic resourceIds[0]
  protected final int mNumCols;   // the number of images per row in the tiled graphic resourceIds[0]
  protected final int mTileWidth;     // the width of each tile
  protected final int mTileHeight;    // the height of each tile
  
  protected final int[][] mOffsets;
  
  protected Rect mOffsetRect;
  protected Rect mDrawRect;
  
  public TiledCanvasSprite(Bitmap bitmap, int numImages, int numCols, int tileWidth, int tileHeight, int imageWidth, int imageHeight) {
    super(bitmap);
    
    mWidth = imageWidth;
    mHeight = imageHeight;
    mNumImages = numImages;
    mNumCols = numCols;
    mTileWidth = tileWidth;
    mTileHeight = tileHeight;
    mHalfContainerWidth = tileWidth/2;
    mHalfContainerHeight = tileHeight/2;
    
    mOffsets = new int[numImages][2];
    
    int x = 0;
    int y = mTileHeight;
    for(int i=0; i < numImages; i++) {
      
      // new 'line'
      if (i!=0 && ((i%numCols) == 0)) {
        x = 0;
        y += mTileHeight;
      }
      
      mOffsets[i] = new int[2];
      mOffsets[i][0] = x;
      mOffsets[i][1] = y-mTileHeight;
      
      x += mTileWidth;
    }

    mOffsetRect = new Rect();   
    mDrawRect   = new Rect();
  }
  
  public void draw(Canvas canvas, int img) {
    draw(canvas, img, mRelX, mRelY);
  }

  public void draw(Canvas canvas, int img, float x, float y) {
    mOffsetRect.left    = mOffsets[img][0];
    mOffsetRect.right   = (mOffsets[img][0] + mTileWidth);
    mOffsetRect.top     = mOffsets[img][1];
    mOffsetRect.bottom  = (mOffsets[img][1] + mTileHeight);

    mDrawRect.left    = (int)x;
    mDrawRect.right   = (int)(x+mTileWidth);
    mDrawRect.top     = (int)y;
    mDrawRect.bottom  = (int)(y+mTileHeight);
    
    canvas.drawBitmap(mBitmap, mOffsetRect, mDrawRect, null);
  }
}
