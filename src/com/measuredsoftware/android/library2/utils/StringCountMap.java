package com.measuredsoftware.android.library2.utils;

import java.util.HashMap;

public class StringCountMap extends HashMap<String,Integer>
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public int incKey(String sKey)
  {
    Integer i = this.get(sKey);
    if (i == null)
    {
      i = new Integer(0);
    }    
    ++i;
    this.put(sKey, i);
    
    return i;
  }
  
  public int decKey(String sKey)
  {
    int nRet = 0;
    
    Integer i = this.get(sKey);
    if (i != null)
    {
      nRet = --i;
      this.put(sKey, i);
    }
    
    return nRet;
  }
  
  public int getKeyCount(String sKey)
  {
    int nRet = -1;
    Integer i = this.get(sKey);
    if (i != null)
      nRet = i;
    
    return nRet;
  } 
}
