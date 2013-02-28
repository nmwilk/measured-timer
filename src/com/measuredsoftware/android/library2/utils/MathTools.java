package com.measuredsoftware.android.library2.utils;

public class MathTools {

  /**
   * Find the smallest power of two >= the input value.
   * (Doesn't work for negative numbers.)
   */
  public static int roundUpPower2(int x) {
    x = x - 1;
    x = x | (x >> 1);
    x = x | (x >> 2);
    x = x | (x >> 4);
    x = x | (x >> 8);
    x = x | (x >>16);
    return Math.round(x + 1);
  }
  
  public static int roundUpPower2(float x) {
    return roundUpPower2((int)x);
  }
  
  public static int roundToNearest(int toRound, int nearest) {
    int rem = toRound % nearest;
    int rounded = toRound/nearest;
    rounded *= nearest;
    if (rem >= (nearest/2))
      rounded += nearest;
    
    return rounded;
  }
}
