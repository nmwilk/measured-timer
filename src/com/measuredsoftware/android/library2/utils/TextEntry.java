package com.measuredsoftware.android.library2.utils;

public class TextEntry {
  public boolean    mDraw;
  public int[]      mCharIndexes;
  public int        mLen;
  public final int  mMaxLen;
  public float      mDrawX;
  public float      mDrawY;
  
  public final boolean mAlignRight; // otherwise it draws from the left
  
  private int       mLastInt;
  private float     mLastFloat;
  
  private boolean   mHasFirstChar;
  
  private String mCharSet;
  private int mCharSetCode;
  
  private TextSprites mParent;

  public TextEntry(int maxLen, int firstChar, TextSprites parent) {
    this(maxLen, firstChar, false, parent);
  }
  
  public TextEntry(int maxLen, int firstChar, boolean alignRight, TextSprites parent) {
    this.setParent(parent);
    mHasFirstChar = (firstChar != -1);
    
    mMaxLen = maxLen;
    mCharIndexes = new int[mMaxLen];
    for(int i=0; i < mMaxLen; i++) {
      if (mHasFirstChar && i==0) {
        mCharIndexes[i] = firstChar;
        continue;
      }
      mCharIndexes[i] = -1;
    }
    mLen = 0;
    mDrawX = 0f;
    mDrawY = 0f;
    mLastInt = -1;
    mLastFloat = -1;
    mAlignRight = alignRight;
  }
  
  public TextEntry(int maxLen, TextSprites parent) {
    this(maxLen, -1, parent);
  }
  
  public void setParent(TextSprites parent) {
    mParent = parent;
    mCharSet = parent.mCharSet;
    mCharSetCode = parent.mCharSetCode;    
  }
  
  public void drawAt(int x, int y) {
    mDrawX = x;
    mDrawY = y;
  }
  
  private static int countDigits(int value) {
    if (value == 0)
      return 1;
    
    int digits = 1;
    if (value > 9)
      ++digits;
    if (value > 99)
      ++digits;
    if (value > 999)
      ++digits;
    if (value > 9999)
      ++digits;
    
    return digits;
  }
  
  private void resetLasts() {
    mLastInt =   -1;
    mLastFloat = -1f;
  }
  
  public boolean setFloat(float value, int decPlaces) {
    //Log.d("testgame", "setFloat " + value + " (" + decPlaces + ")");
    if (value > 0.0 && mLastFloat == value) {
      mLastInt = -1;
      return true;
    }

    mLastFloat = value;
    
    int index = 0;
    if (mHasFirstChar)
      ++index;
    
    if (value < 0.0f) {
      mCharIndexes[index++] = mParent.CHAR_SET_INDEX_DASH;
      value = -value;
    }

    int intValue = (int)value;
    //int intValue = Math.round(value);
    final int digitsBefore = countDigits(intValue);
    
    value -= intValue;
    for(int i=0; i < decPlaces; i++) {
      value *= 10;
    }
    
    int digitsAfter = countDigits((int)value);
    if (digitsAfter < decPlaces)
      digitsAfter = decPlaces;

    final int totalLen = digitsBefore+(index+1)+digitsAfter;
    if (totalLen > mMaxLen)
      return false;

    for(int i=digitsBefore-1; i > -1; i--) {
      mCharIndexes[index+i] = (intValue%10);
      intValue /= 10;
    }
    
    index += digitsBefore;
    
    mCharIndexes[index++] = mParent.CHAR_SET_INDEX_DOT;

    intValue = Math.round(value);
    for(int i=digitsAfter-1; i > -1; i--) {
      mCharIndexes[index+i] = (intValue%10);
      intValue /= 10;
    }
    
    index += digitsAfter;
    
    mLen = index;
    
    while(index < mMaxLen) {
      mCharIndexes[index++] = -1;
    }

    //Log.d("testgame", "set label to: " + getAsString());
    
    return true;
  }
  
  
  /* for testing */
  public String getAsString() {
    StringBuilder sb = new StringBuilder();
    for(int j=0; j < mLen; j++) {
      if (mCharIndexes[j] != -1)
        sb.append(mCharSet.charAt(mCharIndexes[j]));
    }

    return sb.toString();
  }  
  
