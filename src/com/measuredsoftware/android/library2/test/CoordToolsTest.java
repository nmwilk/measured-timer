package com.measuredsoftware.android.library2.test;

import java.text.DecimalFormat;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.measuredsoftware.android.library2.game.Vec2;
import com.measuredsoftware.android.library2.game.Vec2F;
import com.measuredsoftware.android.library2.utils.CoordTools;
import com.measuredsoftware.android.library2.utils.ValueTools;

public class CoordToolsTest extends TestCase {

  public void testWithinAngle() {
    assertEquals(true, CoordTools.withinAngle(90f, 70f, 21f));
    assertEquals(true, CoordTools.withinAngle(90f, 70f, 20f));
    assertEquals(false, CoordTools.withinAngle(90f, 70f, 19f));
    
    assertEquals(true, CoordTools.withinAngle(50f, 70f, 21f));
    assertEquals(true, CoordTools.withinAngle(50f, 70f, 20f));
    assertEquals(false, CoordTools.withinAngle(50f, 70f, 19f));
    
    assertEquals(true, CoordTools.withinAngle(350f, 10f, 21f));
    assertEquals(true, CoordTools.withinAngle(30f, 10f, 21f));
    assertEquals(false, CoordTools.withinAngle(350f, 10f, 19f));
    assertEquals(false, CoordTools.withinAngle(30f, 10f, 19f));

    assertEquals(true, CoordTools.withinAngle(180f, 160f, 21f));
    assertEquals(true, CoordTools.withinAngle(180f, 160f, 20f));
    assertEquals(false, CoordTools.withinAngle(180f, 160f, 19f));

    assertEquals(true, CoordTools.withinAngle(220f, 200f, 21f));
    assertEquals(true, CoordTools.withinAngle(220f, 200f, 20f));
    assertEquals(false, CoordTools.withinAngle(220f, 200f, 19f));

    assertEquals(true, CoordTools.withinAngle(270f, 250f, 21f));
    assertEquals(true, CoordTools.withinAngle(270f, 250f, 20f));
    assertEquals(false, CoordTools.withinAngle(270f, 250f, 19f));

    assertEquals(true, CoordTools.withinAngle(10f, 350f, 21f));
    assertEquals(true, CoordTools.withinAngle(330f, 350f, 21f));
    assertEquals(false, CoordTools.withinAngle(10f, 350f, 19f));
    assertEquals(false, CoordTools.withinAngle(330f, 350f, 19f));
    
    float start = 0;
    final float bounds = 20f;
    float lowerstart = start-19f;
    float upperstart = start+19f;
    int i=0;
    try {
      for(; i < 360; i++) {
        start = ValueTools.makeValidAngle(start);
        lowerstart = ValueTools.makeValidAngle(lowerstart);
        upperstart = ValueTools.makeValidAngle(upperstart);
        assertEquals(true, CoordTools.withinAngle(start, lowerstart, bounds));
        assertEquals(true, CoordTools.withinAngle(start, upperstart, bounds));
        ++start;
        ++lowerstart;
        ++upperstart;
      }
    } catch (junit.framework.AssertionFailedError e) {
      // break to here to capture which test within the loop failed
      throw e;
    }
  }
  
  public void testWithinAngleFloat() {
    assertEquals(-0.5f, CoordTools.withinAngleFloat(60, 30, 60));
    assertEquals(-0.25f, CoordTools.withinAngleFloat(120, 105, 60));
    assertEquals(-0.75f, CoordTools.withinAngleFloat(120, 75, 60));
    assertEquals(0.5f, CoordTools.withinAngleFloat(30, 60, 60));
    assertEquals(0.25f, CoordTools.withinAngleFloat(105, 120, 60));
    assertEquals(0.75f, CoordTools.withinAngleFloat(75, 120, 60));

    // > 1 || < -1
    assertEquals(-1.5f, CoordTools.withinAngleFloat(120, 30, 60));
    assertEquals(-1.25f, CoordTools.withinAngleFloat(180, 105, 60));
    assertEquals(-1.75f, CoordTools.withinAngleFloat(180, 75, 60));
    assertEquals(1.5f, CoordTools.withinAngleFloat(30, 120, 60));
    assertEquals(1.25f, CoordTools.withinAngleFloat(105, 180, 60));
    assertEquals(1.75f, CoordTools.withinAngleFloat(75, 180, 60));
  }
  
