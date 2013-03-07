package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.measuredsoftware.android.timer.ColorFilterTools;
import com.measuredsoftware.android.timer.R;

/**
 * Tints the button when touched/selected.
 * 
 * @author neil
 * 
 */
public class TintedImageButton extends ImageButton
{
    private static final int FILTER_TYPE_NONE = -1;
    private static final int FILTER_TYPE_DIM = 0;
    private static final int FILTER_TYPE_DESAT = 1;
    private static final int FILTER_TYPE_BOTH = 2;

    private final ColorFilter filterDown;
    private final ColorFilter filterUp;

    private boolean filterActive;

    /**
     * @param context
     * @param attrs
     */
    public TintedImageButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TintedImageButton);

        final int upFilter = ta.getInteger(R.styleable.TintedImageButton_upTintType, FILTER_TYPE_NONE);
        final int downFilter = ta.getInteger(R.styleable.TintedImageButton_downTintType, FILTER_TYPE_DIM);

        filterUp = createFilter(upFilter);
        filterDown = createFilter(downFilter);

        ta.recycle();
    }
    
    private static void dimMatrix(final ColorMatrix cm)
    {
        ColorFilterTools.adjustBrightness(cm, -50);
    }
    
    private static void desatMatrix(final ColorMatrix cm)
    {
        ColorFilterTools.adjustSaturation(cm, -70);
    }

    private static ColorFilter createFilter(final int type)
    {
        final ColorFilter newFilter;
        switch (type)
        {
            case FILTER_TYPE_DIM:
                final ColorMatrix dim = new ColorMatrix();
                dimMatrix(dim);
                newFilter = new ColorMatrixColorFilter(dim);
                break;
            case FILTER_TYPE_DESAT:
                final ColorMatrix desat = new ColorMatrix();
                desatMatrix(desat);
                newFilter = new ColorMatrixColorFilter(desat);
                break;
            case FILTER_TYPE_BOTH:
                final ColorMatrix both = new ColorMatrix();
                desatMatrix(both);
                dimMatrix(both);
                newFilter = new ColorMatrixColorFilter(both);
                break;
            default:
            case FILTER_TYPE_NONE:
                newFilter = null;
                break;
        }

        return newFilter;
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
        
        if (pressed)
        {
            filterActive = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        getDrawable().setColorFilter(filterActive ? filterDown : filterUp);
        super.onDraw(canvas);
    }
}
