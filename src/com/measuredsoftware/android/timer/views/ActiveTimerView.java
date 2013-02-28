package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.measuredsoftware.android.timer.R;

/**
 * Item in the list showing an active timer.
 * 
 * @author neil
 * 
 */
public class ActiveTimerView extends RelativeLayout
{
    private final int timerId;
    
    private final TextView countdownTextView;
    
    /**
     * @param context
     * @param timerId 
     * @param countdownTime the formatted string for the countdown time.
     * @param targetTime the formatted string for the target time.
     */
    public ActiveTimerView(final Context context, final int timerId, final String countdownTime, final String targetTime)
    {
        super(context);
        
        this.timerId = timerId;
        
        View.inflate(context, R.layout.active_timer, this);
        
        setBackgroundResource(R.drawable.timer_list_back);
        
        countdownTextView = (TextView)findViewById(R.id.countdown_time);
        countdownTextView.setText(countdownTime);
        
        final TextView targetTextView = (TextView)findViewById(R.id.target_time);
        targetTextView.setText(targetTime);
    }
    
    /**
     * @param time the formatted string.
     */
    public void setCountdownTime(final String time)
    {
        countdownTextView.setText(time);
    }

    /**
     * @return uid of the timer added.
     */
    public int getTimerId()
    {
        return this.timerId;
    }
}
