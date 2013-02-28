package com.measuredsoftware.android.library2.game;

public abstract class PhysicsHandler {
  protected GameState mGameState;
  protected SoundHandler mSoundHandler;
  
  public PhysicsHandler(GameState state) {
    mGameState = state;
  }
  
  public void setSoundHandler(SoundHandler soundHandler) {
    mSoundHandler = soundHandler;
  }
  
  public abstract void processPhysics(long time);
  public abstract void onResume();
}
