package com.measuredsoftware.android.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class DigitalTimeTextView extends TextView {
  
  private Paint mPaintTimer;

  public DigitalTimeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    mPaintTimer = new Paint();
    mPaintTimer.setTypeface(Globals.getFont());
    mPaintTimer.setTextSize(24);
    mPaintTimer.setAntiAlias(true);
//    mPaintTimer.setColor(TimerActivity.N_COLOUR_ORANGE);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    canvas.drawText(""+this.getText(), 0, 0, mPaintTimer);
  }

  
}
