package com.measuredsoftware.android.library2.game;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface BaseInputHandler {
  public void processInput();
  public void setGameState(BaseGameState gameState);
  public void pause();
  public void restart();
  public void shutdown();
  public void addInput(MotionEvent event);
  public void addInput(KeyEvent event);
}
