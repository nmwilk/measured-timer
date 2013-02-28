package com.measuredsoftware.android.library2.game;

public class Resources {
  public ResourceSet tileResources;
  public ResourceSet unitResources;
  public ResourceSet otherResources;
  
  public void free() {
    tileResources.free();
    unitResources.free();
    otherResources.free();
  }
}
