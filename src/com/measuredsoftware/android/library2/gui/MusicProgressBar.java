package com.measuredsoftware.android.library2.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.measuredsoftware.android.library2.utils.DateTools;

public class MusicProgressBar extends ProgressBar
{
  private Paint textPaint;
  private float mTextPosX;
  private float mTextPosY;
  
  public int mTextColor;
  
  public MusicProgressBar(Context context)
  {
    super(context);
    init();
  }
  
  public MusicProgressBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }
  
  public MusicProgressBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }
  
  private void init()
  {
    Resources r = this.getResources();
    
    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setStrokeWidth(2);
    textPaint.setColor(r.getColor(mTextColor));
    textPaint.setTextSize(12);
    
    mTextPosX = -1;
    mTextPosY = -1;
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    String text = DateTools.millisecondsToStringMMSS(this.getProgress());
    
    if (mTextPosX == -1)
      mTextPosX = (this.getWidth()/2)-(textPaint.measureText("00:00")/2);
    if (mTextPosY == -1)
      mTextPosY = (this.getHeight()/2)+(textPaint.getTextSize()/3);
    
    canvas.drawText(text, mTextPosX, mTextPosY, textPaint);
  }
}
