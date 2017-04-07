package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.Colourable;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.data.EndTimes;
import com.measuredsoftware.android.timer.data.EndTimes.Alarm;

/**
 * List of active timers
 * 
 * @author neil
 * 
 */
public class ActiveTimerListView extends LinearLayout implements Colourable
{
    private static ActiveTimerListView.LayoutParams lp;

    private int childHeight;

    private EndTimes alarms;

    private OnClickListener cancelClickListener;
    
    public ActiveTimerListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        setOrientation(VERTICAL);

        if (lp == null)
        {
            lp = new ActiveTimerListView.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                    R.dimen.active_timer_height));
            final int horizMargin = getResources().getDimensionPixelSize(R.dimen.active_timer_margin_horiz);
            final int vertMargin = getResources().getDimensionPixelSize(R.dimen.active_timer_margin_vert);
            lp.setMargins(horizMargin, vertMargin, horizMargin, vertMargin);
        }

        childHeight = 0;
    }
    
    public void setCancelClickListener(final View.OnClickListener cancelClickListener)
    {
        this.cancelClickListener = cancelClickListener;
    }

    public void setAlarms(final EndTimes alarms)
    {
        this.alarms = alarms;
    }
    
    public int getTimerHeight()
    {
        return childHeight;
    }

    public void tickAlarms()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            final ActiveTimerView timerView = (ActiveTimerView) getChildAt(i);
            timerView.updateCountdown();
        }
    }

    /**
     * Cause the View to update its children from the model.
     */
    public void updateAlarms()
    {
        // REMOVE EXPIRED VIEWS:
        // go through our sub views looking for alarms not in the list we've
        // been given.
        for (int i = 0; i < getChildCount(); i++)
        {
            final ActiveTimerView timerView = (ActiveTimerView) getChildAt(i);
            boolean found = false;
            for (int a = 0; a < alarms.count(); a++)
            {
                final Alarm alarm = alarms.getTime(a);
                if (alarm.uid == timerView.getAlarm().uid)
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                removeView(timerView);
                --i;
            }
        }

        // ADD NEW VIEWS
        // now go through the alarms checking for new ones
        for (int a = 0; a < alarms.count(); a++)
        {
            boolean found = false;
            for (int i = 0; i < getChildCount(); i++)
            {
                final ActiveTimerView timerView = (ActiveTimerView) getChildAt(i);
                if (alarms.getTime(a).uid == timerView.getAlarm().uid)
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                final ActiveTimerView newTimer = new ActiveTimerView(getContext(), alarms.getTime(a), cancelClickListener);
                addTimer(newTimer);
            }
        }
    }

    @Override
    public void addView(final View child)
    {
        if (!(child instanceof ActiveTimerView))
        {
            throw new RuntimeException("Can't add anything other than an ActiveTimerView to a TimersListView");
        }

        super.addView(child);
    }

    private void addTimer(final ActiveTimerView timer)
    {
        timer.setLayoutParams(lp);
        
        timer.setHue(filter);
        
        int earlierTimers = 0;
        final long endTime = timer.getAlarm().ms; 
        for(int i=0; i < getChildCount(); i++)
        {
            final ActiveTimerView child = (ActiveTimerView)getChildAt(i);
            
            if (endTime > child.getAlarm().ms)
            {
                ++earlierTimers;
            }
        }

        if (earlierTimers == getChildCount())
        {
            super.addView(timer);
        }
        else
        {
            super.addView(timer, earlierTimers);
        }
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (childHeight == 0 && getChildCount() > 0)
        {
            final View child = getChildAt(0);
            childHeight = child.getHeight();
            if (childHeight != 0)
            {
                final ActiveTimerListView.LayoutParams lp = (ActiveTimerListView.LayoutParams)child.getLayoutParams(); 
                childHeight += lp.bottomMargin;
                childHeight += lp.topMargin;
            }
        }
    }

    private ColorMatrixColorFilter filter;
    
    @Override
    public void onColourSet(final float colour)
    {
        final ColorMatrix hueMatrix = new ColorMatrix();
        ColorFilterTools.adjustHue(hueMatrix, Math.round(colour * 360) - 180);
        filter = new ColorMatrixColorFilter(hueMatrix);
        
        for(int i=0; i < getChildCount(); i++)
        {
            final ActiveTimerView timer = (ActiveTimerView)getChildAt(i);
            timer.setHue(filter);
        }
    }
}
