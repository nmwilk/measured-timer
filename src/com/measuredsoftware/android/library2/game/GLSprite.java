package com.measuredsoftware.android.library2.game;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.PointF;

import com.measuredsoftware.android.library2.utils.CoordTools;

/**
 * This is the OpenGL ES version of a sprite.  It is more complicated than the
 * CanvasSprite class because it can be used in more than one way.  This class
 * can draw using a grid of verts, a grid of verts stored in VBO objects, or
 * using the DrawTexture extension.
 */
public class GLSprite extends Renderable {
  
  public static final int TYPE_NORMAL           = 0;
  public static final int TYPE_BACKGROUND       = 1;
  public static final int TYPE_ROTATABLE_TILES  = 2;
  public static final int TYPE_ROTATABLE_ONFLY  = 3;
  
  // The OpenGL ES texture handle to draw.
  public final int        mType;
  protected int[]         mTexNames;
  // The id of the original resource that mTextureName is based on.
  protected final int[]   mResourceIds;
  protected final int     mTexCount;
  protected final Grid    mGrid;
  protected final boolean mDontOffset; // don't offset the centre to draw at centre position 
  
  // background
  protected final float mWorldX;
  protected final float mWorldY;
  
  // rotatable_onfly
  protected final int     mHalfWidth;
  protected final float   mFixedAngleOffset;
  protected final float   mPivotRadius;
  public    final PointF  mDrawOffset;
  
  // SCALING
  //
  // device scale is for use in positioning, so that game logic
  // can work in one resolution and the renderer will adjust the graphics
  // to the correct resolution when drawing
  //
  // STATIC VARS
  // world scale is the current scale of the map, i.e. current zoom in/out level.
  // device scale is the density of the device (i.e. 0.75 for X10mini, 1.0 for Hero, 1.5 for N1)
  // overall scale is world scale * device scale. ONLY USED FOR POSITIONING - as images are loaded
  // in the correct scale (ldpi/mdpi/hdpi etc).
  // 
  // INSTANCE VARS
  // Renderable's mScale is the scaling of the graphics itself, in relation to the world scale.
  // i.e. world scale is used to zoom in. mScale is used to scale the sprite separately from other
  // sprites.
  
  public    static float mDeviceScale;
  protected static float mWorldScale;
  protected static float mOverallScale;
  
  public static class Builder extends Renderable.Builder {
    
    private int     mType;
    private int     mResIds[];
    private int     mTexCount;
    private Grid    mGrid;
    private boolean mDontOffset;
    
    // background
    private float mWorldX;
    private float mWorldY;
    
    // rotatable_onfly
    private int   mHalfWidth;
    private float mFixedAngleOffset;

    
    public Builder() {
    }
    
    public Builder type(int t) {
      mType = t;
      return this;
    }
    
    public Builder resIds(int ids[]) {
      mResIds = ids;
      if (ids != null)
        mTexCount = ids.length;

      return this;
    }
    
    public Builder grid(Grid g) {
      mGrid = g;
      return this;
    }
    
    /**
     * for calculating an offset when rotating. Used for sprites that need to be pivoted on their centre. 
     * So not used for sprites that are designed to be rotated using their bottom-left as the pivot.
     * @param hw
     * @return
     */
    public Builder halfWidth(int hw) {
      mHalfWidth = hw;
      return this;
    }
    
    public Builder fixedAngleOffset(float ao) {
      this.mFixedAngleOffset = ao;
      return this;
    }
    
    public Builder worldSize(float x, float y) {
      this.mWorldX = x;
      this.mWorldY = y;
      return this;
    }
    
    public Builder dontOffset(boolean b) {
      this.mDontOffset = b;
      return this;
    }
    
    // BACKGROUND
    public Builder setWorldSize(float x, float y) {  

      this.mWorldX = x;
      this.mWorldY = y;
      return this;
    }
    
    public GLSprite build() {
      return new GLSprite(this);
    }
  }
  
