package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.DataTools;


public class DataToolsTest extends TestCase {

  public void testFloatByteArray() {
    float f = 23.4567f;
    byte[] ba = DataTools.floatToByteArray(f);
    float res = DataTools.byteArrayToFloat(ba);
    
    assertEquals(f, res);
    
    f = 424235.34f;
    ba = DataTools.floatToByteArray(f);
    res = DataTools.byteArrayToFloat(ba);

    assertEquals(f, res);
  }
  
  public void testFloatByteArray2() {
    float f = 40.932f;
    byte[] ba = DataTools.floatToByteArray(f);
    float res = DataTools.byteArrayToFloat(ba);
    
    assertEquals(f, res);
    
    f = 424235.34f;
    ba = DataTools.floatToByteArray(f);
    res = DataTools.byteArrayToFloat(ba);

    assertEquals(f, res);
  }
  
  public void testIntByteArray() {
    int i = 12345;
    byte[] ba = DataTools.intToByteArray(i);
    int res = DataTools.byteArrayToInt(ba);
    
    assertEquals(i, res);
    
    i = 1;
    ba = DataTools.intToByteArray(i);
    res = DataTools.byteArrayToInt(ba);
    
    assertEquals(i, res);
  }
  
  public void testLongByteArray() {
    long l = 2132345;
    byte[] ba  = DataTools.longToByteArray(l);
    long res = DataTools.byteArrayToLong(ba);
    
    assertEquals(l, res);
    
    l = 1;
    ba = DataTools.longToByteArray(l);
    res = DataTools.byteArrayToLong(ba);
    
    assertEquals(l, res);
  }
  
  public void testShortByteArray() {
    short s = 2324;
    byte[] ba = DataTools.shortToByteArray(s);
    short res = DataTools.byteArrayToShort(ba);
    
    assertEquals(s, res);
    
    s = 1;
    ba = DataTools.shortToByteArray(s);
    res = DataTools.byteArrayToShort(ba);
    
    assertEquals(s, res);
  }
  
  public void testSHA1Hash() {
    String value = "neilwilkinson";
    String exp   = "ba632c867973d520fda2fcbac26bb7469fe87703";
    String res   = DataTools.getSHA1Hash(value);
    
    assertEquals(exp, res);
  }
}
