package com.measuredsoftware.android.timer.viewgroups;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.measuredsoftware.android.timer.R;
import com.measuredsoftware.android.timer.views.ActiveTimerListView;
import com.measuredsoftware.android.timer.views.TimerView;

/**
 */
public class ContainerView extends RelativeLayout
{
    private TimerView timerView;
    private ActiveTimerListView listView;
    private View stopView;

    public ContainerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        View.inflate(context, R.layout.content, this);
    }

    public TimerView getTimerView()
    {
        return timerView;
    }

    public ActiveTimerListView getListView()
    {
        return listView;
    }

    public View getStopView()
    {
        return stopView;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        timerView = (TimerView) findViewById(R.id.the_dial);
        listView = (ActiveTimerListView) findViewById(R.id.timer_list);
        stopView = findViewById(R.id.stop_button_container);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b)
    {
        super.onLayout(changed, l, t, r, b);

        stopView.layout(l, timerView.getTop(), r, timerView.getBottom());
    }
}
