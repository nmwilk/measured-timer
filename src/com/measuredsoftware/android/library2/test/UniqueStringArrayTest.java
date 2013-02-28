package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.UniqueStringArray;

public class UniqueStringArrayTest extends TestCase
{
  private UniqueStringArray initSimpleArray()
  {
    UniqueStringArray sa = new UniqueStringArray();
    sa.add("string1");
    sa.add("string2");
    sa.add("string3");
    
    return sa;
  }
  
  public void testAddString()
  {
    UniqueStringArray sa = initSimpleArray();
    
    assertEquals(sa.get(0), "string1");
    assertEquals(sa.get(1), "string2");
    assertEquals(sa.get(2), "string3");
  }

  public void testContainsString()
  {
    UniqueStringArray sa = initSimpleArray();
    
    assertTrue(sa.contains("string1"));
    assertFalse(sa.contains("String1"));
    assertFalse(sa.contains("String1 "));
    assertFalse(sa.contains(" String1"));

    assertTrue(sa.contains("string2"));

    sa.add("a new, longer string");
    assertTrue(sa.contains("string3"));
    
    assertTrue(sa.contains("a new, longer string"));
  }

}
