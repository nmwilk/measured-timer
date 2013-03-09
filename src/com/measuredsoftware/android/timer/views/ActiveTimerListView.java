package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.Colourable;
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
public class ActiveTimerListView extends LinearLayout implements Colourable
{
    /** */
    public interface LayoutListener
    {
        /** the View was laid out and sizes can be retrieved now */
        void wasLayedOut(boolean layoutChanged);
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
        else
        {
            setBackgroundDrawable(null);
        }

        if (lp == null)
        {
            lp = new ActiveTimerListView.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                    R.dimen.active_timer_height));
            final int horizMargin = getResources().getDimensionPixelSize(R.dimen.active_timer_margin_horiz);
            final int vertMargin = getResources().getDimensionPixelSize(R.dimen.active_timer_margin_vert);
            lp.setMargins(horizMargin, vertMargin, horizMargin, vertMargin);
        }
        
//        final LayoutTransition transition = getLayoutTransition();
//        if (transition != null)
//        {
//            Log.d(TAG, "CHANGE_APPEARING " + transition.getInterpolator(LayoutTransition.CHANGE_APPEARING).toString());
//            Log.d(TAG, "CHANGE_DISAPPEARING " + transition.getInterpolator(LayoutTransition.CHANGE_DISAPPEARING).toString());
//            //Log.d(TAG, "CHANGING " + transition.getInterpolator(LayoutTransition.CHANGING).toString());
//            Log.d(TAG, "APPEARING " + transition.getInterpolator(LayoutTransition.APPEARING).toString());
//            Log.d(TAG, "DISAPPEARING " + transition.getInterpolator(LayoutTransition.DISAPPEARING).toString());
//            
//            transition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new DecelerateInterpolator(2f));
//            transition.setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, new DecelerateInterpolator(2f));
//        }
        
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
     * 
     */
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
        
        if (layoutListener != null)
        { 
            layoutListener.wasLayedOut(changed);
        }
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
