package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.measuredsoftware.android.timer.Globals;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.data.EndTimes;
import com.measuredsoftware.android.timer.data.EndTimes.Alarm;

/**
 * List of active timers
 * 
 * @author neil
 * 
 */
public class ActiveTimerListView extends LinearLayout
{
    private static final String TAG = "ATLV";
    
    private static ActiveTimerListView.LayoutParams lp;

    private int childHeight;
    private int maxHeight;

    private EndTimes alarms;

    private OnClickListener cancelClickListener;
    
    /**
     * @param context
     * @param attrs
     */
    public ActiveTimerListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        setOrientation(VERTICAL);

        if (Globals.DEBUG_LAYOUT)
        {
            setBackgroundColor(0x40FFFF00);
        }

        if (lp == null)
        {
            lp = new ActiveTimerListView.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                    R.dimen.active_timer_height));
        }

        childHeight = 0;
    }
    
    /**
     * @param cancelClickListener
     */
    public void setCancelClickListener(final View.OnClickListener cancelClickListener)
    {
        this.cancelClickListener = cancelClickListener;
    }
    
    /**
     * @param alarms
     */
    public void setAlarms(final EndTimes alarms)
    {
        this.alarms = alarms;
    }

    /**
     * 
     * @param maxHeight
     */
    public void setMaxHeight(final int maxHeight)
    {
        this.maxHeight = maxHeight;
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
            Log.d(TAG, "Looking for View with uid " + timerView.getAlarm().uid);
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
                Log.d(TAG, "- didn't find it");
                removeView(timerView);
                --i;
            }
            else
            {
                Log.d(TAG, "- found it");
            }
        }

        // ADD NEW VIEWS
        // now go through the alarms checking for new ones
        for (int a = 0; a < alarms.count(); a++)
        {
            boolean found = false;
            Log.d(TAG, "Looking for Alarm with uid " + alarms.getTime(a).uid);
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
                Log.d(TAG, "- didn't find it");
                final ActiveTimerView newTimer = new ActiveTimerView(getContext(), alarms.getTime(a), cancelClickListener);
                addTimer(newTimer);
            }
            else
            {
                Log.d(TAG, "- found it");
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
        super.addView(timer, lp);
    }

    /**
     * @param timerId
     */
    public void removeTimer(final int timerId)
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            final ActiveTimerView timer = (ActiveTimerView) getChildAt(i);
            if (timer.getAlarm().uid == timerId)
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
