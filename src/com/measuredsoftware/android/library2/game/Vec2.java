package com.measuredsoftware.android.library2.game;

import android.graphics.Point;
import android.graphics.PointF;

public class Vec2 extends Point {
  public Point start;
  
  public Vec2() {
    this(0,0,0,0);
  }
  
  public Vec2(int vx, int vy) {
    this(vx, vy, 0, 0);
  }

  public Vec2(int vx, int vy, int sx, int sy) {
    super(vx,vy);
    start = new Point(sx,sy);
  }
  
  /*
   *  v.rx = -v.vy;
      v.ry = v.vx; 
      v.lx = v.vy;
      v.ly = -v.vx; 
   */
  public Point getNormal(Point p, boolean leftRight) {
    if (!leftRight) { // left
      p.x = this.y;
      p.y = -(this.x);
    } else {
      p.x = -(this.y);
      p.y = this.x;
    }
    
    return p;
  }
  
  public static PointF getNormalised(PointF p) {
    // v.len=Math.sqrt(v.vx*v.vx+v.vy*v.vy);
    // v.dx=v.vx/v.len;
    // v.dy=v.vy/v.len;
    float len = (float)Math.sqrt((p.x*p.x)+(p.y*p.y));
    p.x = p.x/len;
    p.y = p.y/len;
    
    return p;
  }
  
  public Vec2 add(Point addThis) {
    this.x += addThis.x;
    this.y += addThis.y;
    
    return this;
  }
  
  public Vec2 multiply(int by) {
    this.x *= by;
    this.y *= by;
    
    return this;
  }
  
  public void set(Vec2 as) {
    this.x = as.x;
    this.y = as.y;
  }
  
  public Vec2 reset() {
    this.x = 0;
    this.y = 0;
    
    return this;
  }
  
  public Point getReverse(Point p) {
    p.x = -this.x;
    p.y = -this.y;
    
    return p;
  }
}
