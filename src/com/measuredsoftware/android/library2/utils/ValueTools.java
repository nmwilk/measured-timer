package com.measuredsoftware.android.library2.utils;

import android.graphics.Point;


public class ValueTools {

  public static final double GRAVITATIONAL_CONSTANT = 0.0667;

  public static float limitTo(float in, float limit) {
    boolean negative = false;
    
    if (in < 0.0f) {
      negative = true;
      in = -in;
    }
    
    if (in > limit)
      in = limit;

    if (negative)
      in = -in;
    
    return in;
  }
  
  public static float noNegative(float in) {
    if (in < 0)
      in = 0;
    
    return in;
  }

  public static float makePositive(float in) {
    if (in < 0)
      in = -in;
    
    return in;
  }
  
  public static float minValue(float value, float minAllowed) {
    if (value < minAllowed)
      value = minAllowed;
    
    return value;
  }
  
  public static float maxValue(float value, float maxAllowed) {
    if (value > maxAllowed)
      value = maxAllowed;
    
    return value;
  }
  
  public static float forceWithin(float value, float minAllowed, float maxAllowed) {
    if (value < minAllowed)
      return minAllowed;
    if (value > maxAllowed)
      return maxAllowed;
    
    return value;
  }

  /*
   * is current value within +/-bounds of comparison?
   * 
   */
  public static boolean within(float current, float comparison, float bounds) {
    return ((current-bounds > comparison) && (current+bounds < comparison));
  }
  
  public static int withinInt(float current, float comparison, float bounds) {
    if (current-bounds < comparison)
      return -1;
    
    if (current+bounds > comparison)
      return 1;
    
    return 0;
  }
  
  public static float progressInRange(float value, float min, float max) {
    if (value <= min)
      return 0f;
    
    if (value >= max)
      return 1.0f;
    
    return ((value-min)/(max-min));
  }

  public static float getPositiveDiff(float mPrevCamX, float targetsX) {
    if (targetsX > mPrevCamX)
      return targetsX - mPrevCamX;
    
    return mPrevCamX - targetsX;
  }

  // will return -2 as 358, and 362 to as 2, and etc.
  public static float makeValidAngle(float start) {
    while (start < 0) {
      start += 360f;
    }
    
    if (start > 360)
      return start%360f;
    
    return start;
  }

  /**
   * forces the supplied value (current) to be within the supplied range
   * @param min
   * @param max
   * @param current
   * @return
   */
  public static float withinAdjust(float min, float max, float current) {
    if (current < min)
      return min;
    
    if (current > max)
      return max;
    
    return current;
  }
  
  /**
   * rounds a float to a specified number of dec places. e.g. parameters 0.925 and 2 would give the result 0.93.
   * @param value value to round
   * @param decPlaces number of decimal places
   * @return
   */
  public static float roundFloatToDecPlaces(float value, int decPlaces) {
    // get number to multiply by. e.g. 1/10/100/1000
    final int multiplyBy = (int)Math.pow(10, decPlaces);
    value *= multiplyBy;
    value = Math.round(value);
    return (value / multiplyBy);
  }

  public static boolean opposite(float x, float x2) {
    return ((x < 0 && x2 > 0) || (x > 0 && x2 < 0));
  }
  
  

  /**
   * fills out the Point with the number of rows and cols for a nice fit (approx 3:2 ratio)
   * given the supplied number of cells
   * @param cells
   * @param rowsCols
   */
  
  /*
                                    // cells: 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
  private static int mExpRows[] = new int[] { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3 };
  private static int mexpCols[] = new int[] { 1, 2, 3, 4, 5, 3, 4, 4, 5, 5, 4, 4, 5, 5, 5, 6, 6, 6 };
  */
  public static void getIdealRowsAndCols(int cells, Point rowsCols) {
    if (cells < 5) {
      rowsCols.x = cells;
      rowsCols.y = 1;
      return;
    }

    final int cols = (cells < 11) ? 2 : 3;
    
    rowsCols.y = cols;
    rowsCols.x = cells/cols;
    if ((rowsCols.x * rowsCols.y) < cells)
      ++rowsCols.x;
  }

  public static String bytesToHexString(byte[] data) {
    if (data == null)
      return new String();
    
    final int len = data.length;
    StringBuffer s = new StringBuffer();
    for(int i=0; i < len; i++) {
      String hex = Integer.toHexString(0xFF & data[i]);
      if (hex.length() == 1) {
        s.append('0');
      }
      s.append(hex);
    }
    
    return s.toString();
  }  
}
