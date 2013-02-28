package com.measuredsoftware.android.library2.game;

import javax.microedition.khronos.opengles.GL10;

public interface Sprite {
  public int[] getResourceIds();
  public int[] getTextureNames();
  public void setTextureNames(int[] texNames);
  public void draw(GL10 gl, float adjX, float adjY, int textId);
}
