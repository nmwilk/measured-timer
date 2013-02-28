package com.measuredsoftware.android.library2.utils;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Paint;

public class TextSprites {
  //public final static String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,";
  public final static String CHAR_SET_DEFAULT = "0123456789abcdefghijklmnopqrstuvwxyz.,-/+";
  public final static String CHAR_SET_NUMBERS = "0123456789.,-/+";
  public final static String CHAR_SET_CHARS   = "abcdefghijklmnopqrstuvwxyz.,-/+";
  public final static String CHAR_SET_DOLLARS = "0123456789.,-/+$";
  public final static int CHAR_SET_CODE_DEFAULT = 0;
  public final static int CHAR_SET_CODE_NUMBERS = 1;
  public final static int CHAR_SET_CODE_CHARS   = 2;
  public final static int CHAR_SET_CODE_DOLLARS = 3;

  public final static int CHAR_SET_DEFAULT_INDEX_DOT      = 36;//62;
  public final static int CHAR_SET_DEFAULT_INDEX_COMMA    = 37;//63;
  public final static int CHAR_SET_DEFAULT_INDEX_DASH     = 38;//63;
  public final static int CHAR_SET_DEFAULT_INDEX_SLASH    = 39;//63;
  public final static int CHAR_SET_DEFAULT_INDEX_PLUS     = 40;//63;
  public final static int CHAR_SET_DEFAULT_INDEX_DOLLAR   = 15;
  public final int CHAR_SET_INDEX_DOT;
  public final int CHAR_SET_INDEX_COMMA;
  public final int CHAR_SET_INDEX_DASH;
  public final int CHAR_SET_INDEX_SLASH;
  public final int CHAR_SET_INDEX_PLUS;
  public final int CHAR_SET_INDEX_DOLLAR;
  
  private static final int MAX_STRINGS = 10;
  
  private LabelMaker mLabelMaker;
  private int[] mWidth;
  private int[] mLabelId;
  
  public  TextEntry[] mStrings;
  private int mStringsCount;
  
  public final String mCharSet;
  public final int    mCharSetCode;
  
  private final float mViewWidth;
  private final float mViewHeight;
  
  public TextSprites(float viewWidth, float viewHeight) {
    this(viewWidth, viewHeight, null);
  }
  
  public TextSprites(float viewWidth, float viewHeight, String charSet) {
    if (charSet == null) {
      mCharSet = CHAR_SET_DEFAULT;
      mCharSetCode = 0;
      CHAR_SET_INDEX_DOT = CHAR_SET_DEFAULT_INDEX_DOT;
      CHAR_SET_INDEX_COMMA = CHAR_SET_DEFAULT_INDEX_COMMA;
      CHAR_SET_INDEX_DASH = CHAR_SET_DEFAULT_INDEX_DASH;
      CHAR_SET_INDEX_SLASH = CHAR_SET_DEFAULT_INDEX_SLASH;
      CHAR_SET_INDEX_PLUS = CHAR_SET_DEFAULT_INDEX_PLUS;
    } else {
      mCharSet = charSet;
      if (mCharSet == CHAR_SET_DEFAULT)
        mCharSetCode = CHAR_SET_CODE_DEFAULT;
      else if (mCharSet == CHAR_SET_NUMBERS)
        mCharSetCode = CHAR_SET_CODE_NUMBERS;
      else if (mCharSet == CHAR_SET_DOLLARS)
        mCharSetCode = CHAR_SET_CODE_DOLLARS;
      else
        mCharSetCode = CHAR_SET_CODE_CHARS;

      final int sizeDiff = CHAR_SET_DEFAULT.length()-mCharSet.length();
      CHAR_SET_INDEX_DOT = CHAR_SET_DEFAULT_INDEX_DOT-sizeDiff;
      CHAR_SET_INDEX_COMMA = CHAR_SET_DEFAULT_INDEX_COMMA-sizeDiff;
      CHAR_SET_INDEX_DASH = CHAR_SET_DEFAULT_INDEX_DASH-sizeDiff;
      CHAR_SET_INDEX_SLASH = CHAR_SET_DEFAULT_INDEX_SLASH-sizeDiff;
      CHAR_SET_INDEX_PLUS = CHAR_SET_DEFAULT_INDEX_PLUS-sizeDiff;
    }

    CHAR_SET_INDEX_DOLLAR = CHAR_SET_DEFAULT_INDEX_DOLLAR;
    
    mLabelMaker = null;

    mStrings = new TextEntry[MAX_STRINGS];
    for(int i=0; i < MAX_STRINGS; i++) {
      mStrings[i] = null;
    }
    
    mStringsCount = 0;
    
    mViewWidth = viewWidth;
    mViewHeight = viewHeight;
    
    mWidth = new int[mCharSet.length()];
    mLabelId = new int[mCharSet.length()];
  }  

  public void initialize(GL10 gl, Paint paint) {
    int height = MathTools.roundUpPower2((int) paint.getFontSpacing());
    final float interDigitGaps = 9 * 1.0f;
    int width = MathTools.roundUpPower2((int) (interDigitGaps + paint.measureText(mCharSet)));
    mLabelMaker = new LabelMaker(true, width, height);
    mLabelMaker.initialize(gl);
    mLabelMaker.beginAdding(gl);
    for (int i = 0; i < mCharSet.length(); i++) {
      String digit = mCharSet.substring(i, i+1);
      mLabelId[i] = mLabelMaker.add(gl, digit, paint);
      mWidth[i] = (int) Math.ceil(mLabelMaker.getWidth(i));
    }
    mLabelMaker.endAdding(gl);
  }

  public void shutdown(GL10 gl) {
    mLabelMaker.shutdown(gl);
    mLabelMaker = null;
  }
  
  /* for testing */
  public String[] getAsStrings() {
    String[] strings = new String[mStringsCount];
    for(int i=0; i < mStringsCount; i++) {
      strings[i] = mStrings[i].getAsString();
    }
    
    return strings;
  }

  
  public int addTextEntry(TextEntry te) {
    int index = -1;
    if (mStringsCount < MAX_STRINGS) {
      index = mStringsCount;
      mStrings[mStringsCount++] = te;
    }
    
    return index;
  }
  
  public int getSize() {
    return mStringsCount;
  }
  
  public void draw(GL10 gl) {
    mLabelMaker.beginDrawing(gl, mViewWidth, mViewHeight);
    for(int i=0; i < mStringsCount; i++) {
      TextEntry te = mStrings[i];
      if (!te.mDraw)
        continue;
      
      float x = te.mDrawX;
      
      // align right? compute start pos, so it ends at the defined position
      if (te.mAlignRight) {
        int offset = 0;
        for(int j=0; j < te.mLen; j++) {
          final int value = te.mCharIndexes[j]; 
          if (value == -1)
            continue;          

          try {
            offset += mWidth[te.mCharIndexes[j]];
          } catch(ArrayIndexOutOfBoundsException e) {
            //Log.e("wh","TextSprites: AIOOBE");
          }
        }
        
        x -= offset;
      }
      
      for(int j=0; j < te.mLen; j++) {
        final int value = te.mCharIndexes[j]; 
        if (value == -1)
          continue;

        mLabelMaker.draw(gl, x, te.mDrawY, mLabelId[value]);
        x += mWidth[value];
      }
    }
    
    mLabelMaker.endDrawing(gl);
  }

  public float width(int string) {
    float width = 0.0f;
    TextEntry te = mStrings[string];
    for(int i=0; i < te.mLen; i++) {   
      if (te.mCharIndexes[i] == -1)
        continue;
      width += mWidth[te.mCharIndexes[i]];
    }
    return width;
  }
}
