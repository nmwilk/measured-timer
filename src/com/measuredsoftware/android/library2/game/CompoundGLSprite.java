package com.measuredsoftware.android.library2.game;

public class CompoundGLSprite {

  public static final int MAX_SPRITES = 10;
  
  protected GLSprite  mSprites[];
  protected boolean   mIsRotatable[];
  protected final int mSpriteCount;
  
  public static class Builder {
    
    private GLSprite mSprites[];
    private int      mSpriteCount;
    private boolean  mIsFixed;
    
    public Builder() {
      mSprites     = new GLSprite[CompoundGLSprite.MAX_SPRITES];
      mSpriteCount = 0;
    }
    
    public Builder add(GLSprite s) {
      if (mSpriteCount < mSprites.length) {
        mSprites[mSpriteCount++] = s;
      }
      return this;
    }
    
    public Builder isFixed(boolean b) {
      mIsFixed = b;
      return this;
    }

    public CompoundGLSprite build() {
      return new CompoundGLSprite(this);
    }
  }  
  
  protected CompoundGLSprite(Builder b) {
    this.mSpriteCount = b.mSpriteCount;
    this.mSprites     = new GLSprite[this.mSpriteCount];
    this.mIsRotatable = new boolean[this.mSpriteCount];
    for(int i=0; i < this.mSpriteCount; i++) {
      this.mSprites[i] = b.mSprites[i];
      this.mIsRotatable[i] = (b.mSprites[i].mType == GLSprite.TYPE_ROTATABLE_ONFLY);
    }
  }
  
  public void setRelPos(float x, float y) {
    for(int i=0; i < mSpriteCount; i++) {
      this.mSprites[i].setRelPos(x, y);
    }
  }

  public void setActPos(float x, float y) {
    for(int i=0; i < mSpriteCount; i++) {
      this.mSprites[i].setActPos(x, y);
    }
  }
  
  public void show(boolean hideShow) {
    for(int i=0; i < mSpriteCount; i++) {
      this.mSprites[i].mDraw = hideShow;
    }
  }
}
