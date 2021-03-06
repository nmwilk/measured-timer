package com.measuredsoftware.android.timer.model;

import com.measuredsoftware.android.timer.util.CoordTools;

/**
 * Standalone model class for handling touch rotation around a pivot point.
 */
public class RotationModel
{
    private static final int MAX_TOUCHES = 2;
    private static final int TOUCH_NOT_STARTED = Integer.MAX_VALUE;

    private int minAngle;
    private int maxAngle;

    private int snapTo;

    private int pivotX;

    private int pivotY;
    private final int[] lastAngles = new int[MAX_TOUCHES];

    private float[] angleFactor = new float[MAX_TOUCHES];

    private double angle;

    public RotationModel()
    {
        reset();

        minAngle = Integer.MIN_VALUE;
        maxAngle = Integer.MAX_VALUE;
        snapTo = 1;

        angleFactor[0] = 1.0f;
        angleFactor[1] = 1.0f;
    }

    public final void setAngle(final double angle)
    {
        reset();
        this.angle = angle;
    }

    public final void setAngleFactor(final int touchIndex, final float angleFactor)
    {
        if (validTouchBounds(touchIndex))
        {
            this.angleFactor[touchIndex] = angleFactor;
        }
    }

    private void reset()
    {
        for (int touch = 0; touch < MAX_TOUCHES; touch++)
        {
            lastAngles[touch] = TOUCH_NOT_STARTED;
        }

        angle = 0;
    }

    public void setSnapTo(final int snapTo)
    {
        if (snapTo > 0)
        {
            this.snapTo = snapTo;
        }
    }

    public void setPivot(final int pivotX, final int pivotY)
    {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    public void setMinAngle(int minAngle)
    {
        this.minAngle = minAngle;
    }

    public void setMaxAngle(int maxAngle)
    {
        this.maxAngle = maxAngle;
    }

    public void setTouch(final int touchIndex, final int touchX, final int touchY)
    {
        if (validTouchBounds(touchIndex))
        {
            // get angleCurrent of current touch from pivot point.
            final int vx = touchX - pivotX;
            final int vy = touchY - pivotY;
            final int touchedAngle = Math.round(CoordTools.getAngleFromVelocity(vx, vy));

            if (lastAngles[touchIndex] != TOUCH_NOT_STARTED)
            {
                final float angleDifference = CoordTools.getAngleDifference(touchedAngle, lastAngles[touchIndex]) * angleFactor[touchIndex];
                angle += angleDifference;

                angle = CoordTools.limitValue(angle, minAngle, maxAngle);
            }

            lastAngles[touchIndex] = touchedAngle;
        }
    }

    public void setTouchEnded(final int touchIndex)
    {
        if (validTouchBounds(touchIndex))
        {
            lastAngles[touchIndex] = TOUCH_NOT_STARTED;
        }
    }

    /**
     * @return The angle to display at, a value between 0-359.9.
     */
    public double getDisplayAngle()
    {
        final double moddedTotalAngle = getTotalAngle() % 360.0f;

        return moddedTotalAngle < 0.0f ? 360.0f + moddedTotalAngle : moddedTotalAngle;
    }

    /**
     * @return The total angle rotated. Negative values are anti-clockwise, positive values are clockwise.
     */
    public double getTotalAngle()
    {
        return snapAngle(angle);
    }

    private boolean validTouchBounds(final int touchIndex)
    {
        return touchIndex >= 0 && touchIndex < MAX_TOUCHES;
    }

    private double snapAngle(final double angleSigned)
    {
        final boolean isNegative = angleSigned < 0;
        final double angle = isNegative ? Math.abs(angleSigned) : angleSigned;

        final double diff = angle % snapTo;

        double r = angle;
        final int halfSnap = snapTo / 2;
        if (diff < halfSnap)
        {
            r -= diff;
        }
        else if (diff > halfSnap)
        {
            r += (snapTo - diff);
        }

        return isNegative ? -r : r;
    }

    public int getPivotY()
    {
        return pivotY;
    }

    public int getPivotX()
    {
        return pivotX;
    }


}
