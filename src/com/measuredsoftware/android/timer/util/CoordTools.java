package com.measuredsoftware.android.timer.util;

public class CoordTools
{
    public static class Velocity
    {
        public float x;
        public float y;
    }

    public static float getAngleFromVelocity(float vx, float vy)
    {
        float angle = (float) Math.toDegrees(Math.atan2(vx, -vy));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public static float getAngleDifference(float angleA, float angleB)
    {
        float difference = angleA - angleB;
        while (difference < -180)
        {
            difference += 360;
        }
        while (difference > 180)
        {
            difference -= 360;
        }
        return difference;
    }

    public static double limitValue(final double value, final double min, final double max)
    {
        return Math.max(min, Math.min(value, max));
    }

    public static void getOffsetFromAngleAndHyp(final float angle, final float hyp, final Velocity offset)
    {
        final float radians = (float) Math.toRadians(angle % 360);
        final float newXOffset = ((float) Math.sin(radians)) * hyp;
        final float newYOffset = ((float) Math.cos(radians)) * hyp;

        offset.x = newXOffset;
        offset.y = newYOffset;
    }
}
