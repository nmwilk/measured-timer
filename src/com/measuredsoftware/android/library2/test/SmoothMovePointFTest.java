package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.measuredsoftware.android.library2.utils.SmoothMovePointF;

public class SmoothMovePointFTest extends TestCase {

  public void testSimpleA1() {
    SmoothMovePointF mover = new SmoothMovePointF(1000, 2000, 100, 100, 200, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(100+exp[i], Math.round(working.x));
          assertEquals(100+exp[i], Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }

  public void testSimpleA2() {
    SmoothMovePointF mover = new SmoothMovePointF();
    mover.init(1000, 2000, 100, 100, 200, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(100+exp[i], Math.round(working.x));
          assertEquals(100+exp[i], Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }
  
  public void testSimpleB1() {
    SmoothMovePointF mover = new SmoothMovePointF(1000, 2000, 100, 100, 200, 300);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(100+exp[i], Math.round(working.x));
          assertEquals(100+(2*exp[i]), Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }

  public void testSimpleB2() {
    SmoothMovePointF mover = new SmoothMovePointF();
    mover.init(1000, 2000, 100, 100, 200, 300);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(100+exp[i], Math.round(working.x));
          assertEquals(100+(2*exp[i]), Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }
  
  public void testNegativeA1() {
    SmoothMovePointF mover = new SmoothMovePointF(1000, 2000, 200, 200, 100, 100);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(200-exp[i], Math.round(working.x));
          assertEquals(200-exp[i], Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }  
  
  public void testNegativeA2() {
    SmoothMovePointF mover = new SmoothMovePointF();
    mover.init(1000, 2000, 200, 200, 100, 100);
    int exp[] = { 0, 19, 36, 51, 64, 75, 84, 91, 96, 99, 100 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          mover.move(1000+(i*100), working);
          assertEquals(200-exp[i], Math.round(working.x));
          assertEquals(200-exp[i], Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }  
  
  public void testUpdateA1() {
    SmoothMovePointF mover = new SmoothMovePointF();
    mover.init(1000, 2000, 100, 100, 200, 200);
    int exp[] = { 0, 19, 36, 51, 64, 75, 48, 27, 12, 3, 0 };
    {
      PointF working = new PointF();
      try {
        for(int i=0; i < 10; i++) {
          if (i == 5) {
            mover.updateEnd(1500, 100, 100);
          }
          
          mover.move(1000+(i*100), working);
          assertEquals(100+exp[i], Math.round(working.x));
          assertEquals(100+exp[i], Math.round(working.y));
        }
      } catch(junit.framework.AssertionFailedError e) {
        throw e;
      }
    }
  }  
}
