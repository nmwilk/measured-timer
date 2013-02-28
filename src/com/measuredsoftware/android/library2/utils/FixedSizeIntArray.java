package com.measuredsoftware.android.library2.utils;

public class FixedSizeIntArray {
  public final int maxSize;
  public int[] theQueue;
  
  public int currentPos; // current position marker, -1 means queue is empty
  public int nextPos;    // next space
  
  public FixedSizeIntArray(int _maxSize) {
    maxSize = _maxSize;
    theQueue = new int[maxSize];
    for(int i=0; i < maxSize; i++) {
      theQueue[i] = -1;
    }
    
    currentPos = -1;
    nextPos = 0;
  }
  
  public void set(int v) {
    // full? start overwriting...
    if (currentPos == nextPos) {
      ++currentPos;
      if (currentPos == maxSize)
        currentPos = 0;
    } else if (currentPos == -1)
      currentPos = nextPos;
    
    theQueue[nextPos] = v;
    
    ++nextPos;
    // cycle back to zero
    if (nextPos == maxSize)
      nextPos = 0;
  }
  
  public int getNext() {
    if (currentPos == -1)
      return -1;
    
    int v = theQueue[currentPos++];
    if (currentPos == maxSize)
      currentPos = 0;
    
    if (currentPos == nextPos)
      currentPos = -1;
    
    return v;
  }
  
  public int peekNext() {
    if (currentPos == -1)
      return -1;
    
    return theQueue[currentPos];
  }
}
