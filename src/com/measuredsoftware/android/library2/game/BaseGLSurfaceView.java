package com.measuredsoftware.android.library2.game;

import android.content.Context;
import android.util.AttributeSet;

public class BaseGLSurfaceView extends MSGLSurfaceView {

  protected BaseInputHandler mInput;
  protected BaseGameState mGameState;
  
  public BaseGLSurfaceView(Context context) {
    super(context);
  }

  public BaseGLSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setInputHandler(BaseInputHandler input) {
    mInput = input;
  }
  public void setGameState(BaseGameState gameState) {
    mGameState = gameState;
  }
  
  public void pause() {
  }

  public void resume() {
  }
}