  public GLSprite(Builder b) {
    super(b);
    this.mType              = b.mType;
    this.mResourceIds       = b.mResIds;
    this.mTexCount          = b.mTexCount;
    this.mDontOffset        = b.mDontOffset;
    this.mGrid              = b.mGrid;
    this.mWorldX            = b.mWorldX;
    this.mWorldY            = b.mWorldY;
    this.mHalfWidth         = b.mHalfWidth;
    this.mFixedAngleOffset  = b.mFixedAngleOffset;
    this.mPivotRadius       = (this.mHalfWidth != 0 && this.mType == TYPE_ROTATABLE_ONFLY) ? (float)Math.sqrt((Math.pow(this.mHalfWidth, 2)*2)) : 0;
    mCurrentTexIndex = 0;
    
    mDrawOffset = new PointF();
  }
  
  public GLSprite(GLSprite copy) {
    super(copy);
    
    this.mType = copy.mType;
    
    mTexCount = copy.mTexCount;
    if (this.mTexCount > 0 && copy.mTexNames != null) {
      this.mTexNames = new int[this.mTexCount];
      for(int i=0; i < this.mTexCount; i++) {
        this.mTexNames[i] = copy.mTexNames[i];
      }
    }    
    final int residCount = copy.mResourceIds.length;
    if (residCount > 0) {
      this.mResourceIds = new int[residCount];
      for(int i=0; i < residCount; i++) {
        this.mResourceIds[i] = copy.mResourceIds[i];
      }
    } else {
      this.mResourceIds = null;
    }
    
    this.mDontOffset        = copy.mDontOffset;
    this.mGrid              = copy.mGrid;
    this.mWorldX            = copy.mWorldX;
    this.mWorldY            = copy.mWorldY;
    this.mHalfWidth         = copy.mHalfWidth;
    this.mFixedAngleOffset  = copy.mFixedAngleOffset;
    this.mPivotRadius       = (this.mHalfWidth != 0 && this.mType == TYPE_ROTATABLE_ONFLY) ? (float)Math.sqrt((Math.pow(this.mHalfWidth, 2)*2)) : 0;
    
    mDrawOffset = new PointF();
    
    update(copy);
  }
  
  public int[] getResourceIds() {
    return mResourceIds;
  }
  
  public int[] getTextureNames() {
    return mTexNames;
  }
  
  public static void setWorldScale(float worldScale) {
    mWorldScale = worldScale;
    mOverallScale = mWorldScale*mDeviceScale;
  }
  
  public static void setDeviceScale(float deviceScale) {
    mDeviceScale = deviceScale;
    mOverallScale = mWorldScale*mDeviceScale;
  }
  
  public static float getDeviceScale() {
    return mDeviceScale;
  }
  
  public static float getWorldScale() {
    return mWorldScale;
  }
  
  public static float getOverallScale() {
    return mOverallScale;
  }
  
  public Grid getGrid() {
      return mGrid;
  }  

  public void setTextureNames(int[] texNames) {
    mTexNames = texNames;
  }
  
  public void draw(GL10 gl) {
    draw(gl, mTexNames[mCurrentTexIndex]);
  }
  
  protected void draw(GL10 gl, int textId) {
    if (mFixedPos) {
      draw(gl, Math.round(mRelX*mDeviceScale), Math.round(mRelY*mDeviceScale), textId);
    } else {
      draw(gl, Math.round(mRelX), Math.round(mRelY), textId);
    }
  }

  /**
   * used for when specific positioning is required (i.e. drawing one GLSprite instance more than once).
   * @param gl
   * @param drawX - screen position X, ignoring scaling/relative positioning
   * @param drawY - screen position Y, ignoring scaling/relative positioning
   */
  public void draw(GL10 gl, float drawX, float drawY) {
    draw(gl, Math.round(drawX*mOverallScale*mImageScale), Math.round(drawY*mOverallScale*mImageScale), mTexNames[mCurrentTexIndex]);
  }
  
