package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;
import android.view.MotionEvent;

import com.measuredsoftware.android.library2.utils.SimpleEventQueue;
import com.measuredsoftware.android.library2.utils.SimpleEventQueue.SimpleEvent;

public class MotionEventQueueTest extends TestCase {
  public void testMotionEventQueue() {
    SimpleEventQueue queue = new SimpleEventQueue(8);

    int rep = 0;
    
    while(rep < 3) {
      queue.set(MotionEvent.obtain(0, 1, MotionEvent.ACTION_DOWN, 1, 1, 0));
      queue.set(MotionEvent.obtain(0, 2, MotionEvent.ACTION_DOWN, 2, 2, 0));
      queue.set(MotionEvent.obtain(0, 3, MotionEvent.ACTION_DOWN, 3, 3, 0));
      
      SimpleEvent e = queue.readNext();
      assertEquals(e.x, 1);
      assertEquals(e.y, 1);
      
      e = queue.readNext();
      assertEquals(e.x, 2);
      assertEquals(e.y, 2);    
      
      queue.set(MotionEvent.obtain(0, 4, MotionEvent.ACTION_DOWN, 4, 4, 0));
      queue.set(MotionEvent.obtain(0, 5, MotionEvent.ACTION_DOWN, 5, 5, 0));
  
      e = queue.readNext();
      assertEquals(e.x, 3);
      assertEquals(e.y, 3);    
  
      queue.set(MotionEvent.obtain(0, 6, MotionEvent.ACTION_DOWN, 6, 6, 0));
      queue.set(MotionEvent.obtain(0, 7, MotionEvent.ACTION_DOWN, 7, 7, 0));
      queue.set(MotionEvent.obtain(0, 8, MotionEvent.ACTION_DOWN, 8, 8, 0));
      queue.set(MotionEvent.obtain(0, 9, MotionEvent.ACTION_DOWN, 9, 9, 0));
      queue.set(MotionEvent.obtain(0, 10, MotionEvent.ACTION_DOWN, 10, 10, 0));
      queue.set(MotionEvent.obtain(0, 11, MotionEvent.ACTION_DOWN, 11, 11, 0));
      queue.set(MotionEvent.obtain(0, 12, MotionEvent.ACTION_DOWN, 12, 12, 0));
  
      e = queue.readNext();
      assertEquals(e.x, 5);
      assertEquals(e.y, 5);    
  
      e = queue.readNext();
      assertEquals(e.x, 6);
      assertEquals(e.y, 6);    
  
      e = queue.readNext();
      assertEquals(e.x, 7);
      assertEquals(e.y, 7);    
  
      e = queue.readNext();
      assertEquals(e.x, 8);
      assertEquals(e.y, 8);    
  
      e = queue.readNext();
      assertEquals(e.x, 9);
      assertEquals(e.y, 9);    
  
      e = queue.readNext();
      assertEquals(e.x, 10);
      assertEquals(e.y, 10);    
  
      e = queue.readNext();
      assertEquals(e.x, 11);
      assertEquals(e.y, 11);    
  
      e = queue.readNext();
      assertEquals(e.x, 12);
      assertEquals(e.y, 12);   
      
      e = queue.readNext();
      assertEquals(e, null);
      
      ++rep;
    }
  }
}
