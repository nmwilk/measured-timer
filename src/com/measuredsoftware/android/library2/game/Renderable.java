
package com.measuredsoftware.android.library2.game;

import android.graphics.PointF;

import com.measuredsoftware.android.library2.utils.SmoothRiseFallFloat;

/** 
 * Base class defining the core set of information necessary to render (and move
 * an object on the screen.
 */
public class Renderable {
  public static final int ANIMATION_TYPE_FRAMES  = 0;
  public static final int ANIMATION_TYPE_SCALING = 1;
  
  // Position.
  protected float mRelX;
  protected float mRelY;
  
  protected float mActX;
  protected float mActY;
 
  public float mSpeed;
  
  public long mTurningSince;
  
  // angles
  public float mCurrentAngle;   // angle current travelling
  public float mIntendedAngle;  // angle currently pointing
  
  // Size as required by GL i.e. ^2.
  public float mWidth;
  public float mHeight;
  public float mHalfContainerHeight;
  public float mHalfContainerWidth;
  
  // Size of part visible graphic (cos GL requires images to be ^2)
  public int mContentWidth;
  public int mContentHeight;
  public float mHalfContentHeight;
  public float mHalfContentWidth;
  
  public boolean mHide; // differs to to mDraw, as mDraw is used when animating, mHide overrides mDraw when animating ONLY
  public boolean mDraw;
  public final boolean mFixedPos;
  public final boolean mDontScale;
  
  protected int mCurrentTexIndex;
  
  public float mImageScale; // scale of the image
  public float mDrawScale;  // the overall scale to draw at (takes into account overall/world scale and mImageScale

  // animated?
  protected final boolean mAnimated;
  protected final int     mAnimationType;
  protected final long    mAnimationDuration;
  protected final int     mIndexStart;
  protected final int     mIndexEnd;
  protected final float   mScaleStart;
  protected final float   mScaleEnd;
  protected final long    mFrameTick;    // ms to show a frame for
  protected final long    mWaitBetween;  // ms to wait before looping
  protected final int     mFrameCount;
  private   long          mNextFrameAt;
  
  protected SmoothRiseFallFloat mMover;
  
  private long mAnimStartTime; // used to calc how far animation is through its progress
  
  public static class Builder {
    // animated
    private boolean mAnimated;
    private int     mAnimationType;
    private int     mIndexStart;
    private int     mIndexEnd;
    private long    mAnimationDuration;
    private float   mScaleStart;
    private float   mScaleEnd;
    private long    mFrameTick;    
    private long    mWaitBetween;  
    private float   mWidth;
    private float   mHeight;
    private int     mContentWidth;
    private int     mContentHeight;
    private boolean mFixedPos;
    private float   mImageScale;
    private boolean mDontScale;
    
    public Builder() {
      mImageScale = 1.0f;// default this because 0 is no good
    }
    
    public Builder animatedFrames(int startIndex, int endIndex, long frameTick, long waitBetween) {
      this.mAnimationType = Renderable.ANIMATION_TYPE_FRAMES;
      this.mAnimated      = true;
      this.mIndexStart    = startIndex;
      this.mIndexEnd      = endIndex;
      this.mFrameTick     = frameTick;
      this.mWaitBetween   = waitBetween;
      return this;
    }

    public Builder animatedScaling(float startScale, float endScale, long duration, long waitBetween) {
      this.mAnimationType     = Renderable.ANIMATION_TYPE_SCALING;
      this.mAnimated          = true;
      this.mScaleStart        = startScale;
      this.mScaleEnd          = endScale;
      this.mAnimationDuration = duration;
      this.mWaitBetween       = waitBetween;
      return this;
    }
    
    public Builder setWidthHeight(float w, float h) {
      mWidth  = w;
      mHeight = h;
      return this;
    }
    
    public Builder setContentSize(int w, int h) {
      mContentWidth   = w;
      mContentHeight  = h;
      return this;
    }  
    
    public Builder scale(float scale) {
      mImageScale = scale;
      return this;
    }
    
    public Builder fixedPos(boolean b) {
      mFixedPos = b;
      return this;
    }
    
    public Builder dontScale(boolean b) {
      mDontScale = b;
      return this;
    }
    
    public Renderable build() {
      return new Renderable(this);
    }
  }
  
  public Renderable(Builder b) {
    this.mImageScale        = b.mImageScale; 
    this.mDontScale         = b.mDontScale;
    
    this.mCurrentAngle      = -1;
    
    this.mFixedPos          = b.mFixedPos;
    
    this.mAnimated          = b.mAnimated;
    this.mAnimationType     = b.mAnimationType;
    this.mIndexStart        = b.mIndexStart;
    this.mIndexEnd          = b.mIndexEnd;
    this.mAnimationDuration = b.mAnimationDuration;
    this.mScaleStart        = b.mScaleStart;
    this.mScaleEnd          = b.mScaleEnd;
    this.mFrameTick         = b.mFrameTick;
    this.mFrameCount        = this.mIndexEnd-this.mIndexStart;
    this.mWaitBetween       = b.mWaitBetween;
    this.mNextFrameAt       = 0;
    
    this.mWidth             = b.mWidth;
    this.mHeight            = b.mHeight;
    this.mContentWidth      = b.mContentWidth;
    this.mContentHeight     = b.mContentHeight;
    
    this.mHalfContentWidth    = this.mContentWidth/2;
    this.mHalfContentHeight   = this.mContentHeight/2;
    this.mHalfContainerWidth  = this.mWidth/2;
    this.mHalfContainerHeight = this.mHeight/2;

    if (this.mAnimationType == Renderable.ANIMATION_TYPE_SCALING) {
      mMover = new SmoothRiseFallFloat();
      mMover.init(0, this.mAnimationDuration, this.mScaleStart, this.mScaleEnd);
    }
  }
  
