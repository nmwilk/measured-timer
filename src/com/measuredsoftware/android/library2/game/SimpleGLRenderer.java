
package com.measuredsoftware.android.library2.game;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

/**
 * An OpenGL ES renderer based on the GLSurfaceView rendering framework.  This
 * class is responsible for drawing a list of renderables to the screen every
 * frame.  It also manages loading of textures and (when VBOs are used) the
 * allocation of vertex buffer objects.
 */
public class SimpleGLRenderer implements GLSurfaceView.Renderer {
    // Specifies the format our textures should be converted to upon load.
    private static BitmapFactory.Options sBitmapOptions
        = new BitmapFactory.Options();
    
    private GLSprite[] mSprites;
    
    // Pre-allocated arrays to use at runtime so that allocation during the
    // test can be avoided.
    private int[] mTextureNameWorkspace;
    private int[] mCropWorkspace;
    // A reference to the application context.
    protected Context mContext;
    
    protected GameThread mGameThread;
    protected boolean mFirstDraw;
    public boolean mWasPaused;
    
    protected int mSpritesCount;
    
    public SimpleGLRenderer(Context context) {
      // Pre-allocate and store these objects so we can use them at runtime
      // without allocating memory mid-frame.
      mTextureNameWorkspace = new int[1];
      mCropWorkspace = new int[4];
      
      // Set our bitmaps to 16-bit, 565 format.
      sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
      
      mContext = context;
      
      mFirstDraw = true;
      mWasPaused = false;
      
      mSpritesCount = 0;
    }
    
    public int[] getConfigSpec() {
      // We don't need a depth buffer, and don't care about our
      // color depth.
      int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_NONE };
      return configSpec;
    }
    
    public void setSprites(GLSprite[] sprites) {
      mSpritesCount = sprites.length;
      mSprites = sprites;
    }
    
    public void setGameThread(GameThread thread) {
      mGameThread = thread;
    }
    
    /**
     * Called when the rendering thread shuts down.  This is a good place to
     * release OpenGL ES resources.
     * @param gl
     */
    public void shutdown(GL10 gl) {
      int[] texturesToDelete = null;

      final int count = mSprites.length;

      for (int x = 0; x < count; x++) {
        GLSprite sprite = mSprites[x];
        texturesToDelete = sprite.getTextureNames();
        gl.glDeleteTextures(1, texturesToDelete, 0);
        sprite.setTextureNames(null);
      }
    }
 
    /** 
     * Loads a bitmap into OpenGL and sets up the common parameters for 
     * 2D texture maps. 
     */
    protected int loadBitmapAsTexture(Context context, GL10 gl, int resourceId) {
      int textureName = -1;
      if (context != null && gl != null) {
        gl.glGenTextures(1, mTextureNameWorkspace, 0);

        textureName = mTextureNameWorkspace[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

        InputStream is = context.getResources().openRawResource(resourceId);
        Bitmap bitmap;
        try {
          bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
          
/*          if (mScaleTo != 0) {
            Log.d("testgame","scaling bitmap of size " + bitmap.getWidth() + "x" + bitmap.getHeight() + " to " + (int)(bitmap.getWidth()*mScaleTo) + "x" + (int)(bitmap.getHeight()*mScaleTo));
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*mScaleTo), (int)(bitmap.getHeight()*mScaleTo), true);
          }
          // correct the bitmap if its not a power of two
          boolean widthP2   = ((Math.log((double)bitmap.getWidth())/Math.log(2.0)) - Math.floor((Math.log((double)bitmap.getWidth())/Math.log(2.0)))) == 0;
          boolean heightP2  = ((Math.log((double)bitmap.getHeight())/Math.log(2.0)) - Math.floor((Math.log((double)bitmap.getHeight())/Math.log(2.0)))) == 0;

          int aimWidth  = bitmap.getWidth();
          int aimHeight = bitmap.getHeight();
          // the bitmap width isn't a power of two. 
          if (!widthP2)
            aimWidth = (int) Math.pow(2,Math.ceil(Math.log((double)bitmap.getWidth())/Math.log(2.0)));
          if (!heightP2)
            aimHeight = (int) Math.pow(2,Math.ceil(Math.log((double)bitmap.getHeight())/Math.log(2.0)));
            
          if (!widthP2 || !heightP2) {
            bitmap = Bitmap.createScaledBitmap(bitmap, aimWidth, aimHeight, true);
            //Matrix matrix = new Matrix();
            //matrix.setTranslate(bitmap.getWidth()-aimWidth, bitmap.getHeight()-aimHeight);
            //bitmap = Bitmap.createBitmap(bitmap, 0, 0, aimWidth, aimHeight, matrix, true);
            Log.d("testgame","padded bitmap to " + bitmap.getWidth() + "x" + bitmap.getHeight());
          }*/
        } finally {
          try {
            is.close();
          } catch (IOException e) {
            // Ignore.
          }
        }

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // crop workspace selects a subsection of the bitmap (i.e. for when using more than more graphic in a bitmap)
        mCropWorkspace[0] = 0;
        mCropWorkspace[1] = bitmap.getHeight();
        mCropWorkspace[2] = bitmap.getWidth();
        mCropWorkspace[3] = -bitmap.getHeight();
        
        bitmap.recycle();

        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

        int error = gl.glGetError();
        if (error != GL10.GL_NO_ERROR) {
          //Log.e("testgame", "Texture Load GLError: " + error);
        }
      }

      return textureName;
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
      if (mFirstDraw) {
        mGameThread.enable();
        mFirstDraw = false;
      }

      gl.glMatrixMode(GL10.GL_MODELVIEW);
      
      for (int x = 0; x < mSpritesCount; x++) {
        if (mSprites[x].mDraw)
          mSprites[x].draw(gl);
      } 
    }

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
      
      // Load our texture and set its texture name on all sprites.
      final int count = mSprites.length;

      for (int x = 0; x < count; x++) {
        GLSprite sprite = mSprites[x];
        int[] resource = sprite.getResourceIds();
        int[] texNames = new int[resource.length];
        for(int i=0; i < resource.length; i++) {
          final int textId = loadBitmapAsTexture(mContext, gl, resource[i]);
          texNames[i] = textId;
        }
        sprite.setTextureNames(texNames);
      }
    }    
}
