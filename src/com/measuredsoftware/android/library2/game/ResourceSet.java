package com.measuredsoftware.android.library2.game;

public class ResourceSet {
  public int spriteCount;
  public CanvasSprite sprites[];
  
  public ResourceSet(int size) {
    init(size);
  }
  
  public void init(int size) {
    sprites = new CanvasSprite[size];
    spriteCount = size;
  }
  
  public void free() {
    for(int i=0; i < spriteCount; i++) {
      sprites[i].mBitmap.recycle();
    }
  }
}
