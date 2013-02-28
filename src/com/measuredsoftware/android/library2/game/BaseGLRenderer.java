
package com.measuredsoftware.android.library2.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

/**
 * An OpenGL ES renderer based on the GLSurfaceView rendering framework.  This
 * class is responsible for drawing a list of renderables to the screen every
 * frame.  It also manages loading of textures and (when VBOs are used) the
 * allocation of vertex buffer objects.
 */
public abstract class BaseGLRenderer implements MSGLSurfaceView.Renderer {
  // Specifies the format our textures should be converted to upon load.
  private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
  
  // Pre-allocated arrays to use at runtime so that allocation during the
  // test can be avoided.
  private int[] mTextureNameWorkspace;
  private int[] mCropWorkspace;
  // A reference to the application context.
  protected Context mContext;
  
  protected BaseGameLogic mGameThread;
  protected boolean mFirstDraw;
  public boolean mWasPaused;
  
  protected RenderableManager mRenderableManager;
  
  private HashMap<Integer,Integer> mTextures;

  public BaseGLRenderer(Context context, Config config) {
    // Pre-allocate and store these objects so we can use them at runtime
    // without allocating memory mid-frame.
    mTextureNameWorkspace = new int[1];
    mCropWorkspace = new int[4];
    
    sBitmapOptions.inPreferredConfig = config;
    
    mContext = context;
    
    mFirstDraw = true;
    mWasPaused = false;
    
    mTextures = new HashMap<Integer,Integer>(200);
  }
  
  public void setRenderableManager(RenderableManager rm) {
    mRenderableManager = rm;
  }

