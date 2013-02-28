package com.measuredsoftware.android.library2.game;

import android.graphics.PointF;

public class Vec2F extends PointF {
  public PointF start;
  public PointF end;
  
  public Vec2F() {
    this(0,0,0,0);
  }
  
  public Vec2F(float vx, float vy) {
    this(vx, vy, 0, 0);
  }

  public Vec2F(float vx, float vy, float sx, float sy) {
    super(vx,vy);
    start = new PointF(sx,sy);
    end = new PointF(sx+vx,sy+vy);
  }
  
  /*
   *  v.rx = -v.vy;
      v.ry = v.vx; 
      v.lx = v.vy;
      v.ly = -v.vx; 
   */
  public PointF getNormal(PointF p, boolean leftRight) {
    if (!leftRight) { // left
      p.x = this.y;
      p.y = -(this.x);
    } else {
      p.x = -(this.y);
      p.y = this.x;
    }
    
    return p;
  }
  
  public Vec2F add(PointF addThis) {
    this.x += addThis.x;
    this.y += addThis.y;
    
    return this;
  }
  
  public Vec2F multiply(int by) {
    this.x *= by;
    this.y *= by;
    
    return this;
  }
  
  public void set(Vec2F as) {
    this.x = as.x;
    this.y = as.y;
  }
  
  public Vec2F reset() {
    this.x = 0;
    this.y = 0;
    
    return this;
  }
  
  public PointF getReverse(PointF p) {
    p.x = -this.x;
    p.y = -this.y;
    
    return p;
  }
}
