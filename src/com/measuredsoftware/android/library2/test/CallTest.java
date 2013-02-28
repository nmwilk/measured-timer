package com.measuredsoftware.android.library2.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.measuredsoftware.android.library2.utils.Calls;
import com.measuredsoftware.android.library2.utils.StringCountMap;

public class CallTest extends AndroidTestCase
{
  public void testGetCallCounts()
  {
    Cursor c = Calls.getCallHistoryCursor(getContext());
    
    StringCountMap scm = Calls.getCallCounts(c);

    c.close();
  }
}
