package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Used to align stop button over timer view.
 * 
 * @author neil
 * 
 */
public class RelativeLayout extends android.widget.RelativeLayout
{
    /**
     * @param context
     * @param attrs
     */
    public RelativeLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        
        if (changed)
        {
            
        }
    }
}
