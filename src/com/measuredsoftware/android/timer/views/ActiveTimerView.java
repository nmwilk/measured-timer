package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.Colourable;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.data.EndTimes.Alarm;

/**
 * Item in the list showing an active timer.
 * 
 * @author neil
 * 
 */
public class ActiveTimerView extends RelativeLayout
{
    private final Alarm alarm;
    private final ColorFilter dimmer;

    private final TextView countdownTextView;
    private final TextView targetTextView;

    /**
     * @param context
     * @param alarm
     *            The model.
     * @param cancelClickListener
     *            The listener for the cancel button.
     */
    public ActiveTimerView(final Context context, final Alarm alarm, final View.OnClickListener cancelClickListener)
    {
        super(context);

        this.alarm = alarm;

        View.inflate(context, R.layout.active_timer, this);

        setBackgroundResource(R.drawable.timer_list_back);

        countdownTextView = (TextView) findViewById(R.id.countdown_time);
        countdownTextView.setText(alarm.getCountdownTime());

        targetTextView = (TextView) findViewById(R.id.target_time);
        targetTextView.setText(alarm.getTargetTime());
        targetTextView.setTextColor(getResources().getColor(R.color.tint));

        dimmer = new PorterDuffColorFilter(0x30FFFFFF, PorterDuff.Mode.SRC_ATOP);

        setOnClickListener(cancelClickListener);

        setId(R.id.active_timer_view);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        final boolean b = super.onTouchEvent(event);

        final int action = event.getAction();
        if (action != MotionEvent.ACTION_MOVE)
        {
            getBackground().setColorFilter(action == MotionEvent.ACTION_DOWN ? dimmer : null);
        }

        return b;
    }

    /**
     * @param time
     *            the formatted string.
     */
    public void setCountdownTime(final String time)
    {
        countdownTextView.setText(time);
    }

    /**
     * @return the model instance.
     */
    public Alarm getAlarm()
    {
        return this.alarm;
    }

    /**
     * Update the countdown time.
     */
    public void updateCountdown()
    {
        if (!countdownTextView.getText().equals(alarm.getCountdownTime()))
        {
            countdownTextView.setText(alarm.getCountdownTime());
        }
    }

    /**
     * @param colorFilter
     */
    public void setHue(final ColorFilter colorFilter)
    {
        targetTextView.getPaint().setColorFilter(colorFilter);
        targetTextView.invalidate();
    }
}
