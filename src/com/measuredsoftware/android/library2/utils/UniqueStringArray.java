package com.measuredsoftware.android.library2.utils;

import java.util.ArrayList;

public class UniqueStringArray extends ArrayList<String>
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public boolean add(String object)
  {
    if (contains(object))
      return false;
    
    return super.add(object);
  }

  public boolean contains(String object)
  {
    int nCount = this.size();
    for(int i=0; i < nCount; i++)
    {
      if (this.get(i).compareTo(object) == 0)
        return true;
    }
    
    return false;
  }
}
