package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.measuredsoftware.android.timer.R;

/**
 * Main background, draws a drop shadow on its top.
 * 
 * @author neil
 * 
 */
public class Background extends ImageView
{
    private final Drawable dropShadow;
    private boolean shadowSized = false;
    
    /**
     * @param context
     * @param attrs
     */
    public Background(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        
        dropShadow = context.getResources().getDrawable(R.drawable.drop_shadow);
    }
    
    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);
        
        if (!shadowSized) 
        {
            dropShadow.setBounds(0, 0, getWidth(), dropShadow.getIntrinsicHeight());
        }
        
        /* draw drop shadow */
        dropShadow.draw(canvas);
    }
}
