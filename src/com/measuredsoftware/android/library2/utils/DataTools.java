package com.measuredsoftware.android.library2.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import android.os.SystemClock;

public class DataTools {
  public static final int INT_SIZE    = 4;
  public static final int SHORT_SIZE  = 2;
  public static final int FLOAT_SIZE  = 4;
  public static final int LONG_SIZE   = 8;
  public static final int BOOL_SIZE   = 1;
  
  /*
   *  INT
   */
  public static final byte[] intToByteArray(int value) {
    /*return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};*/
    byte[] ba = new byte[INT_SIZE];
    
    intToByteArray(value, ba, 0);
    
    return ba;
  }
  
  public static final int byteArrayToInt(byte [] b) {
    return (b[0] << 24)
            + ((b[1] & MASK) << 16)
            + ((b[2] & MASK) << 8)
            + (b[3] & MASK);
  }

  public static final boolean intToByteArray(int value, byte[] ba, int pos) {
    if ((ba.length-pos) < INT_SIZE)
      return false;
    
    ba[pos] = (byte)(value >>> 24);
    ba[pos+1] = (byte)(value >>> 16);
    ba[pos+2] = (byte)(value >>> 8);
    ba[pos+3] = (byte)value;
    
    return true;
  }
  
  /*
   * BOOL
   */
  public static final byte[] booleanToByteArray(boolean value) {
    byte[] ba = new byte[BOOL_SIZE];
    
    booleanToByteArray(value, ba, 0);
    
    return ba;
  }
  
  public static final boolean booleanToByteArray(boolean value, byte[] ba, int pos) {
    if ((ba.length-pos) < BOOL_SIZE)
      return false;
    
    ba[pos] = (value) ? (byte)1 : (byte)0;
    
    return true;
  }
  
  public static final boolean byteArrayToBoolean(byte [] b) {
    return (b[0] == 1) ? true : false;
  }  
  
  /*
   *  SHORT
   */
  public static final byte[] shortToByteArray(short value) {
    /*return new byte[] {
            (byte)(value >>> 8),
            (byte)value};*/
    byte[] ba = new byte[SHORT_SIZE];
    
    shortToByteArray(value, ba, 0);
    
    return ba;
  }
  
  public static final boolean shortToByteArray(short value, byte[] ba, int pos) {
    if ((ba.length-pos) < SHORT_SIZE)
      return false;
    
    ba[pos] = (byte)(value >>> 8);
    ba[pos+1] = (byte)value;
    
    return true;
  }
  
  public static final short byteArrayToShort(byte [] b) {
    return (short)((b[0] << 8) + (b[1] & MASK));
  }
  
  private static final int MASK = 0xFF;

  /**
   * convert byte array (of size 4) to float
   * @param test
   * @return
   */
  public static float byteArrayToFloat(byte test[]) {
    int bits = 0;
    int i = 0;
    for (int shifter = FLOAT_SIZE-1; shifter >= 0; shifter--) {
      bits |= ((int) test[i] & MASK) << (shifter * 8);
      i++;
    }
 
    return Float.intBitsToFloat(bits);
  }
 
  /**
  * convert float to byte array (of size 4)
  * @param f
  * @return
  */
  public static byte[] floatToByteArray(float f) {
    byte[] ba = new byte[FLOAT_SIZE];
    
    floatToByteArray(f, ba, 0);
    
    return ba;
  }
  
  public static boolean floatToByteArray(float f, byte[] ba, int pos) {
    if ((ba.length-pos) < FLOAT_SIZE)
      return false;
    
    int i = Float.floatToRawIntBits(f);
    intToByteArray(i, ba, pos);
    
    return true;
  }
  
  public static final boolean longToByteArray(long value, byte[] ba, int pos) {
    if ((ba.length-pos) < LONG_SIZE)
      return false;
    
    ba[pos] = (byte)(value >>> 56);
    ba[pos+1] = (byte)(value >>> 48);
    ba[pos+2] = (byte)(value >>> 40);
    ba[pos+3] = (byte)(value >>> 32);
    ba[pos+4] = (byte)(value >>> 24);
    ba[pos+5] = (byte)(value >>> 16);
    ba[pos+6] = (byte)(value >>> 8);
    ba[pos+7] = (byte)value;
    
    return true;
  }
  
  public static byte[] longToByteArray(long value) {
    /*return new byte[] {
        (byte)(value >>> 56),
        (byte)(value >>> 48),
        (byte)(value >>> 40),
        (byte)(value >>> 32),
        (byte)(value >>> 24),
        (byte)(value >>> 16),
        (byte)(value >>> 8),
        (byte) value};*/
    byte[] ba = new byte[LONG_SIZE];
    
    longToByteArray(value, ba, 0);
    
    return ba;
  }
  
  public static long byteArrayToLong(byte[] b) {
    /*long l = 0;
    for(int i =0; i < 4; i++){        
      l <<= 8;
      l ^= (long)ba[i];       
    }
    return l;*/
    return 
      (b[0] << 56)
    + ((b[1] & MASK) << 48)
    + ((b[2] & MASK) << 40)
    + ((b[3] & MASK) << 32)
    + ((b[4] & MASK) << 24)
    + ((b[5] & MASK) << 16)
    + ((b[6] & MASK) << 8)
    +  (b[7] & MASK);    
  }

  public static String getToDP(float mAchievementRedMasterTime, int maxDecPlaces) {
    String time = ""+mAchievementRedMasterTime;
    int dotIndex = time.indexOf('.');
    if (dotIndex != -1) {
      int currentDecPlaces = time.length()-(dotIndex+1);
      if (currentDecPlaces > maxDecPlaces)
        time = time.substring(0, time.length()-(currentDecPlaces-maxDecPlaces));
    }
    return time;
  }

  public static String getSHA1Hash(String deviceId) {
    MessageDigest digester = null;
    try {
      digester = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
    
    byte[] digest;
    String res = null;
    try {
      digest = digester.digest(deviceId.getBytes("UTF-8"));
      res = getStringFromBytes(digest);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
    return res;
  }
  
  public static String getStringFromBytes(byte[] bytes) {
    StringBuffer hexString = new StringBuffer();
    for (int i=0; i<bytes.length; i++) {
      String h = Integer.toHexString(0xFF & bytes[i]);
      while (h.length()<2) h = "0" + h;
      hexString.append(h);
    }
    return hexString.toString();
  }

  public static byte[] generateRandomBytes(int count) {
    byte[] randBytes = new byte[count];
    Random r = new Random(SystemClock.uptimeMillis());
    r.nextFloat();r.nextLong();
    r.nextBytes(randBytes);
    return randBytes;
  }
}
