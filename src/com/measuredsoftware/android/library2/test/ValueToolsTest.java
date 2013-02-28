package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;
import android.graphics.Point;

import com.measuredsoftware.android.library2.utils.ValueTools;

public class ValueToolsTest extends TestCase {

  public void testProgressInRange1() {
    final float min = 0.5f;
    final float max = 1.0f;
    float in[]  = new float[] { 0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
    float exp[] = new float[] { 0f, 0f,   0f,   0f,   0f,   0f,   0.2f, 0.4f, 0.6f, 0.8f, 1.0f, };
    try {
      for(int i=0; i < in.length; i++) {
        final float result  = ValueTools.progressInRange(in[i], min, max);
        final float resDiff = ValueTools.makePositive(result-exp[i]);
        if (resDiff > 0.001f)
          assertEquals(exp[i], result);
      }
    } catch(junit.framework.AssertionFailedError e) {
      throw e;
    }
  }
  
  public void testRoundFloatA() {
    float in[]  = new float[] { 0.92f, 0.925f, 0.99f };
    int   dp[]  = new int[]   { 1,     1,      1 };
    float exp[] = new float[] { 0.9f, 0.9f,    1.0f };
    
    for(int i=0; i < in.length; i++) {
      final float res = ValueTools.roundFloatToDecPlaces(in[i], dp[i]);
      assertEquals(exp[i], res);
    }
  }
  
  public void testRoundFloatB() {
    float in[]  = new float[] { 0.922f, 0.925f, 0.999f };
    int   dp[]  = new int[]   { 2,      2,      2 };
    float exp[] = new float[] { 0.92f,  0.93f,  1.0f };
    
    for(int i=0; i < in.length; i++) {
      final float res = ValueTools.roundFloatToDecPlaces(in[i], dp[i]);
      assertEquals(exp[i], res);
    }
  }
  
  public void testGetIdealRowsAndCols() {
    Point p = new Point();
                      // cells: 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
    int expRows[] = new int[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3 };
    int expCols[] = new int[] { 1, 2, 3, 4, 5, 3, 4, 4, 5, 5, 4, 4, 5, 5, 5, 6, 6, 6 };
    
    for(int i=1; i <= 18; i++) {
      ValueTools.getIdealRowsAndCols(i, p);
      assertEquals(expRows[i-1], p.y);
      assertEquals(expCols[i-1], p.x);
    }
    
  }
}
