package com.measuredsoftware.android.library2.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class FadeView extends View {

  public FadeView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    canvas.drawColor(Color.BLACK);
  }
  
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int measuredWidth  = measuredWidth(widthMeasureSpec);
    int measuredHeight = measuredHeight(heightMeasureSpec);
    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  
  private int measuredHeight(int measuredspec) {
    return MeasureSpec.getSize(measuredspec);
  }

  
  private int measuredWidth(int measuredspec) {
    return MeasureSpec.getSize(measuredspec);
  }  

}
