package com.measuredsoftware.android.library2.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CanvasSprite extends Renderable {
  protected Bitmap mBitmap;
  
  public CanvasSprite(Bitmap bitmap) {
    this(bitmap, 0, 0);   
  }

  public CanvasSprite(Bitmap bitmap, float _x, float _y) {
    super(new Renderable.Builder());
    mBitmap = bitmap;
    mRelX = _x;
    mRelY = _y;
    mDraw = true;
  }
  
  public void draw(Canvas canvas) {
    canvas.drawBitmap(mBitmap, mRelX, mRelY, null);
  }
}
