package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.FaderFloat;

public class FaderFloatTest extends TestCase {

  public void testOutA() {
    FaderFloat ff = new FaderFloat(FaderFloat.FADE_OUT);
    ff.setValue(100f);
    ff.setFadeTime(1000);      
    assertEquals(100, (int)ff.getCurrentValue(9000));
    assertEquals(100, (int)ff.getCurrentValue(9500));
    ff.setEnded(10000);
    for(int i=1; i < 10; i++) {
      assertEquals(100-(i*10), Math.round(ff.getCurrentValue(10000+(i*100))));
    }
  }
  
  public void testOutNegativeA() {
    FaderFloat ff = new FaderFloat(FaderFloat.FADE_OUT);
    ff.setValue(-100f);
    ff.setFadeTime(1000);      
    assertEquals(-100, (int)ff.getCurrentValue(9000));
    assertEquals(-100, (int)ff.getCurrentValue(9500));
    ff.setEnded(10000);
    for(int i=1; i < 10; i++) {
      int exp = -100+(i*10);
      assertEquals(exp, Math.round(ff.getCurrentValue(10000+(i*100))));
    }
  }
  
  public void testInA() {
    FaderFloat ff = new FaderFloat(FaderFloat.FADE_IN);
    ff.setValue(100f);
    ff.setFadeTime(1000);      
    assertEquals(0, (int)ff.getCurrentValue(9000));
    assertEquals(0, (int)ff.getCurrentValue(9500));
    ff.setEnded(10000);
    for(int i=1; i < 10; i++) {
      assertEquals((i*10), Math.round(ff.getCurrentValue(10000+(i*100))));
    }
  }
  
  public void testInNegativeA() {
    FaderFloat ff = new FaderFloat(FaderFloat.FADE_IN);
    ff.setValue(-100f);
    ff.setFadeTime(1000);      
    assertEquals(0, (int)ff.getCurrentValue(9000));
    assertEquals(0, (int)ff.getCurrentValue(9500));
    ff.setEnded(10000);
    for(int i=1; i < 10; i++) {
      int exp = -(i*10);
      assertEquals(exp, Math.round(ff.getCurrentValue(10000+(i*100))));
    }
  }  
}
