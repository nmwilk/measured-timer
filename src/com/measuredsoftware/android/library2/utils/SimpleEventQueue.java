package com.measuredsoftware.android.library2.utils;

import android.view.KeyEvent;
import android.view.MotionEvent;

public class SimpleEventQueue {
  public class SimpleEvent {
    public static final int TYPE_MOTION = 0;
    public static final int TYPE_KEY    = 1;
    
    public int x;
    public int y;
    public int action; // ACTION_DOWN / UP ETC
    public int pid;
    public long time;
    public int keyCode; 
    public int type; // one of TYPE_XXX above 
    
    public SimpleEvent(int _x, int _y, int _action, int pid, long _time) {
      x = _x;
      y = _y;
      action = _action;
      time = _time;
      keyCode = -1;
      type = TYPE_MOTION;
    }
  }

  public final int maxSize;
  public SimpleEvent[] theQueue;

  public int currentPos; // current position marker, -1 means queue is empty
  public int nextPos;    // next space
  
  public SimpleEventQueue(int _maxSize) {
    maxSize = _maxSize;
    theQueue = new SimpleEvent[maxSize];
    for(int i=0; i < maxSize; i++) {
      theQueue[i] = new SimpleEvent(0, 0, -1, 0, 0);
    }
    
    clear();
  }
  
  public void set(MotionEvent e) {
    //dumpEvent(e);
    // determine which x,y we're getting
    final int actionCode = e.getAction() & MotionEvent.ACTION_MASK;
    int actionPid = -1;
    if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
      actionPid = (e.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);  
      //Log.d("pr","pid: "+actionPid);
    }
    
    for (int i = 0; i < e.getPointerCount(); i++) {
      SimpleEvent temp = getNextForWriting();
      incPos();
      temp.pid  = e.getPointerId(i);
      temp.x    = (int)e.getX(i);
      temp.y    = (int)e.getY(i);
      temp.time = e.getEventTime();
      temp.type = SimpleEvent.TYPE_MOTION;
      if (actionPid == i) {
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN)
          temp.action = MotionEvent.ACTION_DOWN;
        else if (actionCode == MotionEvent.ACTION_POINTER_UP)
          temp.action = MotionEvent.ACTION_UP;
      } else if (actionPid == -1){
        temp.action = e.getAction();
      } else {
        temp.action = MotionEvent.ACTION_MOVE;
      }
    }    
  }
  
  public void set(KeyEvent ke) {
    SimpleEvent temp = getNextForWriting();
    incPos();
    temp.type = SimpleEvent.TYPE_KEY;
    temp.keyCode = ke.getKeyCode();
    temp.action = ke.getAction();
    temp.time = ke.getEventTime();
  }
  
  String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
  private void dumpEvent(MotionEvent event) {
    StringBuilder sb = new StringBuilder();
    int action = event.getAction();
    int actionCode = action & MotionEvent.ACTION_MASK;
    sb.append("event ACTION_" ).append(names[actionCode]);
    if (actionCode == MotionEvent.ACTION_POINTER_DOWN
          || actionCode == MotionEvent.ACTION_POINTER_UP) {
       sb.append("(pid " ).append(
       action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
       sb.append(")" );
    }
    sb.append("[" );
    for (int i = 0; i < event.getPointerCount(); i++) {
       sb.append("#" ).append(i);
       sb.append("(pid " ).append(event.getPointerId(i));
       sb.append(")=" ).append((int) event.getX(i));
       sb.append("," ).append((int) event.getY(i));
       if (i + 1 < event.getPointerCount())
          sb.append(";" );
    }
    sb.append("]" );
    //Log.d("pr", sb.toString());
 }  

  private void incPos() {
    ++nextPos;
    // cycle back to zero
    if (nextPos == maxSize)
      nextPos = 0;
  }
  
  public SimpleEvent getNextForWriting() {
    // full? start overwriting...
    if (currentPos == nextPos) {
      //Log.d("wh","eventqueue FULL");
      ++currentPos;
      if (currentPos == maxSize)
        currentPos = 0;
    } else if (currentPos == -1)
      currentPos = nextPos;

    return theQueue[nextPos];
  }
  
  public SimpleEvent readNext() {
    if (currentPos == -1)
      return null;
    
    SimpleEvent e = theQueue[currentPos++];
    
    // wrapped?
    if (currentPos == maxSize)
      currentPos = 0;
    
    // now empty?
    if (currentPos == nextPos)
      currentPos = -1;
    
    return e;
  }
  
  public SimpleEvent peekNext() {
    if (currentPos == -1)
      return null;
    
    SimpleEvent e = theQueue[currentPos];
    
    return e;
  }
  
  public void clear() {
    currentPos = -1;
    nextPos = 0;
  }
}
