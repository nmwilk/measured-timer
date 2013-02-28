
package com.measuredsoftware.android.library2.utils;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Paint;

public class NumericSprite {
  private final static String sStrike = "0123456789";
  private final static int DEFAULT_MAX_NUMBER_LEN = 4;

  int mPrevVal;
  private LabelMaker mLabelMaker;
  private int[] mWidth = new int[10];
  private int[] mLabelId = new int[10];
  
  private int[] mNumber;
  private int mNumberLen;
  private final int mMaxNumLen;
  private final int mFirstDivisor;
  
  private final float mViewWidth;
  private final float mViewHeight;
  private final boolean mLeadingZeros;
  
  public NumericSprite() {
    this(DEFAULT_MAX_NUMBER_LEN, 480, 320, false);
  }

  public NumericSprite(int maxDigits, boolean leadingZeros) {
    this(maxDigits, 480, 320, leadingZeros);
  }
  
  public NumericSprite(int maxDigits, float viewWidth, float viewHeight, boolean leadingZeros) {
    mLabelMaker = null;
    
    mMaxNumLen = maxDigits;
    if (maxDigits > 0)
      mFirstDivisor = (int)Math.pow(10, (int)(maxDigits-1));
    else 
      mFirstDivisor = 1;
    
    mViewWidth = viewWidth;
    mViewHeight = viewHeight;
    mLeadingZeros = leadingZeros;
    
    mPrevVal = -1;

    initNumber();
  }  
  
  private void initNumber() {
    mNumber = new int[mMaxNumLen];
    for(int i=0; i < mMaxNumLen; i++) {
      mNumber[i] = -1;
    }
    mNumberLen = 0;
  }

  public void initialize(GL10 gl, Paint paint) {
    int height = roundUpPower2((int) paint.getFontSpacing());
    final float interDigitGaps = 9 * 1.0f;
    int width = roundUpPower2((int) (interDigitGaps + paint.measureText(sStrike)));
    mLabelMaker = new LabelMaker(true, width, height);
    mLabelMaker.initialize(gl);
    mLabelMaker.beginAdding(gl);
    for (int i = 0; i < mLabelId.length; i++) {
      String digit = sStrike.substring(i, i+1);
      mLabelId[i] = mLabelMaker.add(gl, digit, paint);
      mWidth[i] = (int) Math.ceil(mLabelMaker.getWidth(i));
    }
    mLabelMaker.endAdding(gl);
  }

  public void shutdown(GL10 gl) {
    mLabelMaker.shutdown(gl);
    mLabelMaker = null;
  }

  /**
   * Find the smallest power of two >= the input value.
   * (Doesn't work for negative numbers.)
   */
  private int roundUpPower2(int x) {
    x = x - 1;
    x = x | (x >> 1);
    x = x | (x >> 2);
    x = x | (x >> 4);
    x = x | (x >> 8);
    x = x | (x >>16);
    return x + 1;
  }
  
  public void setValue(int value) {
    if (value != mPrevVal) {
      mPrevVal = value; // set it now as we're going to change it during calculation
      int currDivisor = mFirstDivisor;
      while(currDivisor > 1 && value < currDivisor) {
        currDivisor /= 10;
      }

      int digits = 1;
      if (value > 9)
        ++digits;
      if (value > 99)
        ++digits;
      if (value > 999)
        ++digits;
      
      // got less digits than the size?
      int diff = mMaxNumLen-digits;
      if (diff > 0) {
        int zero = 0;
        if (!mLeadingZeros)
          zero = -1;
        for(int i=0; i < diff; i++) {
          mNumber[i] = zero; 
        }
      } else {
        diff = 0;
        switch(digits) {
          case 1:
            currDivisor = 1;
            break;
          case 2:
            currDivisor = 10;
            break;
          case 3:
            currDivisor = 100;
            break;
          case 4:
            currDivisor = 1000;
            break;
        }
      }
      
      int breakout = mMaxNumLen-1;
      for(int i=diff; i < mMaxNumLen; i++) {
        int v = value/currDivisor;
        mNumber[i] = v;
        if (i == breakout)
          break;
        if (v > 0)
          value -= v*currDivisor;
        
        currDivisor /= 10;
        if (currDivisor == 0)
          break;
      }      
    }
  }
  
  public String getAsString() {
    StringBuilder sb = new StringBuilder();
    for(int i=0; i < mMaxNumLen; i++) {
      if (mNumber[i] != -1)
      sb.append((char)('0'+mNumber[i]));
    }
    
    return sb.toString();
  }
    
  public void draw(GL10 gl, float x, float y) {
    mLabelMaker.beginDrawing(gl, mViewWidth, mViewHeight);
    for(int i=0; i < mMaxNumLen; i++) {      
      final int current = mNumber[i];
      if (current == -1)
        continue;
      mLabelMaker.draw(gl, x, y, mLabelId[current]);
      x += mWidth[current];
    }
    
    mLabelMaker.endDrawing(gl);
  }

  public float width() {
    float width = 0.0f;
    for(int i=0; i < mMaxNumLen; i++) {   
      if (mNumber[i] == -1)
        continue;
      width += mWidth[mNumber[i]];
    }
    return width;
  }
}
