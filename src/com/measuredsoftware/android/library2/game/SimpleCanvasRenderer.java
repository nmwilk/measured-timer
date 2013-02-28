package com.measuredsoftware.android.library2.game;

import android.graphics.Canvas;

import com.measuredsoftware.android.library2.game.CanvasSurfaceView.Renderer;

/**
 * An extremely simple renderer based on the CanvasSurfaceView drawing
 * framework.  Simply draws a list of sprites to a canvas every frame.
 */
public class SimpleCanvasRenderer implements Renderer {

  private CanvasSprite[] mSprites;
  protected GameThread mGameThread;
  protected boolean mFirstDraw;
  
  public void setSprites(CanvasSprite[] sprites) {
    mSprites = sprites;
    
    mFirstDraw = true;
  }
  
  public void surfaceCreated() {
  }
  
  public void onDrawFrame(Canvas canvas) {
    if (mFirstDraw) {
      mGameThread.enable();
      mFirstDraw = false;
    }
    
    if (mSprites != null) {
      for (int x = 0; x < mSprites.length; x++) {
        if (mSprites[x].mDraw)
          mSprites[x].draw(canvas);
      }
    }
      
  }
  
  public void setGameThread(GameThread gt) {
    mGameThread = gt;
  }

  public void sizeChanged(int width, int height) {
  }
}