  public boolean setInt(int value) {
    if (mLastInt == value || value < 0) {
      mLastFloat = -1;
      return true;
    }
    
    mLen = 0;
    mLastInt = value;
    
    int placeForMinus = 0;
    if (value < 0) {
      placeForMinus = 1;
      mCharIndexes[0] = mParent.CHAR_SET_INDEX_DASH;
    }
    
    final int digits = countDigits(value) + placeForMinus;
    final int off = digits-1;
    for(int i=0; i < mMaxLen; i++) {
      if (i < digits) {
        mCharIndexes[off-i] = (value%10);
        value /= 10;
        ++mLen;
      } else {
        mCharIndexes[i] = -1;
      }
    }
    
    return true;
  }

  public boolean setPosInt(int value) {
    if (mLastInt == value) {
      mLastFloat = -1;
      return true;
    }
    
    mLen = 1;
    mLastInt = value;
    
    mCharIndexes[0] = mParent.CHAR_SET_INDEX_PLUS;
    
    final int digits = countDigits(value)+1;
    for(int i=1; i < mMaxLen; i++) {
      if (i < digits) {
        mCharIndexes[digits-i] = (value%10);
        value /= 10;
        ++mLen;
      } else {
        mCharIndexes[i] = -1;
      }
    }
    
    return true;    
  }
  
  public boolean setDollarsInt(int value) {
    if (mLastInt == value) {
      mLastFloat = -1;
      return true;
    }
    
    mLen = 1;
    mLastInt = value;
    
    mCharIndexes[0] = mParent.CHAR_SET_INDEX_DOLLAR;
    
    final int digits = countDigits(value)+1;
    for(int i=1; i < mMaxLen; i++) {
      if (i < digits) {
        mCharIndexes[digits-i] = (value%10);
        value /= 10;
        ++mLen;
      } else {
        mCharIndexes[i] = -1;
      }
    }
    
    return true;    
  }
  
  public boolean setText(String text) {
    resetLasts();
    mLen = 0;
    int i=0;
    if (mHasFirstChar)
      ++i;    
    for(; i < mMaxLen; i++) {
      if (i < text.length()) {
        mCharIndexes[i] = lookupCharValue(text.charAt(i));
        ++mLen;
      } else {
        mCharIndexes[i] = -1;
      }
    }
    
    return true;
  }
  
  public boolean setText(char[] text) {
    resetLasts();
    mLen = 0;
    int i=0;
    if (mHasFirstChar)
      ++i;
    
    for(; i < mMaxLen; i++) {
      if (i < text.length) {
        mCharIndexes[i] = lookupCharValue(text[i]);
        ++mLen;
      } else {
        mCharIndexes[i] = -1;
      }
    }
    
    return true;    
  }
  
  private int lookupCharValue(char c) {
    int i = (int)c;
 
    if (c == '.')
      return mParent.CHAR_SET_INDEX_DOT;
    else if (c == ',')
      return mParent.CHAR_SET_INDEX_COMMA;
    else if (c == '/')
      return mParent.CHAR_SET_INDEX_SLASH;
    else if (c == '-')
      return mParent.CHAR_SET_INDEX_DASH;
    else if (c == '+')
      return mParent.CHAR_SET_INDEX_PLUS;
    
    // digits
    if (i < 58) 
      return i-48;
    
    // lower case char, so class as upper case ascii value
    if (i > 96)
      i -= 32;
    
    if (mCharSetCode == TextSprites.CHAR_SET_CODE_CHARS)
      return i-65;
    
    return i-55;
  }
}
