package com.measuredsoftware.android.library2.game;

import android.graphics.PointF;
import android.graphics.RectF;

import com.measuredsoftware.android.library2.utils.CoordTools;

public class BarrierRect {
  public Vec2F[] mWalls; // TODO make final as final optimisation step - to see if it works
  public final int mWallsCount;
  public RectF mRect;
  
  public BarrierRect(PointF start, PointF[] vecs) {
    mWallsCount = vecs.length;
    mWalls = new Vec2F[mWallsCount];
    init(start, vecs);
  }

  public BarrierRect(float startX, float startY, PointF[] vecs) {
    this(new PointF(startX, startY), vecs);
  }
  
  private void init(PointF start, PointF[] vecs) {
    // create walls from start pos and vectors
    {
      PointF currentPos = new PointF(start.x, start.y);
      
      int i = 0;
      while(i < mWallsCount) {
        PointF p = vecs[i];
        mWalls[i] = new Vec2F(p.x, p.y, currentPos.x, currentPos.y);
        currentPos.x += p.x;
        currentPos.y += p.y;
        
        ++i;
      }
    }
    
    // set the rect based on max and min values in walls
    float l=9999,t=9999,r=0,b=0;
    for(int i=0; i < mWallsCount; i++) {
      final Vec2F wall = mWalls[i];
      if (wall.start.x < l)
        l = wall.start.x;
      if (wall.start.x > r)
        r = wall.start.x;
      if (wall.start.y < t)
        t = wall.start.y;
      if (wall.start.y > b)
        b = wall.start.y;
    }
    
    mRect = new RectF(l,t,r,b);
  }
  
  public Vec2F findWallHit(Vec2F carVec, PointF intersect) {
    for(int i=0; i < mWallsCount; i++) {
      final Vec2F wall = mWalls[i];
      float i1 = CoordTools.findIntersectionPointF(carVec, wall, null);
      float i2 = CoordTools.findIntersectionPointF(wall, carVec, intersect);
      //Log.d("testgame","t1:"+i1+" t2:"+i2);
      if (i1 >= 0.0f && i1 <= 1.0f && i2 >= 0.0f && i2 <= 1.0f)
        return wall;
    }

    return null;
  }
}
