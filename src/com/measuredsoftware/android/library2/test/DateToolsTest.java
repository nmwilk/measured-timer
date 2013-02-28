package com.measuredsoftware.android.library2.test;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.DateTools;


public class DateToolsTest extends TestCase
{
  public void testMillisecondsToString()
  {
    String small = DateTools.millisecondsToStringMMSS(1000);
    
    assertEquals("Small", small, "00:01");
    
    String med = DateTools.millisecondsToStringMMSS(91000);
    
    assertEquals("Med", med, "01:31");    
    
    String large = DateTools.millisecondsToStringMMSS(1191000);
    
    assertEquals("Large", large, "19:51");    
  }

  public void testSecondsToString()
  {
    String small = DateTools.secondsToStringMMSS(1);
    
    assertEquals("Small", small, "00:01");
    
    String med = DateTools.secondsToStringMMSS(91);
    
    assertEquals("Med", med, "01:31");    
    
    String large = DateTools.secondsToStringMMSS(1191);
    
    assertEquals("Large", large, "19:51");    
  }

  public void testDatePast() {
    // not passed
    GregorianCalendar c = new GregorianCalendar(2010,9,10);
    assertEquals(false, DateTools.datePast("20100905", 7, c));
    c = new GregorianCalendar(2010,9,10);
    assertEquals(false, DateTools.datePast("20100904", 7, c));
    c = new GregorianCalendar(2010,9,10);
    assertEquals(false, DateTools.datePast("20100903", 8, c));

    // same
    c = new GregorianCalendar(2010,9,10);
    assertEquals(false, DateTools.datePast("20100903", 7, c));
    c = new GregorianCalendar(2010,9,1);
    assertEquals(false, DateTools.datePast("20100903", 9, c));
    
    // has passed
    c = new GregorianCalendar(2010,9,10);
    assertEquals(true, DateTools.datePast("20100901", 7, c));
    c = new GregorianCalendar(2010,9,10);
    assertEquals(true, DateTools.datePast("20100901", 8, c));
    
    // error from MeasuredTimer
    c = new GregorianCalendar(2010,9,22);
    assertEquals(true, DateTools.datePast("20100910", 5, c));
  }
}