  public int[] getConfigSpec() {
    // We don't need a depth buffer, and don't care about our
    // color depth.
    int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_NONE };
    return configSpec;
  }
  
  public void setGameThread(BaseGameLogic gt) {
    mGameThread = gt;
  }    
  
  /**
   * Called when the rendering thread shuts down.  This is a good place to
   * release OpenGL ES resources.
   * @param gl
   */
  public void shutdown(GL10 gl) {
    int[] texturesToDelete = null;

    //Log.d("wh","BaseGLRenderer shutdown");
    final int count = mRenderableManager.mTotalCount;
    //Log.d("wh",count + " sprites");
    
    int deleted = 0;
    for (int x = 0; x < count; x++) {
      GLSprite sprite = mRenderableManager.mForDrawing[x];
      if (sprite == null)
        continue;
      
      ++deleted;
      texturesToDelete = sprite.getTextureNames();
      if (texturesToDelete != null) {
        gl.glDeleteTextures(1, texturesToDelete, 0);
        sprite.setTextureNames(null);
      }
      final Grid g = sprite.getGrid();
      if (g != null)
        g.freeHardwareBuffers(gl);
    }

    //Log.d("wh","deleted " + deleted + " sprites");
  }
 
  /** 
   * Loads a bitmap into OpenGL and sets up the common parameters for 
   * 2D texture maps. 
   */
  protected int loadBitmapAsTexture(Context context, GL10 gl, int resourceId, int cropX, int cropY) {
    int textureName = -1;
    Integer key = new Integer(resourceId);
    Integer existing = mTextures.get(key);
    if (existing != null) {
      return existing.intValue();
      //////
    }
    
    if (context != null && gl != null) {
      gl.glGenTextures(1, mTextureNameWorkspace, 0);

      textureName = mTextureNameWorkspace[0];
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

      gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
      
      InputStream is = context.getResources().openRawResource(resourceId);
      Bitmap bitmap;
      try {
        bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // Ignore.
        }
      }

      myTexImage2D(gl, bitmap);
      //GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

      // crop workspace selects a subsection of the bitmap (i.e. for when using more than more graphic in a bitmap)
      mCropWorkspace[0] = 0;
      mCropWorkspace[1] = cropX;
      mCropWorkspace[2] = cropY;
      mCropWorkspace[3] = -cropX;
      
      bitmap.recycle();
      bitmap = null;

      ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

      int error = gl.glGetError();
      if (error != GL10.GL_NO_ERROR) {
        //Log.e("common", "Texture Load GLError: " + error);
      }
      
      mTextures.put(key, new Integer(textureName));
    }

    return textureName;
  }
  
  /*
  private void myTexImage2D(GL10 gl, Bitmap bitmap) { 
    // Don't loading using GLUtils, load using gl-method directly 
    // GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); 
    int[] pixels = extractPixels(bitmap); 
    byte[] pixelComponents = new byte[pixels.length*4]; 
    int byteIndex = 0; 
    for (int i = 0; i < pixels.length; i++) { 
      int p = pixels[i]; 
              // Convert to byte representation RGBA required by gl.glTexImage2D. 
              // We don't use intbuffer, because then we 
              // would be relying on the intbuffer wrapping to write the ints in 
              // big-endian format, which means it would work for the wrong 
              // reasons, and it might brake on some hardware. 
      pixelComponents[byteIndex++] = (byte) ((p >> 16) & 0xFF); // red 
      pixelComponents[byteIndex++] = (byte) ((p >> 8) & 0xFF); //  green 
      pixelComponents[byteIndex++] = (byte) ((p) & 0xFF); // blue 
      pixelComponents[byteIndex++] = (byte) (p >> 24);  // alpha 
    } 
    ByteBuffer pixelBuffer = ByteBuffer.wrap(pixelComponents); 
    gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, 
    bitmap.getWidth(), bitmap.getHeight(), 0, GL10.GL_RGBA, 
    GL10.GL_UNSIGNED_BYTE, pixelBuffer); 
  } */
  private static final boolean IS_LITTLE_ENDIAN = 
    (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN); 
            private void myTexImage2D(GL10 gl, Bitmap bitmap) { 
                    // Don't loading using GLUtils, load using gl-method directly 
                    // GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); 
                    int[] pixels = extractPixels(bitmap); 
                    for (int i = pixels.length - 1; i >= 0; i--) { 
                            int p = pixels[i]; 
                            int r = ((p >> 16) & 0xFF); 
                            int g = ((p >> 8) & 0xFF); // green 
                            int b = ((p) & 0xFF); // blue 
                            int a = (p >> 24); // alpha 
                            if (IS_LITTLE_ENDIAN) { 
                                    pixels[i] = a << 24 | b << 16 | g << 8 | r; 
                            } else { 
                                    pixels[i] = r << 24 | g << 16 | b << 8 | a; 
                            } 
                    } 
                    IntBuffer pixelBuffer = IntBuffer.wrap(pixels); 
                    gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, 
    bitmap.getWidth(), bitmap.getHeight(), 0, GL10.GL_RGBA, 
    GL10.GL_UNSIGNED_BYTE, pixelBuffer); 
            }   

  public static int[] extractPixels(Bitmap src) { 
    int x = 0; 
    int y = 0; 
    int w = src.getWidth(); 
    int h = src.getHeight(); 
    int[] colors = new int[w * h]; 
    src.getPixels(colors, 0, w, x, y, w, h); 
    return colors; 
}   

  /** 
   * Loads a bitmap 
   */
  public static Bitmap loadBitmap(Context context, int resourceId) {
    Bitmap bitmap = null;
    if (context != null) {
      InputStream is = context.getResources().openRawResource(resourceId);
      try {
        bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // Ignore.
        }
      }
    }

    return bitmap;
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    
    // call back to enable game because renderer is now up and running
    if (mFirstDraw) {
      mGameThread.enable();
      mFirstDraw = false;
    }

    gl.glMatrixMode(GL10.GL_MODELVIEW);
    
    Grid.beginDrawing(gl, true);
    
    int x = 0;
    try {
      for (x = 0; x < mRenderableManager.mTotalCount; x++) {
        if (mRenderableManager.mForDrawing[x] == null)
          continue;
        
        if (mRenderableManager.mForDrawing[x].mDraw) {
          mRenderableManager.mForDrawing[x].draw(gl);
        }
      } 
    } catch(ArrayIndexOutOfBoundsException e) {
      //Log.e("galataxi","aioob exception drawing sprite " + x);
      //Log.e("galataxi","sprite id " + mRenderableManager.mForDrawing[x].mResourceIds[0]);
      throw e;
    } catch(NullPointerException e) {
    }

    drawOthers(gl);

    Grid.endDrawing(gl);
  }
  
  protected abstract void drawOthers(GL10 gl);

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    gl.glViewport(0, 0, width, height);

    mFirstDraw = true;
    
    /*
     * Set our projection matrix. This doesn't have to be done each time we
     * draw, but usually a new projection needs to be set when the viewport
     * is resized.
     */
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
    
    gl.glShadeModel(GL10.GL_FLAT);
    gl.glEnable(GL10.GL_BLEND);
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
    gl.glEnable(GL10.GL_TEXTURE_2D);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    mTextures.clear();
    
    /*
     * Some one-time OpenGL initialization can be made here probably based
     * on features of this particular context
     */
    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

    gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
    gl.glShadeModel(GL10.GL_FLAT);
    gl.glDisable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL10.GL_TEXTURE_2D);
    /*
     * By default, OpenGL enables features that improve quality but reduce
     * performance. One might want to tweak that especially on software
     * renderer.
     */
    gl.glDisable(GL10.GL_DITHER);
    gl.glDisable(GL10.GL_LIGHTING);

    gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
    
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    
    loadSpriteTextures(gl);
  }    

  private void loadSpriteTextures(GL10 gl) {
    // Load our texture and set its texture name on all sprites.
    final int count = mRenderableManager.mTotalCount;
    
    // we are using hardware buffers and the screen lost context
    // then the buffer indexes that we recorded previously are now
    // invalid.  Forget them here and recreate them below.
    for (int x = 0; x < count; x++) {
      if (mRenderableManager.mForDrawing[x] == null)
        continue;
      
      // Ditch old buffer indexes.
      final Grid g = mRenderableManager.mForDrawing[x].getGrid();
      if (g != null)
        g.forgetHardwareBuffers();
    }

    for (int x = 0; x < count; x++) {
      GLSprite sprite = mRenderableManager.mForDrawing[x];
      if (sprite == null)
        continue;
      
      int[] resource = sprite.getResourceIds();
      int[] texNames = new int[resource.length];
      for(int i=0; i < resource.length; i++) {
        final int textId = loadBitmapAsTexture(mContext, gl, resource[i], sprite.mContentWidth, sprite.mContentHeight);
        texNames[i] = textId;
      }
      sprite.setTextureNames(texNames);
      
      if (gl != null ) {
        Grid currentGrid = sprite.getGrid();
        if (currentGrid != null && !currentGrid.usingHardwareBuffers())
          currentGrid.generateHardwareBuffers(gl);
      }
    }
  }
  
  public void pause() {
  }

  public void resume() {
  }
}
