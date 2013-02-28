package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.measuredsoftware.android.timer.R;

/**
 * Top bar of the app.
 * 
 * @author neil
 *
 */
public class TopBar extends RelativeLayout
{
    /**
     * @param context
     * @param attrs
     */
    public TopBar(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        
        View.inflate(context, R.layout.top_bar, this);
    }

    @Override
    public void setOnClickListener(final OnClickListener listener)
    {
        findViewById(R.id.settings_button).setOnClickListener(listener);
        findViewById(R.id.measured_button).setOnClickListener(listener);
    }
}
