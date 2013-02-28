package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.SmoothMoveFloat;

public class SmoothMoveFloatTest extends TestCase {

  public void testSimpleA1() {
    SmoothMoveFloat mover = new SmoothMoveFloat();
    mover.init(1000, 2000, 100, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      try {
        for(int i=0; i < 10; i++) {
          final float result = mover.move(1000+(i*100));
          assertEquals(100+exp[i], Math.round(result));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }

  public void testSimpleB1() {
    SmoothMoveFloat mover = new SmoothMoveFloat();
    mover.init(1000, 2000, 100, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      try {
        for(int i=0; i < 10; i++) {
          final float result = mover.move(1000+(i*100));
          assertEquals(100+exp[i], Math.round(result));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }

  public void testNegative1() {
    SmoothMoveFloat mover = new SmoothMoveFloat();
    mover.init(1000, 2000, 200, 100);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      try {
        for(int i=0; i < 10; i++) {
          final float result = mover.move(1000+(i*100));
          assertEquals(200-exp[i], Math.round(result));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }  
  
  public void testUpdateA1() {
    SmoothMoveFloat mover = new SmoothMoveFloat();
    mover.init(1000, 2000, 100, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 48, 27, 12, 3, 0 };
    {
      try {
        for(int i=0; i < 10; i++) {
          if (i == 5) {
            mover.updateEnd(1500, 100);
          }
          
          final float result = mover.move(1000+(i*100));
          assertEquals(100+exp[i], Math.round(result));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }   
}
