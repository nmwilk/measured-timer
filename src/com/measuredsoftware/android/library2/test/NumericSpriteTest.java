package com.measuredsoftware.android.library2.test;

import junit.framework.TestCase;

import com.measuredsoftware.android.library2.utils.NumericSprite;

public class NumericSpriteTest extends TestCase {
  public void testNumericSprites() {
    NumericSprite secs1;
    NumericSprite secs2;
    NumericSprite secs3;
    NumericSprite secs4;
    secs1   = new NumericSprite(1, false);
    secs2   = new NumericSprite(2, false);
    secs3   = new NumericSprite(3, false);
    secs4   = new NumericSprite(4, false);

    NumericSprite[] allSprites = new NumericSprite[4];
    allSprites[0] = secs1;
    allSprites[1] = secs2;
    allSprites[2] = secs3;
    allSprites[3] = secs4;

    for(int i=0; i < allSprites.length; i++) {
      NumericSprite sprite = allSprites[i];

      for(int j=0; j < 10000; j++) {
        String shouldBe = String.valueOf(j);

        final int maxLen = i+1;
        final int tooBigBy = shouldBe.length() - maxLen;
        if (tooBigBy > 0) {
          int end = shouldBe.length()-tooBigBy;
          shouldBe = shouldBe.substring(0, end);
        }
          
        sprite.setValue(j);
        assertEquals(shouldBe, sprite.getAsString());
      }
    }  
  }
  
  public void testNumericSpritesWithZeros() {
    NumericSprite secs1;
    NumericSprite secs2;
    NumericSprite secs3;
    NumericSprite secs4;
    secs1   = new NumericSprite(1, true);
    secs2   = new NumericSprite(2, true);
    secs3   = new NumericSprite(3, true);
    secs4   = new NumericSprite(4, true);

    NumericSprite[] allSprites = new NumericSprite[4];
    allSprites[0] = secs1;
    allSprites[1] = secs2;
    allSprites[2] = secs3;
    allSprites[3] = secs4;

    for(int i=0; i < allSprites.length; i++) {
      NumericSprite sprite = allSprites[i];

      if (i == 3)
      {
        int h=0;
        h++;
      }
      for(int j=0; j < 10000; j++) {
        String shouldBe = String.valueOf(j);

        final int arrayLen = i+1;
        int tooBigBy = shouldBe.length() - arrayLen;
        if (tooBigBy > 0) {
          int end = shouldBe.length()-tooBigBy;
          shouldBe = shouldBe.substring(0, end);
        } else if (tooBigBy < 0) {
          while(tooBigBy < 0) {
            shouldBe = "0" + shouldBe;
            ++tooBigBy;
          }
        }
          
        sprite.setValue(j);
        assertEquals(shouldBe, sprite.getAsString());
      }
    }  
  }
}
