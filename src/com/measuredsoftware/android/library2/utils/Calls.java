package com.measuredsoftware.android.library2.utils;

import android.content.Context;
import android.database.Cursor;

public class Calls
{
  public static Cursor getCallHistoryCursor(Context context)
  {
    Cursor c = context.getContentResolver().query( 
        android.provider.CallLog.Calls.CONTENT_URI, 
        null, null, null, 
        android.provider.CallLog.Calls.DATE + " DESC"); 
    
    return c;
  }
  
  public static StringCountMap getCallCounts(Cursor c)
  {
    StringCountMap callCounts = new StringCountMap();
    
    int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);

    if (c != null && c.moveToFirst())     
    { 
      do
      {
        callCounts.incKey(c.getString(numberColumn));
      } while (c.moveToNext());
    } 
    
    return callCounts;
  }
}
