package com.measuredsoftware.android.library2.game;

import android.graphics.Canvas;

import com.measuredsoftware.android.library2.game.BaseCanvasSurfaceView.Renderer;

/**
 * An extremely simple renderer based on the CanvasSurfaceView drawing
 * framework.  Simply draws a list of sprites to a canvas every frame.
 */
public class BaseCanvasRenderer implements Renderer {

  private CanvasSprite[] mSprites;
  protected BaseGameLogic mGameThread;
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
  
  public void setGameThread(BaseGameLogic gt) {
    mGameThread = gt;
  }

  public void sizeChanged(int width, int height) {
  }
}
