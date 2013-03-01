package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.measuredsoftware.android.timer.R;

/**
 * Lives inside an ActiveTimerView, and is a button to cancel an active timer.
 * 
 * @author neil
 * 
 */
public class CancelIcon extends ImageView
{
    /**
     * @param context
     * @param attrs
     */
    public CancelIcon(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        
        setBackgroundDrawable(null);
        setImageResource(R.drawable.cancel_button);
    }

}
