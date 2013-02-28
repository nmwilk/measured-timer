package com.measuredsoftware.android.timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.measuredsoftware.android.library2.utils.CoordTools;

public class RotatableImageView extends ImageView {
  
  public static final int MAX_ANGLE = 360;
  
  protected float mAngle;
  private   float mAnglePrev;
  
  // multitouch
  protected float mStartAngle1; 
  protected float mAngle1;      
  protected float mStartAngle2; 
  protected float mAngle2;      
  protected boolean mMultitouchActive; // >1 finger down currently?
  
  //protected int   m1stPointerID; // DOWN event
  //protected int   m2ndPointerID; // POINTER_DOWN event

  protected int mCentreX;
  protected int mCentreY;
  protected int mRadius;
  
  protected float mIncPerSection;
  protected float mHalfIncPerSection;

  private Bitmap  mIgnoreMask;
  private int mIgnoreMaskWidth;
  private int mIgnoreMaskHeight;
  private boolean mUIMOnlyOnDown;
  
  private boolean mUsedDownAction;
  
  public RotatableImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    mStartAngle1 = 0;
    mStartAngle2 = 0;
    mAngle = 0;
    mAnglePrev = 0;
    
    mCentreX = -1;
    mCentreY = -1;
    
    mMultitouchActive = false;
    
    mIgnoreMask = null;
    mUIMOnlyOnDown = false;
    
    mUsedDownAction = false;
  }
  
  public void setIncrement(float incPerSection) {
    mIncPerSection = incPerSection;
    mHalfIncPerSection = (mIncPerSection/2);
  }
  
  protected void initCentre() {
    if (mCentreX == -1)
    {
      mCentreX = getMeasuredWidth() / 2;
      mCentreY = (getMeasuredHeight() / 2) ;
      mRadius = Math.min(mCentreX, mCentreY);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    initCentre();
    
    canvas.save();
    canvas.rotate(mAngle, mCentreX, mCentreY);

    // Draw the background
    super.onDraw(canvas);
    
    canvas.restore();
  }

  private float snapAngle(float angle) {
    float r = angle;
    final float diff = angle%mIncPerSection;
    if (diff < mHalfIncPerSection)
      r -= diff;
    else
      r += (mIncPerSection-diff);
    
    return r;
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    initCentre();
    
    boolean handled = false;
    
    final int action = event.getAction();
    final float x = event.getX();
    final float y = event.getY();
    
    if (mIgnoreMask != null && !mUIMOnlyOnDown && x < mIgnoreMaskWidth && y < mIgnoreMaskHeight) {
      if (mIgnoreMask.getPixel((int)x, (int)y) == Color.WHITE)
        return true;
    }
    
    final int actionMasked = action & MotionEvent.ACTION_MASK;
    switch(actionMasked) {
      case MotionEvent.ACTION_DOWN:
        if (mIgnoreMask != null && mUIMOnlyOnDown && x < mIgnoreMaskWidth && y < mIgnoreMaskHeight) {
          if (mIgnoreMask.getPixel((int)x, (int)y) == Color.WHITE) {
            return true;
          }
        }
        mUsedDownAction = true;
        mStartAngle1 = snapAngle(mAngle1+CoordTools.getAngleFromVelocity(x-mCentreX, y-mCentreY));
        event.getPointerCount();
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        if (!mUsedDownAction)
          return true;
        
        mStartAngle2 = snapAngle(mAngle2+CoordTools.getAngleFromVelocity(event.getX(1)-mCentreX, event.getY(1)-mCentreY));
        mMultitouchActive = true;
        break;
      case MotionEvent.ACTION_UP:
        if (!mUsedDownAction)
          return true;
        mStartAngle1 = 0;
        mAngle1 = 0;
        mUsedDownAction = false;
        break;
      case MotionEvent.ACTION_POINTER_UP:
        mStartAngle2 = 0;
        mAngle2 = 0;
        mMultitouchActive = false;
        break;
      case MotionEvent.ACTION_MOVE:
        if (!mUsedDownAction)
          return true;

        float diffAngle1 = 0;
        float diffAngle2 = 0;
        
        final int ptrCount = event.getPointerCount();
        for(int i=0; i < ptrCount; i++) {
          if (event.getPointerId(i) == 1) {
            float newAngle2 = snapAngle(mStartAngle2-CoordTools.getAngleFromVelocity(event.getX(i)-mCentreX, event.getY(i)-mCentreY)); 
            diffAngle2 = newAngle2-mAngle2;
            mAngle2 = newAngle2;
          } else {
            float newAngle1 = snapAngle(mStartAngle1-CoordTools.getAngleFromVelocity(x-mCentreX, y-mCentreY)); 
            diffAngle1 = newAngle1-mAngle1;
            mAngle1 = newAngle1;
          }
        }
        
        mAngle += (diffAngle1 + diffAngle2);
        
        if (mAngle < 0)
          mAngle += 360;
        else if (mAngle > MAX_ANGLE)
          mAngle -= MAX_ANGLE;

        break;
    }
    
    if (mAnglePrev != mAngle)
      this.invalidate();
    
    mAnglePrev = mAngle;
    
    return handled;
  }

  public void setIgnoreMask(Bitmap ignoreMask, boolean useOnlyOnDown) {
    mIgnoreMask = ignoreMask;
    mIgnoreMaskWidth = 0;
    mIgnoreMaskHeight = 0;
    if (mIgnoreMask != null) {
      mIgnoreMaskWidth = mIgnoreMask.getWidth();
      mIgnoreMaskHeight = mIgnoreMask.getHeight();
    }
    mUIMOnlyOnDown = useOnlyOnDown;    
  }
}
