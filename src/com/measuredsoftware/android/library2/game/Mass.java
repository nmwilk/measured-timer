package com.measuredsoftware.android.library2.game;

public class Mass {
  private static final int DEFAULT_FORCE_LIST_SIZE = 5;
  
  public Vec2 mPos;
  public Vec2 mVel;
  public Vec2 mAccel;
  
  public Vec2List mForces;
  public Vec2List mImpulses;
  
  private static Vec2 cache;
  
  public Mass() {
    mForces = new Vec2List(DEFAULT_FORCE_LIST_SIZE);
    mImpulses = new Vec2List(DEFAULT_FORCE_LIST_SIZE);
    
    cache = new Vec2(0,0);
  }
  
  public void addForce(Vec2 force) {
    mForces.add(force);
  }
  
  public void addImpulse(Vec2 force) {
    mImpulses.add(force);
  }
  
  public void onUpdate(int time) {
    cache.reset();
    
    final int forcesSize = mForces.mCurrentSize;
    for(int i=0; i < forcesSize; i++) {
      if (!mForces.getActive(i))
        continue;
      
      cache.add(mForces.get(i, false));
    }
    
    final int impulsesSize = mImpulses.mCurrentSize;
    for(int j=0; j < impulsesSize; j++) {
      cache.add(mImpulses.get(j,true));
    }
    
    mAccel.set(cache);
    mVel.add(mAccel.multiply(time));
    mPos.add(mVel.multiply(time));
  }
}
