package com.measuredsoftware.android.timer.views;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.Colourable;
import com.measuredsoftware.android.timer.Globals;

/**
 * Button to stop the timer from ringing.
 * 
 * @author neil
 * 
 */
public class StopButton extends Button implements Colourable
{
    private static class HighlightHandler extends Handler
    {
        private final WeakReference<StopButton> parent;

        public HighlightHandler(final StopButton parent)
        {
            this.parent = new WeakReference<StopButton>(parent);
        }

        @Override
        public void handleMessage(Message msg)
        {
            parent.get().getBackground().setColorFilter(null);
        }
    }
    
    private final ColorFilter highlight = new PorterDuffColorFilter(0x1FFFFFFF, PorterDuff.Mode.SRC_ATOP);
    private static HighlightHandler handler;
    
    /**
     * @param context
     * @param attrs
     */
    public StopButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        setTypeface(Globals.getFont());
        
        handler = new HighlightHandler(this);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        final int action = event.getAction();
        
        if (action == MotionEvent.ACTION_DOWN)
        {
            getBackground().setColorFilter(highlight);
        }
        else if (action == MotionEvent.ACTION_UP)
        {
            handler.sendEmptyMessageDelayed(0, 200);
        }
        
        return super.onTouchEvent(event);
    }

    @Override
    public void onColourSet(final float colour)
    {
        final ColorMatrix hueMatrix = new ColorMatrix();
        ColorFilterTools.adjustHue(hueMatrix, Math.round(colour * 360) - 180);
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(hueMatrix);
        getPaint().setColorFilter(filter);
        invalidate();
    }
}
