package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.measuredsoftware.android.timer.Globals;

/**
 * List of active timers
 * 
 * @author neil
 * 
 */
public class TimersList extends LinearLayout
{
    private static TimersList.LayoutParams lp;
    private int childHeight;
    private int maxHeight;

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
        
        if (lp == null)
        {
            lp = new TimersList.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }

        childHeight = 0;
    }
    
    /**
     * 
     * @param maxHeight
     */
    public void setMaxHeight(final int maxHeight)
    {
        this.maxHeight = maxHeight;
    }
    
    @Override
    public void addView(final View child)
    {
        if (!(child instanceof ActiveTimer)) throw new RuntimeException("Can't add anything other than an ActiveTimerView to a TimersListView");
        
        super.addView(child);
    }
    
    /**
     * @param timer
     */
    public void addTimer(final ActiveTimer timer)
    {
        super.addView(timer, lp);
    }
    
    /**
     * @param timerId 
     */
    public void removeTimer(final int timerId)
    {
        for(int i=0; i < getChildCount(); i++)
        {
            final ActiveTimer timer = (ActiveTimer)getChildAt(i);
            if (timer.getTimerId() == timerId)
            {
                this.removeViewAt(i);
                break;
            }
        }
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (childHeight == 0 && getChildCount() > 0)
        {
            childHeight = getChildAt(0).getHeight();
        }
    }

    /**
     * @return true if there's room for another View like the one already added.
     *         true if none have been added yet.
     * 
     */
    public boolean hasSpace()
    {
        boolean hasSpace = true;

        // get last child position
        if (getChildCount() != 0)
        {
            final View lastChild = getChildAt(getChildCount() - 1);
            hasSpace = (lastChild.getBottom() + childHeight) < maxHeight;
        }

        return hasSpace;
    }
}
