package com.measuredsoftware.android.library2.game;

import android.view.MotionEvent;

import com.measuredsoftware.android.library2.utils.SimpleEventQueue;

public abstract class MotionInputHandler implements InputHandler {
  protected SimpleEventQueue mMotionEventQueue;
  protected GameState mGameState;
  
  public MotionInputHandler(GameState gameState) {
    mMotionEventQueue = new SimpleEventQueue(30);
    mGameState = gameState;
  }
  
  public void addEvent(MotionEvent me) {
    mMotionEventQueue.set(me);
  }
  
  public abstract void processInput();
}
