package com.measuredsoftware.android.library2.utils;

import android.graphics.PointF;

public class SmoothMovePointF extends SmoothMove {

  private PointF mStartPos;
  private PointF mMove;
  
  public SmoothMovePointF() {
    super();
    
    mStartPos   = new PointF();
    mMove       = new PointF();
  }
  
  public SmoothMovePointF(long startTime, long endTime, float startPosX, float startPosY, float endPosX, float endPosY) {
    this();
    init(startTime, endTime, startPosX, startPosY, endPosX, endPosY);
  }
  
  public void updateEnd(long currentTime, float newEndX, float newEndY) {
    // get the current progress
    final float progressMove = getProgress(currentTime);
    
    // change the start position using previous mover info
    mStartPos.x += (mMove.x*progressMove);
    mStartPos.y += (mMove.y*progressMove);
    
    super.update(currentTime);
    
    // update the mover with the new positions
    mMove.set(newEndX-mStartPos.x, newEndY-mStartPos.y);
  }

  public void init(long startTime, long endTime, float startPosX, float startPosY, float endPosX, float endPosY) {
    super.init(startTime, endTime);
    mStartPos.set(startPosX, startPosY);
    mMove.set(endPosX-startPosX, endPosY-startPosY);
  }
  
  @Override
  public void move(long currentTime, Object instance) {
    PointF currentPos = (PointF)instance;
    
    // =(9*A3)-(4.70*(A3*A3))
    // =(2*A3)-((A3*A3))
    final float progressMove = getProgress(currentTime);
    
    // now set the position
    currentPos.x = mStartPos.x+(mMove.x*progressMove);
    currentPos.y = mStartPos.y+(mMove.y*progressMove);
  }
}
