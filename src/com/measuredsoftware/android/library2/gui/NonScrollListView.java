package com.measuredsoftware.android.library2.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NonScrollListView extends ListView {

  public NonScrollListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    int action = ev.getAction();

    if (action == MotionEvent.ACTION_MOVE) {
      return true;
    }

    return super.onTouchEvent(ev);
  }

}
