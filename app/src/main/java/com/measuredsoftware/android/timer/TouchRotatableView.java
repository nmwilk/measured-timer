package com.measuredsoftware.android.timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.measuredsoftware.android.timer.model.RotationModel;

/**
 * View that can rotate.
 *
 * @author neil
 */
public class TouchRotatableView extends View
{
    private final RotationModel rotationModel;

    private boolean multitouchActive; // >1 finger down currently?

    private Bitmap ignoreMask;
    private int ignoreMaskWidth;
    private int ignoreMaskHeight;
    private boolean useMaskOnDownOnly;

    private boolean usedDownAction;
    private float anglePrev = Float.MAX_VALUE;

    public TouchRotatableView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        multitouchActive = false;

        useMaskOnDownOnly = false;

        usedDownAction = false;

        rotationModel = new RotationModel();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        rotationModel.setPivot(w / 2, h / 2);
    }

    protected float getTouchPivotX()
    {
        return rotationModel.getPivotX();
    }

    protected float getTouchPivotY()
    {
        return rotationModel.getPivotY();
    }

    public void setMinimumTotalAngle(final float minimumTotalAngle)
    {
        rotationModel.setMinAngle((int) minimumTotalAngle);
    }

    public void setMaximumTotalAngle(final float maximumTotalAngle)
    {
        rotationModel.setMaxAngle((int) maximumTotalAngle);
    }

    protected boolean isMultitouchActive()
    {
        return multitouchActive;
    }

    protected void setMultitouchActive(final boolean multitouchActive)
    {
        this.multitouchActive = multitouchActive;
    }

    /**
     * @param incPerSection degrees per section
     */
    public void setIncrement(final float incPerSection)
    {
        rotationModel.setSnapTo((int) incPerSection);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        boolean handled = false;

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        if (ignoreMask != null && !useMaskOnDownOnly && x < ignoreMaskWidth && y < ignoreMaskHeight)
        {
            if (ignoreMask.getPixel((int) x, (int) y) == Color.WHITE)
            {
                return true;
            }
        }

        final int actionMasked = action & MotionEvent.ACTION_MASK;
        switch (actionMasked)
        {
            case MotionEvent.ACTION_DOWN:
                if (ignoreMask != null && useMaskOnDownOnly && x < ignoreMaskWidth && y < ignoreMaskHeight)
                {
                    if (ignoreMask.getPixel((int) x, (int) y) == Color.WHITE)
                    {
                        return true;
                    }
                }
                usedDownAction = true;

                rotationModel.setTouch(0, (int) x, (int) y);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!usedDownAction)
                {
                    return true;
                }

                rotationModel.setTouch(1, (int) event.getX(1), (int) event.getY(1));
                setMultitouchActive(true);
                break;
            case MotionEvent.ACTION_UP:
                if (!usedDownAction)
                {
                    return true;
                }
                rotationModel.setTouchEnded(0);
                usedDownAction = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                rotationModel.setTouchEnded(1);
                setMultitouchActive(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!usedDownAction)
                {
                    return true;
                }

                final int ptrCount = event.getPointerCount();
                for (int i = 0; i < ptrCount; i++)
                {
                    if (event.getPointerId(i) == 1)
                    {
                        rotationModel.setTouch(1, (int) event.getX(i), (int) event.getY(i));
                    }
                    else
                    {
                        rotationModel.setTouch(0, (int) x, (int) y);
                    }
                }

                break;
        }


        final float newAngle = (float) rotationModel.getDisplayAngle();
        if (anglePrev != newAngle)
        {
            this.invalidate();
        }

        anglePrev = newAngle;

        return handled;
    }

    public void setAngle(final float angle)
    {
        rotationModel.setAngle(angle);
    }

    protected float getTotalAngle()
    {
        return (float) rotationModel.getTotalAngle();
    }

    protected float getDisplayAngle()
    {
        return (float) rotationModel.getDisplayAngle();
    }

    public void setIgnoreMask(final Bitmap ignoreMask, final boolean useOnlyOnDown)
    {
        this.ignoreMask = ignoreMask;
        ignoreMaskWidth = 0;
        ignoreMaskHeight = 0;
        if (this.ignoreMask != null)
        {
            ignoreMaskWidth = this.ignoreMask.getWidth();
            ignoreMaskHeight = this.ignoreMask.getHeight();
        }
        useMaskOnDownOnly = useOnlyOnDown;
    }
}
