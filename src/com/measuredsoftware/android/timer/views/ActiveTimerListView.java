package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    /** */
    public interface LayoutListener
    {
        /** the View was laid out and sizes can be retrieved now */
        void wasLayedOut();
    }
    
    private static final String TAG = "ATLV";
    
    private static ActiveTimerListView.LayoutParams lp;

    private int childHeight;

    private EndTimes alarms;

    private OnClickListener cancelClickListener;
    
    private LayoutListener layoutListener;
    
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
            final int margin = getResources().getDimensionPixelSize(R.dimen.active_timer_margin);
            lp.setMargins(margin, margin, margin, margin);
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
     * @param layoutListener
     */
    public void setLayoutListener(final LayoutListener layoutListener)
    {
        this.layoutListener = layoutListener;
    }
    
    /**
     * @param alarms
     */
    public void setAlarms(final EndTimes alarms)
    {
        this.alarms = alarms;
    }
    
    /**
     * @return The height of a single child including top and bottom margins.
     */
    public int getTimerHeight()
    {
        return childHeight;
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
            final View child = getChildAt(0);
            childHeight = child.getHeight();
            if (childHeight != 0)
            {
                final ActiveTimerListView.LayoutParams lp = (ActiveTimerListView.LayoutParams)child.getLayoutParams(); 
                childHeight += lp.bottomMargin;
                childHeight += lp.topMargin;
            }
        }
        
        if (layoutListener != null) layoutListener.wasLayedOut();
    }

    /**
     * @return Height plus the top and bottom margins.
     */
    public int getTotalHeight()
    {
        int topMargin = 0;
        int bottomMargin = 0;
        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)getLayoutParams();
        if (lp != null)
        {
            bottomMargin = lp.bottomMargin;
            topMargin = lp.topMargin;
        }
        return getHeight() + topMargin + bottomMargin;
    }
}
