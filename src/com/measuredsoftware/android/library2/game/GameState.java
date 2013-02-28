package com.measuredsoftware.android.library2.game;

public class GameState {
  public int mStateCode;
  public Renderable[] mRenderables;
  protected boolean mUserPlaying;
  protected int mScreenResX;
  protected int mScreenResY;

  public GameState() {
    mStateCode = 0;
    mUserPlaying = false;
  }
  
  public void setScreenRes(int x, int y) {
    mScreenResX = x;
    mScreenResY = y;
  }
  
  public boolean userPlaying() {
    synchronized(this) {
      return mUserPlaying;
    }
  }
  
  public void userPlaying(boolean b) {
    synchronized(this) {
      mUserPlaying = b;
    }
  }
  
}
