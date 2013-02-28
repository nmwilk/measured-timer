package com.measuredsoftware.android.timer.views;

import com.measuredsoftware.android.timer.Globals;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * List of active timers
 * 
 * @author neil
 *
 */
public class TimersList extends LinearLayout
{
    /**
     * @param context
     * @param attrs
     */
    public TimersList(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        setOrientation(VERTICAL);
        
        if (Globals.DEBUG_LAYOUT)
        {
            setBackgroundColor(0x40FFFF00);
        }
    }
}