  public void testAllCornersCoords() {
    PointF lf = new PointF();
    PointF rf = new PointF();
    PointF lr = new PointF();
    PointF rr = new PointF();
    
    //////////////////////////
    // 90 deg blocks
    //////////////////////////
    CoordTools.calculateCorners(5, 360f, 36.87f, lf, rf, lr, rr);
    assertEquals(-3, Math.round(lf.x));
    assertEquals(4, Math.round(lf.y));
    assertEquals(3, Math.round(rf.x));
    assertEquals(4, Math.round(rf.y));
    assertEquals(-3, Math.round(lr.x));
    assertEquals(-4, Math.round(lr.y));
    assertEquals(3, Math.round(rr.x));
    assertEquals(-4, Math.round(rr.y));

    CoordTools.calculateCorners(5, 450f, 36.87f, lf, rf, lr, rr);
    assertEquals(4, Math.round(lf.x));
    assertEquals(3, Math.round(lf.y));
    assertEquals(4, Math.round(rf.x));
    assertEquals(-3, Math.round(rf.y));
    assertEquals(-4, Math.round(lr.x));
    assertEquals(3, Math.round(lr.y));
    assertEquals(-4, Math.round(rr.x));
    assertEquals(-3, Math.round(rr.y));

    CoordTools.calculateCorners(5, 540f, 36.87f, lf, rf, lr, rr);
    assertEquals(3, Math.round(lf.x));
    assertEquals(-4, Math.round(lf.y));
    assertEquals(-3, Math.round(rf.x));
    assertEquals(-4, Math.round(rf.y));
    assertEquals(3, Math.round(lr.x));
    assertEquals(4, Math.round(lr.y));
    assertEquals(-3, Math.round(rr.x));
    assertEquals(4, Math.round(rr.y));
    
    CoordTools.calculateCorners(5, 630f, 36.87f, lf, rf, lr, rr);
    assertEquals(-4, Math.round(lf.x));
    assertEquals(-3, Math.round(lf.y));
    assertEquals(-4, Math.round(rf.x));
    assertEquals(3, Math.round(rf.y));
    assertEquals(4, Math.round(lr.x));
    assertEquals(-3, Math.round(lr.y));
    assertEquals(4, Math.round(rr.x));
    assertEquals(3, Math.round(rr.y));    
    
    //////////////////////////
    // 45 deg blocks
    //////////////////////////
    CoordTools.calculateCorners(50, 405f, 36.87f, lf, rf, lr, rr);
    assertEquals(7, Math.round(lf.x));
    assertEquals(49, Math.round(lf.y));
    assertEquals(49, Math.round(rf.x));
    assertEquals(7, Math.round(rf.y));
    assertEquals(-49, Math.round(lr.x));
    assertEquals(-7, Math.round(lr.y));
    assertEquals(-7, Math.round(rr.x));
    assertEquals(-49, Math.round(rr.y));    
    
    CoordTools.calculateCorners(50, 495f, 36.87f, lf, rf, lr, rr);
    assertEquals(49, Math.round(lf.x));
    assertEquals(-7, Math.round(lf.y));
    assertEquals(7, Math.round(rf.x));
    assertEquals(-49, Math.round(rf.y));
    assertEquals(-7, Math.round(lr.x));
    assertEquals(49, Math.round(lr.y));   
    assertEquals(-49, Math.round(rr.x));
    assertEquals(7, Math.round(rr.y));
    
    CoordTools.calculateCorners(50, 585f, 36.87f, lf, rf, lr, rr);
    assertEquals(-7, Math.round(lf.x));
    assertEquals(-49, Math.round(lf.y));
    assertEquals(-49, Math.round(rf.x));
    assertEquals(-7, Math.round(rf.y));
    assertEquals(49, Math.round(lr.x));
    assertEquals(7, Math.round(lr.y));
    assertEquals(7, Math.round(rr.x));
    assertEquals(49, Math.round(rr.y));      

    CoordTools.calculateCorners(50, 675f, 36.87f, lf, rf, lr, rr);
    assertEquals(-49, Math.round(lf.x));
    assertEquals(7, Math.round(lf.y));
    assertEquals(-7, Math.round(rf.x));
    assertEquals(49, Math.round(rf.y));
    assertEquals(7, Math.round(lr.x));
    assertEquals(-49, Math.round(lr.y));   
    assertEquals(49, Math.round(rr.x));
    assertEquals(-7, Math.round(rr.y));
  }
  
