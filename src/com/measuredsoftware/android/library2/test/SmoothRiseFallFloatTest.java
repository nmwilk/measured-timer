package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.SmoothRiseFallFloat;

public class SmoothRiseFallFloatTest extends TestCase {

  public void testA() {
    SmoothRiseFallFloat s = new SmoothRiseFallFloat();
    s.init(0, 1000, 0, 100);
    assertEquals(0,   Math.round(s.move(0)));
    assertEquals(100, Math.round(s.move(500)));
    
    s.updateStart(1000);
    assertEquals(0,   Math.round(s.move(1000)));
    assertEquals(100, Math.round(s.move(1500)));
    assertEquals(0,   Math.round(s.move(2000)));
    s.updateStart(2000);
    assertEquals(0,   Math.round(s.move(2000)));
    assertEquals(100, Math.round(s.move(2500)));
    assertEquals(0,   Math.round(s.move(3000)));
    s.updateStart(3000);
    assertEquals(0,   Math.round(s.move(3000)));
    assertEquals(100, Math.round(s.move(3500)));
    assertEquals(0,   Math.round(s.move(4000)));
    s.updateStart(4000);
    assertEquals(0,   Math.round(s.move(4000)));
    assertEquals(100, Math.round(s.move(4500)));
    assertEquals(0,   Math.round(s.move(5000)));
  }
  
  public void testB() {
    SmoothRiseFallFloat s = new SmoothRiseFallFloat();
    s.init(0, 1000, 0, 100);
    assertEquals(0,   Math.round(s.move(0)));
    assertEquals(75, Math.round(s.move(250)));
    assertEquals(75, Math.round(s.move(750)));
    
    s.updateStart(1000);
    assertEquals(0,   Math.round(s.move(1000)));
    assertEquals(75, Math.round(s.move(1250)));
    assertEquals(75, Math.round(s.move(1750)));
    s.updateStart(2000);
    assertEquals(0,   Math.round(s.move(2000)));
    assertEquals(75, Math.round(s.move(2250)));
    assertEquals(75, Math.round(s.move(2750)));
    s.updateStart(3000);
    assertEquals(0,   Math.round(s.move(3000)));
    assertEquals(75, Math.round(s.move(3250)));
    assertEquals(75, Math.round(s.move(3750)));
    s.updateStart(4000);
    assertEquals(0,   Math.round(s.move(4000)));
    assertEquals(75, Math.round(s.move(4250)));
    assertEquals(75, Math.round(s.move(4750)));
  }  
}
