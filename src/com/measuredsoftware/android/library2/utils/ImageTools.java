package com.measuredsoftware.android.library2.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageTools {
  public static Bitmap loadBitmap(Context context, int resourceId, BitmapFactory.Options sBitmapOptions) {
    Bitmap bitmap = null;
    
    if (context != null) {
      InputStream is = context.getResources().openRawResource(resourceId);
      try {
        bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // Ignore.
        }
      }
    }

    return bitmap;
  }
}