  public void testCalcDrawOffsetGLRotate() {
    PointF point = new PointF(0,0);
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    
    CoordTools.calcDrawOffsetGLRotate(0, 22.63f, point);
    assertEquals(-16, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(-16, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(45, 22.63f, point);
    assertEquals(-23, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(0, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(90, 22.63f, point);
    assertEquals(-16, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(16, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(135, 22.63f, point);
    assertEquals(0, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(23, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(180, 22.63f, point);
    assertEquals(16, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(16, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(225, 22.63f, point);
    assertEquals(23, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(0, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(270, 22.63f, point);
    assertEquals(16, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(-16, Math.round(Float.valueOf(twoDForm.format(point.y))));

    CoordTools.calcDrawOffsetGLRotate(315, 22.63f, point);
    assertEquals(0, Math.round(Float.valueOf(twoDForm.format(point.x))));
    assertEquals(-23, Math.round(Float.valueOf(twoDForm.format(point.y))));
  }
  
  public void testGetVelocityFromAngleAndSpeed() {
    PointF res = new PointF();
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    
    CoordTools.getVelocityFromAngleAndSpeed(60, 5, res);
    assertEquals(4.33f, Float.valueOf(twoDForm.format(res.x)));
    assertEquals(2.50f, Float.valueOf(twoDForm.format(res.y)));

    CoordTools.getVelocityFromAngleAndSpeed(120, 5, res);
    assertEquals(4.33f, Float.valueOf(twoDForm.format(res.x)));
    assertEquals(-2.50f, Float.valueOf(twoDForm.format(res.y)));

    CoordTools.getVelocityFromAngleAndSpeed(180, 5, res);
    assertEquals(-0.00f, Float.valueOf(twoDForm.format(res.x))); // ignore -0.00 for now, doesn't matter in game
    assertEquals(-5.00f, Float.valueOf(twoDForm.format(res.y)));

    CoordTools.getVelocityFromAngleAndSpeed(240, 5, res);
    assertEquals(-4.33f, Float.valueOf(twoDForm.format(res.x)));
    assertEquals(-2.50f, Float.valueOf(twoDForm.format(res.y)));

    CoordTools.getVelocityFromAngleAndSpeed(300, 5, res);
    assertEquals(-4.33f, Float.valueOf(twoDForm.format(res.x)));
    assertEquals(2.50f, Float.valueOf(twoDForm.format(res.y)));
  }
  
  public void testGetAngleFromVelocity45s() {
    assertEquals(135f, CoordTools.getAngleFromVelocity(3, -3));
    assertEquals(45f, CoordTools.getAngleFromVelocity(3, 3));
    assertEquals(315f, CoordTools.getAngleFromVelocity(-3, 3));
    assertEquals(225f, CoordTools.getAngleFromVelocity(-3, -3));
  }

  public void testGetAngleFromVelocity() {
    assertEquals(60, Math.round(CoordTools.getAngleFromVelocity(4.33f, 2.50f)));
    assertEquals(120,  Math.round(CoordTools.getAngleFromVelocity(4.33f, -2.5f)));
    assertEquals(180,  Math.round(CoordTools.getAngleFromVelocity(0f, -5f)));
    assertEquals(240,  Math.round(CoordTools.getAngleFromVelocity(-4.33f, -2.5f)));
    assertEquals(300,  Math.round(CoordTools.getAngleFromVelocity(-4.33f, 2.5f)));
  }
  
  public void testFindIntersectionPointF() {
    {
      
      PointF intersect = new PointF();
      Vec2F v1 = new Vec2F(3,12,3,0);
      Vec2F v2 = new Vec2F(-12,4,14,5);
      
      // floats
      float t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);
      t = CoordTools.findIntersectionPointF(v2, v1, intersect);
      assertEquals(0.75f, t);
      
      v2 = new Vec2F(12,-4,2,9);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);
      t = CoordTools.findIntersectionPointF(v2, v1, intersect);
      assertEquals(0.25f, t);
      
      v2 = new Vec2F(-13,-3,14,10);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.65986395f, t);
      assertEquals(4.979592f,intersect.x);
      assertEquals(7.9183674f,intersect.y);      
      
      v2 = new Vec2F(-13,0,14,8);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);       
      
      v2 = new Vec2F(-6,6,8,5);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);    
      
      v2 = new Vec2F(-9,3,14,5);
      
      t = CoordTools.findIntersectionPointF(v2, v1, intersect);
      assertEquals(1.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);          
      
      // future intersect
      v1 = new Vec2F(1,4,3,0);
      v2 = new Vec2F(-12,4,14,5);
      
      // floats
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(2.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);
      
      v2 = new Vec2F(12,-4,2,9);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(2.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);
      
      v2 = new Vec2F(-13,-3,14,10);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(1.9795918f, t);
      assertEquals(4.979592f,intersect.x);
      assertEquals(7.9183674f,intersect.y);      
      
      v2 = new Vec2F(-13,0,14,8);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(2.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);       
      
      v2 = new Vec2F(-6,6,8,5);
      
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(2.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);    
      
      v2 = new Vec2F(-9,3,14,5);
      
      t = CoordTools.findIntersectionPointF(v2, v1, intersect);
      assertEquals(1.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);          

      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(2.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);          

      v2 = new Vec2F(-12,4,14,5);
      
      t = CoordTools.findIntersectionPointF(v2, v1, intersect);
      assertEquals(0.75f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);  
      
      v1 = new Vec2F(1.5f,2.5f,1,1);
      v2 = new Vec2F(-2,2,5,0);
      PointF intersect2 = new PointF();
      t = CoordTools.findIntersectionPointF(v1, v2, intersect);
      assertEquals(0.75f, t);
      t = CoordTools.findIntersectionPointF(v2, v1, intersect2);
      assertEquals(1.4375f, t);
      
      assertEquals(intersect2.x, intersect.x);
      assertEquals(intersect2.y, intersect.y);
    }
    
    // ints
    {
      PointF intersect = new PointF();

      Vec2 v1 = new Vec2(3,12,3,0);
      Vec2 v2 = new Vec2(-12,4,14,5);
      
      float t = CoordTools.findIntersectionPoint(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);
      
      v2 = new Vec2(12,-4,2,9);
      
      t = CoordTools.findIntersectionPoint(v1, v2, intersect);
      assertEquals(0.6666667f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);        
      
      v2 = new Vec2(-9,3,14,5);
      
      t = CoordTools.findIntersectionPoint(v2, v1, intersect);
      assertEquals(1.0f, t);
      assertEquals(5.0f,intersect.x);
      assertEquals(8.0f,intersect.y);           
    }
  }
  
  public void testGetAngleDifferenceAbs() {
    // answer should always be 30
    {
      float angleA = 0;
      float angleB = 30;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifferenceAbs(angleA, angleB);
        assertEquals(30, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }

    // answer should always be 60
    {
      float angleA = 0;
      float angleB = 60;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifferenceAbs(angleA, angleB);
        assertEquals(60, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }
  }
  
  public void testGetAngleDifference() {
    // answer should always be -30
    {
      float angleA = 0;
      float angleB = 30;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifference(angleA, angleB);
        assertEquals(-30, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }

    // answer should always be -60
    {
      float angleA = 0;
      float angleB = 60;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifference(angleA, angleB);
        assertEquals(-60, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }
  }  
  
  public void testGetAngleDifference2() {
    // answer should always be 30
    {
      float angleA = 0;
      float angleB = 30;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifference(angleB, angleA);
        assertEquals(30, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }

    // answer should always be 60
    {
      float angleA = 0;
      float angleB = 60;
      for(int i=0; i < 360; i++) {
        final float diff = CoordTools.getAngleDifference(angleB, angleA);
        assertEquals(60, (int)diff);
        ++angleA;
        ++angleB;
        if (angleA >= 360)
          angleA %= 360;
        if (angleB >= 360)
          angleB %= 360;
      }
    }
  }    
}
