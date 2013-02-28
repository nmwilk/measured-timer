package com.measuredsoftware.android.library2.game;

public class RenderableManager {

  public GLSprite   mForDrawing[];   // GLSprites for drawing
  public Renderable mForEditing[]; // Renderables for editing
  public final int  mTotalCount;   // for efficiency
  
  public RenderableManager(GLSprite sprites[]) {
    //GLSprite.setWorldScale(defaultZoom);

    mTotalCount = sprites.length;
    mForEditing = sprites;
    mForDrawing = new GLSprite[mTotalCount];
    for(int i=0; i < mTotalCount; i++) {
      mForDrawing[i] = new GLSprite(sprites[i]);
    }  
  }
  
  public synchronized void flip() {
    for(int i=0; i < mTotalCount; i++) {
      mForDrawing[i].update(mForEditing[i]);
    }
  }
}
