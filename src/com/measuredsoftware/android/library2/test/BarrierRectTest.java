package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;
import android.graphics.PointF;
import android.graphics.RectF;

import com.measuredsoftware.android.library2.game.BarrierRect;
import com.measuredsoftware.android.library2.game.Vec2F;

public class BarrierRectTest extends TestCase {
  public void testWallsRect1() {
    {
      PointF[] vecs = new PointF[4];
      vecs[0] = new PointF(0,1023-848);
      vecs[1] = new PointF(652-378,0);
      vecs[2] = new PointF(0,848-1023);
      vecs[3] = new PointF(378-652,0);
      BarrierRect pit = new BarrierRect(378,0,vecs);
      
      RectF rect = pit.mRect;
      assertEquals(378, rect.left);
      assertEquals(652, rect.right);
      assertEquals(0, rect.top);
      assertEquals(175, rect.bottom);
    }
  }
  
  public void testWallsRect2() {
    {
      PointF[] vecs = new PointF[4];
      vecs[0] = new PointF(0,1023-848);
      vecs[1] = new PointF(652-378,0);
      vecs[2] = new PointF(0,848-1023);
      vecs[3] = new PointF(378-652,0);
      BarrierRect pit = new BarrierRect(478,10,vecs);
      
      RectF rect = pit.mRect;
      assertEquals(478, rect.left);
      assertEquals(752, rect.right);
      assertEquals(10, rect.top);
      assertEquals(185, rect.bottom);
    }
  }  
  
  public void testCollision1() {
    PointF[] vecs = new PointF[4];
    vecs[0] = new PointF(0,200);
    vecs[1] = new PointF(20,0);
    vecs[2] = new PointF(0,-200);
    vecs[3] = new PointF(-20,0);
    BarrierRect pit = new BarrierRect(200,200,vecs);

    boolean res;
    PointF pointFRes = new PointF(0,0);
    Vec2F vec = new Vec2F();
    Vec2F wall;
    
    res = pit.mRect.contains(200, 300);
    assertEquals(true, res);
    vec.start.x = 200;
    vec.start.y = 300;
    vec.x = 0.5f;
    vec.y = 0.001f;
    wall = pit.findWallHit(vec, pointFRes);
    assertNotNull(wall);

    vec.start.x = 199.5f;
    vec.start.y = 300;
    vec.x = 0.6f;
    vec.y = 0.001f;
    wall = pit.findWallHit(vec, pointFRes);
    assertNotNull(wall);
    
    vec.start.x = 199.5f;
    vec.start.y = 300;
    vec.x = 0.5f;
    vec.y = 0.001f;
    wall = pit.findWallHit(vec, pointFRes);
    assertNotNull(wall);
  }
  
  public void testCollision2() {
    PointF[] vecs = new PointF[4];
    vecs[0] = new PointF(-80,273);
    vecs[1] = new PointF(14,4);
    vecs[2] = new PointF(80,-273);
    vecs[3] = new PointF(-14,-4);
    BarrierRect centreBarrier = new BarrierRect(443,1024-626,vecs);

    boolean res;
    PointF pointFRes = new PointF(0,0);
    Vec2F vec = new Vec2F();
    Vec2F wall;
    
    vec.start.x = 457.81696f;
    vec.start.y = 488.07834f;
    res = centreBarrier.mRect.contains(vec.start.x, vec.start.y);
    assertEquals(true, res);
    vec.x = -6.171992f;
    vec.y = 4.4842124f;
    wall = centreBarrier.findWallHit(vec, pointFRes);
    assertNotNull(wall);

   
  }  
}