  protected void draw(GL10 gl, float drawAtX, float drawAtY, int textId) {
    gl.glBindTexture(GL10.GL_TEXTURE_2D, textId);
    
    if (mGrid == null) {
      // Draw using the DrawTexture extension.
      ((GL11Ext) gl).glDrawTexfOES(drawAtX, drawAtY, 0, mWidth, mHeight);
    } else {
      // Draw using verts or VBO verts.
      gl.glPushMatrix();
      gl.glLoadIdentity();
      switch(this.mType) {
      case TYPE_NORMAL:
        if (mFixedPos) {
          if (!mDontOffset) {
            gl.glTranslatef(Math.round(drawAtX-(this.mHalfContainerWidth*mImageScale)), Math.round(drawAtY-(this.mHalfContainerHeight*mImageScale)), 0);
          }
        } else {
          final float totalScale = (mDontScale) ? mImageScale : mWorldScale*mImageScale;
          gl.glTranslatef(Math.round(drawAtX-(this.mHalfContainerWidth*totalScale)), Math.round(drawAtY-(this.mHalfContainerHeight*totalScale)), 0);
          //gl.glTranslatef((drawAtX-(this.mHalfContainerWidth*totalScale)), (drawAtY-(this.mHalfContainerHeight*totalScale)), 0);
          gl.glScalef(totalScale,totalScale,1.0f);
        }
        break;
      case TYPE_ROTATABLE_ONFLY:
        calcDrawOffset();
        
        if (mFixedPos || mDontScale) {
          gl.glTranslatef(Math.round(drawAtX+(mDrawOffset.x*mImageScale)), Math.round(drawAtY+(mDrawOffset.y*mImageScale)), 0);
          gl.glRotatef((360-(mIntendedAngle-mFixedAngleOffset))%360, 0, 0, 1.0f);        
          gl.glScalef(mImageScale,mImageScale,1.0f);
        } else {
          gl.glTranslatef(Math.round(drawAtX+((mDrawOffset.x*mWorldScale)*mImageScale)), Math.round(drawAtY+((mDrawOffset.y*mWorldScale)*mImageScale)), 0);
          //gl.glTranslatef((drawAtX+((mDrawOffset.x*mWorldScale)*mScale)), (drawAtY+((mDrawOffset.y*mWorldScale)*mScale)), 0);
          
          gl.glRotatef((360-(mIntendedAngle-mFixedAngleOffset))%360, 0, 0, 1.0f);
          
          // no overall/device scale because this is the scaling NOT positioning
          gl.glScalef(mWorldScale*mImageScale,mWorldScale*mImageScale,1.0f);
        }
        break;
      case GLSprite.TYPE_BACKGROUND:
        
        final float scale = mDrawScale;
        //if (mWorldScale != WHGameSettings.NORMAL_ZOOM_LEVEL) 
        //  Log.d("wh","BEFORE scale " + scale + " x " + drawAtX + " y " + drawAtY);
        
        
        // take the mod width/height of the graphic so that it draws past 2x2 tiles
        drawAtX %= (this.mContentWidth*scale);
        drawAtY %= (this.mContentHeight*scale);
        //if (mWorldScale != WHGameSettings.NORMAL_ZOOM_LEVEL)
        //  Log.d("wh","AFTER  scale " + scale + " x " + drawAtX + " y " + drawAtY);
        
        gl.glScalef(scale,scale,1.0f);
        gl.glTranslatef(drawAtX/scale, drawAtY/scale, 0);
        mGrid.draw(gl, true);
        gl.glTranslatef((this.mContentWidth-2), 0, 0);
        mGrid.draw(gl, true);
        gl.glTranslatef(-(this.mContentWidth-2), (mContentHeight-2), 0);
        mGrid.draw(gl, true);
        gl.glTranslatef(this.mContentWidth-2,0, 0);
        // mGrid.draw(gl, true); last draw done after SWITCH
        break;
      }
      
      mGrid.draw(gl, true);
      
      gl.glPopMatrix();
    }
  }    
  
  protected void calcDrawOffset() {
    final float angle = mIntendedAngle;
    if (mPivotRadius != 0)
      CoordTools.calcDrawOffsetGLRotate(angle, mPivotRadius, mDrawOffset);
  }  
}
