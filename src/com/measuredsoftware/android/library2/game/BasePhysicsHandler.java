package com.measuredsoftware.android.library2.game;

public abstract class BasePhysicsHandler {
  protected BaseGameState mGameState;
  public boolean mLevelEnded;    // for use by gamelogic to check if the level is complete

  public BasePhysicsHandler(BaseGameState state) {
    mGameState = state;
  }
  
  public abstract void processPhysics(long time);
  public abstract void onResume();
  public abstract void setupPhysics();
  public abstract void shutdown();
}