  public Renderable(Renderable copy) {
    this.set(copy);
    this.mDontScale         = copy.mDontScale;
    this.mFixedPos          = copy.mFixedPos;
    this.mAnimated          = copy.mAnimated;
    this.mAnimationType     = copy.mAnimationType;
    this.mScaleStart        = copy.mScaleStart;
    this.mScaleEnd          = copy.mScaleEnd;
    this.mIndexStart        = copy.mIndexStart;
    this.mIndexEnd          = copy.mIndexEnd;
    this.mAnimationDuration = copy.mAnimationDuration;
    this.mFrameTick         = copy.mFrameTick;
    this.mFrameCount        = this.mIndexEnd-this.mIndexStart;    
    this.mWaitBetween       = copy.mWaitBetween;
    this.mNextFrameAt       = 0;

    if (this.mAnimationType == Renderable.ANIMATION_TYPE_SCALING) {
      mMover = new SmoothRiseFallFloat();
      mMover.init(0, this.mAnimationDuration, this.mScaleStart, this.mScaleEnd);
    }
  }

  public void set(Renderable copy) {
    mActX                 = copy.mActX;
    mActY                 = copy.mActY;
    mRelX                 = copy.mRelX;
    mRelY                 = copy.mRelY;
    mSpeed                = copy.mSpeed;
    mTurningSince         = copy.mTurningSince;
    mCurrentAngle         = copy.mCurrentAngle;
    mIntendedAngle        = copy.mIntendedAngle;
    mWidth                = copy.mWidth;
    mHeight               = copy.mHeight;
    mContentWidth         = copy.mContentWidth;
    mContentHeight        = copy.mContentHeight;
    mHalfContainerWidth   = copy.mHalfContainerWidth;
    mHalfContainerHeight  = copy.mHalfContainerHeight;
    mHalfContentWidth     = copy.mHalfContentWidth;
    mHalfContentHeight    = copy.mHalfContentHeight;
    mDraw                 = copy.mDraw;
    mImageScale           = copy.mImageScale;
    mDrawScale            = copy.mDrawScale;
    mCurrentTexIndex      = copy.mCurrentTexIndex;
  }
  
  public void setTexIndex(int i) {
    if (i < 0)
      i=0;
      
    mCurrentTexIndex = i;
  }
  
  /*
   * used in-game to update the sprite's status
   */
  public void update(Renderable copy) {
    this.mDraw              = copy.mDraw;
    this.mCurrentAngle      = copy.mCurrentAngle; 
    this.mIntendedAngle     = copy.mIntendedAngle;
    this.mImageScale        = copy.mImageScale;
    this.mDrawScale         = copy.mDrawScale;
    this.mCurrentTexIndex   = copy.mCurrentTexIndex;
    this.setRelPos(copy.getRelX(), copy.getRelY());
  }  
  
  /**
   * for animated renderables, used to update the tex to show based on time, or the scale based on time
   * @param gameTime
   */
  public void update(long gameTime) {
    if (mAnimationType == Renderable.ANIMATION_TYPE_FRAMES) {
      if (this.mNextFrameAt == 0) {
        this.mNextFrameAt = gameTime+this.mFrameTick;
        return;
        //////
      }
        
      if (!mHide && gameTime > this.mNextFrameAt) {
        if (!this.mDraw)
          this.mDraw = true;
        
        this.mNextFrameAt = gameTime+this.mFrameTick;
        if (++this.mCurrentTexIndex == this.mFrameCount) {
          this.mCurrentTexIndex = 0;
          this.mNextFrameAt += mWaitBetween;
          this.mDraw = false;
        }
      }
    } else if (mAnimationType == Renderable.ANIMATION_TYPE_SCALING) {
      // calculate time since start
      final long diff = gameTime-mAnimStartTime;
      // mod against the duration
      final long remainder = diff%this.mAnimationDuration;

      // get the value for how far we're through the anim
      this.mImageScale = mMover.move(remainder);
    }
  }
    
  public void resetAnim(long gameTime) {
    this.mAnimStartTime = gameTime;
    this.mNextFrameAt = 0;
  }
  
  public void setRelPos(float x, float y) {
    mRelX = x;
    mRelY = y;
  }
  
  public void setRelPos(PointF p) {
    mRelX = p.x;
    mRelY = p.y;
  }
  
  public float getRelX() {
    return mRelX;
  }

  public float getRelY() {
    return mRelY;
  }
  
  public void setActPos(float x, float y) {
    mActX = x;
    mActY = y;
  }
  
  public void setActPos(PointF pos) {
    mActX = pos.x;
    mActY = pos.y;
  }
  
  public void adjustActPos(float x, float y) {
    mActX = (mActX+x);
    mActY = (mActY+y);
  }  

  public void adjustActPos(PointF p) {
    mActX = (mActX+p.x);
    mActY = (mActY+p.y);
  }  
  
  public float getActX() {
    return mActX;
  }

  public float getActY() {
    return mActY;
  }
}
