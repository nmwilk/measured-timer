package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Tints the button when touched/selected.
 * 
 * @author neil
 * 
 */
public class TintedImageButton extends ImageButton
{
    private final ColorFilter colorFilter = new PorterDuffColorFilter(0x7F000000, PorterDuff.Mode.SRC_ATOP);
    private boolean filterActive;
    
    /**
     * @param context
     * @param attrs
     */
    public TintedImageButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        filterActive = selected;
    }
    
    @Override
    public void setPressed(boolean pressed)
    {
        super.setPressed(pressed);
        filterActive = pressed;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        getDrawable().setColorFilter(filterActive ? colorFilter : null);
        super.onDraw(canvas);
    }
}
