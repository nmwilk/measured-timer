package com.measuredsoftware.android.library2.utils;

import android.graphics.PointF;

public class ScreenTools {

  public static final int POS_BOTTOM_RIGHT  = 0;
  public static final int POS_BOTTOM_LEFT   = 1;
  public static final int POS_BOTTOM_CENTRE = 2;
  public static final int POS_TOP_CENTRE    = 3;
  public static final int POS_TOP_LEFT      = 4;

  public static void getPosition(int graphicX, int graphicY, int pos, int screenHeight, float screenRatio, float scalingRatio, int paddingX, int paddingY, PointF tempPoint) {
    final int halfGraphicX = (graphicX/2);
    final int halfGraphicY = (graphicY/2);
    final float height = screenHeight*scalingRatio;
    final float width = Math.round(height * screenRatio);

    switch(pos) {
    case POS_BOTTOM_RIGHT: {
      // get screen width
      tempPoint.x = (width-halfGraphicX)-paddingX;
      tempPoint.y = paddingY+halfGraphicY;
      break;
    }
    case POS_BOTTOM_LEFT: {
      // get screen width
      tempPoint.x = halfGraphicX+paddingX;
      tempPoint.y = paddingY+halfGraphicY;
      break;
    }
    case POS_BOTTOM_CENTRE: {
      // get screen width
      tempPoint.x = (width/2)+paddingX;
      tempPoint.y = paddingY+halfGraphicY;
      break;
    }
    case POS_TOP_CENTRE: {
      tempPoint.x = (width/2)+paddingX;
      tempPoint.y = screenHeight-(paddingY+halfGraphicY);
      break;
    }
    case POS_TOP_LEFT: {
      // get screen width
      tempPoint.x = halfGraphicX+paddingX;
      tempPoint.y = (screenHeight-graphicY)-(paddingY+halfGraphicY);
      break;
    }
    }
  }

  /*public static void getPosition(int graphicSize, int pos, int screenHeight, float screenRatio, int paddingX, int paddingY, float margin, PointF tempPoint) {
    int halfGraphicSize = (graphicSize/2);
    switch(pos) {
    case POS_BOTTOM_RIGHT: {
      // get screen width
      final float width = Math.round(screenHeight * screenRatio);
      tempPoint.x = (width-halfGraphicSize)-(paddingX+margin);
      tempPoint.y = paddingY+halfGraphicSize;
      break;
    }
    case POS_BOTTOM_LEFT: {
      // get screen width
      tempPoint.x = halfGraphicSize+paddingX+margin;
      tempPoint.y = paddingY+halfGraphicSize;
      break;
    }
    }
  }*/
}
