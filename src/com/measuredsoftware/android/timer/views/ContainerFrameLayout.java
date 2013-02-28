package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.measuredsoftware.android.timer.Globals;

/**
 * Only job is to report to the single subview how big it can be.
 * 
 * @author neil
 * 
 */
public class ContainerFrameLayout extends FrameLayout
{
    /**
     * 
     * @param context
     * @param attrs
     */
    public ContainerFrameLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        if (getChildCount() > 0)
        {
            final int childMaxHeight = getMeasuredHeight();
            Log.d(Globals.TAG, "ContainerFrameLayout setting TimersList max height to " + childMaxHeight);
            ((ActiveTimerListView)getChildAt(0)).setMaxHeight(childMaxHeight);
        }
    }

}
