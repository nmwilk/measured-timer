package com.measuredsoftware.android.library2.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateTools
{
  public static String getYYYYMMDD(GregorianCalendar c)
  {
    String DATE_FORMAT = "yyyyMMdd";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    String sText = sdf.format(c.getTime());
    
    return sText;
  }
  
  public static String getYYYYMMDDHHMMSS(GregorianCalendar c) {
    String DATE_FORMAT = "yyyyMMddHHmmss";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    String sText = sdf.format(c.getTime());
    
    return sText;
  }
  
  public static boolean datePast(String last, int addOnDays, GregorianCalendar now) {
    int y = Integer.valueOf(last.substring(0,4));
    int m = Integer.valueOf(last.substring(4,6));
    int d = Integer.valueOf(last.substring(6,8));
    GregorianCalendar lastCal = new GregorianCalendar();
    lastCal.set(y,m,d);
    lastCal.add(Calendar.DAY_OF_MONTH, addOnDays);
    
    return now.after(lastCal);
  }
  
  public static boolean datePast(String last, GregorianCalendar now) {
    int y = Integer.valueOf(last.substring(0,4));
    int m = Integer.valueOf(last.substring(4,6));
    int d = Integer.valueOf(last.substring(6,8));
    GregorianCalendar lastCal = new GregorianCalendar();
    lastCal.set(y,m,d);
    return lastCal.after(now);
  }  
  
  public static boolean datePast(String last) {
    return datePast(last, new GregorianCalendar());
  }    

  public static boolean datePast(String last, int addOnDays) {
    return datePast(last, addOnDays, new GregorianCalendar());
  }
  
  public static String getYYYYMMDD()
  {
    GregorianCalendar c = new GregorianCalendar();
    return getYYYYMMDD(c);
  }
  
  public static String getYYYYMMDDHHMMSS() {
    GregorianCalendar c = new GregorianCalendar();
    return getYYYYMMDDHHMMSS(c);
  }
  
  public static String millisecondsToStringMMSS(int ms)
  {
    int timeLeftInSecs = ms/1000;
    
    // calc mins
    int nMins = timeLeftInSecs/60;
    
    // calc secs
    int nSecs = timeLeftInSecs%60;
    
    String sRet = "";
    if (nMins < 10)
      sRet += "0";
    sRet += nMins+":";
    
    if (nSecs < 10)
      sRet += "0";
    sRet += nSecs;
    
    return sRet;
  }
  
  public static String secondsToStringMMSS(int s)
  {
    // calc mins
    int nMins = s/60;
    
    // calc secs
    int nSecs = s%60;
    
    String sRet = "";
    if (nMins < 10)
      sRet += "0";
    sRet += nMins+":";
    
    if (nSecs < 10)
      sRet += "0";
    sRet += nSecs;
    
    return sRet;
  }  
}
