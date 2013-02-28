
package com.measuredsoftware.android.library2.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Implements a surface view which writes updates to the surface's canvas using
 * a separate rendering thread.  This class is based heavily on GLSurfaceView.
 */
public class CanvasSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
  private boolean mSizeChanged = true;

  private SurfaceHolder mHolder;
  private CanvasThread mCanvasThread;
  private SimpleCanvasRenderer mRenderer;
  
  public CanvasSurfaceView(Context context) {
    super(context);
    init();
  }

  public CanvasSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed
    mHolder = getHolder();
    mHolder.addCallback(this);
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
  }

  public SurfaceHolder getSurfaceHolder() {
    return mHolder;
  }

  /** Sets the user's renderer and kicks off the rendering thread. */
  public void setRenderer(Renderer renderer) {
    mCanvasThread = new CanvasThread(mHolder, renderer);
    mCanvasThread.start();
    mRenderer = (SimpleCanvasRenderer)renderer;
  }

  public void surfaceCreated(SurfaceHolder holder) {
    mCanvasThread.surfaceCreated();
    mRenderer.surfaceCreated();
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // Surface will be destroyed when we return
    mCanvasThread.surfaceDestroyed();
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    // Surface size or format has changed. This should not happen in this
    // example.
    mCanvasThread.onWindowResize(w, h);
  }

  /** Inform the view that the activity is paused.*/
  public void onPause() {
    mCanvasThread.onPause();
  }

  /** Inform the view that the activity is resumed. */
  public void onResume() {
    mCanvasThread.onResume();
  }

  /** Inform the view that the window focus has changed. */
  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    mCanvasThread.onWindowFocusChanged(hasFocus);
  }
  
  /**
   * Set an "event" to be run on the rendering thread.
   * @param r the runnable to be run on the rendering thread.
   */
  public void setEvent(Runnable r) {
    mCanvasThread.setEvent(r);
  }
  
  /** Clears the runnable event, if any, from the rendering thread. */
  public void clearEvent() {
    mCanvasThread.clearEvent();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mCanvasThread.requestExitAndWait();
  }
  
  protected void stopDrawing() {
    mCanvasThread.requestExitAndWait();
  }

  // ----------------------------------------------------------------------

  /** A generic renderer interface. */
  public interface Renderer {
     
    /**
     * Surface changed size.
     * Called after the surface is created and whenever
     * the surface size changes. Set your viewport here.
     * @param width
     * @param height
     */
    void sizeChanged(int width, int height);
    /**
     * Draw the current frame.
     * @param canvas The target canvas to draw into.
     */
    void onDrawFrame(Canvas canvas);
  }


  /**
   * A generic Canvas rendering Thread. Delegates to a Renderer instance to do
   * the actual drawing.
   */
  class CanvasThread extends Thread {
      private boolean mDone;
      private boolean mPaused;
      private boolean mHasFocus;
      private boolean mHasSurface;
      private boolean mContextLost;
      private int mWidth;
      private int mHeight;
      private Renderer mRenderer;
      private Runnable mEvent;
      private SurfaceHolder mSurfaceHolder;
      
      CanvasThread(SurfaceHolder holder, Renderer renderer) {
          super();
          mDone = false;
          mWidth = 0;
          mHeight = 0;
          mRenderer = renderer;
          mSurfaceHolder = holder;
          setName("CanvasThread");
      }

      @Override
      public void run() {
         
          boolean tellRendererSurfaceChanged = true;

          /*
           * This is our main activity thread's loop, we go until
           * asked to quit.
           */
          while (!mDone) {
              /*
               *  Update the asynchronous state (window size)
               */
              int w;
              int h;
              synchronized (this) {
                  // If the user has set a runnable to run in this thread,
                  // execute it and record the amount of time it takes to 
                  // run.
                  if (mEvent != null) {
                    mEvent.run();
                  }
                 
                  if(needToWait()) {
                      while (needToWait()) {
                          try {
                              wait();
                          } catch (InterruptedException e) {
                              
                          }
                      }
                  }
                  if (mDone) {
                      break;
                  }
                  tellRendererSurfaceChanged = mSizeChanged;
                  w = mWidth;
                  h = mHeight;
                  mSizeChanged = false;
              }
             
             
              if (tellRendererSurfaceChanged) {
                  mRenderer.sizeChanged(w, h);
                  tellRendererSurfaceChanged = false;
              }
              
              if ((w > 0) && (h > 0)) {
                  // Get ready to draw.
                  // We record both lockCanvas() and unlockCanvasAndPost()
                  // as part of "page flip" time because either may block
                  // until the previous frame is complete.
                  Canvas canvas = mSurfaceHolder.lockCanvas();
                  if (canvas != null) {
                      // Draw a frame!
                      mRenderer.onDrawFrame(canvas);
                      
                      mSurfaceHolder.unlockCanvasAndPost(canvas);
                      
                  }
              }
          }
      }

      private boolean needToWait() {
          return (mPaused || (! mHasFocus) || (! mHasSurface) || mContextLost)
              && (! mDone);
      }

      public void surfaceCreated() {
          synchronized(this) {
              mHasSurface = true;
              mContextLost = false;
              notify();
          }
      }

      public void surfaceDestroyed() {
          synchronized(this) {
              mHasSurface = false;
              notify();
          }
      }

      public void onPause() {
          synchronized (this) {
              mPaused = true;
          }
      }

      public void onResume() {
          synchronized (this) {
              mPaused = false;
              notify();
          }
      }

      public void onWindowFocusChanged(boolean hasFocus) {
          synchronized (this) {
              mHasFocus = hasFocus;
              if (mHasFocus == true) {
                  notify();
              }
          }
      }
      public void onWindowResize(int w, int h) {
          synchronized (this) {
              mWidth = w;
              mHeight = h;
              mSizeChanged = true;
          }
      }

      public void requestExitAndWait() {
          // don't call this from CanvasThread thread or it is a guaranteed
          // deadlock!
          synchronized(this) {
              mDone = true;
              notify();
          }
          try {
              join();
          } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
          }
      }

      /**
       * Queue an "event" to be run on the rendering thread.
       * @param r the runnable to be run on the rendering thread.
       */
      public void setEvent(Runnable r) {
          synchronized(this) {
              mEvent = r;
          }
      }
      
      public void clearEvent() {
          synchronized(this) {
              mEvent = null;
          }
      }
      
  }
}
