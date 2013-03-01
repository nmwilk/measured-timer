package com.measuredsoftware.android.timer.views;

import com.measuredsoftware.android.timer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Lives inside an ActiveTimerView, and is a button to cancel an active timer.
 * 
 * @author neil
 * 
 */
public class CancelButton extends ImageButton
{
    /**
     * @param context
     * @param attrs
     */
    public CancelButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        
        setBackgroundDrawable(null);
        setImageResource(R.drawable.cancel_button);
    }

}
