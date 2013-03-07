package com.measuredsoftware.android.timer;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.FloatMath;
import android.util.Log;

/**
 * Color Filter tools.
 * 
 * @author neil
 * 
 */
public class ColorFilterTools
{
    /**
     * @param hue
     *            Value between -180 and 180
     * @param saturation
     *            Value between -100 and 100
     * @param brightness
     *            Value between -100 and 100
     * @param contrast
     *            Value between -100 and 100
     * @return A new ColorMatrixColorFilter with the values set as specified.
     */
    public static ColorFilter getFilter(final float hue, final float saturation, final float brightness,
            final float contrast)
    {
        final ColorMatrix cm = new ColorMatrix();

        adjustHue(cm, hue);
        adjustSaturation(cm, saturation);
        adjustBrightness(cm, brightness);
        adjustContrast(cm, contrast);

        return new ColorMatrixColorFilter(cm);
    }

    protected static float cleanValue(float p_val, float p_limit)
    {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    /**
     * @param cm
     *            The ColorMatrix instance to apply the hue adjustment to.
     * @param val
     *            Value between -180 and 180
     */
    public static void adjustHue(final ColorMatrix cm, final float val)
    {
        final float value = cleanValue(val, 180f) / 180f * (float) Math.PI;
        if (value == 0)
        {
            return;
        }
        float cosVal = FloatMath.cos(value);
        float sinVal = FloatMath.sin(value);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]
        { lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG),
                lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0, lumR + cosVal * (-lumR) + sinVal * (0.143f),
                lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG),
                lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f };

        cm.postConcat(new ColorMatrix(mat));
    }

    /**
     * @param cm
     *            The ColorMatrix instance to apply the saturation adjustment
     *            to.
     * @param val
     *            Value between -100 and 100
     */
    public static void adjustSaturation(final ColorMatrix cm, final float val)
    {
        final float value = ValueTools.limitFloat(val, -100, 100);
        if (value == 0)
        {
            return;
        }
        float x = 1 + ((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;
        float[] mat = new float[]
        { lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0, lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x),
                0, 0, lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
        cm.postConcat(new ColorMatrix(mat));
        // cm.setSaturation(0);
    }

    /**
     * @param cm
     *            The ColorMatrix instance to apply the brightness adjustment
     *            to.
     * @param val
     *            Value between -100 and 100
     */
    public static void adjustBrightness(final ColorMatrix cm, final float val)
    {
        final float value = ValueTools.limitFloat(val, -100, 100);
        if (value == 0)
        {
            return;
        }
        float[] mat = new float[]
        { 1, 0, 0, 0, value, 0, 1, 0, 0, value, 0, 0, 1, 0, value, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
        cm.postConcat(new ColorMatrix(mat));
    }

    private final static float[] DELTA_INDEX = new float[]
    { 0f, 0.01f, 0.02f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.1f, 0.11f, 0.12f, 0.14f, 0.15f, 0.16f, 0.17f, 0.18f,
            0.20f, 0.21f, 0.22f, 0.24f, 0.25f, 0.27f, 0.28f, 0.30f, 0.32f, 0.34f, 0.36f, 0.38f, 0.40f, 0.42f, 0.44f,
            0.46f, 0.48f, 0.5f, 0.53f, 0.56f, 0.59f, 0.62f, 0.65f, 0.68f, 0.71f, 0.74f, 0.77f, 0.80f, 0.83f, 0.86f,
            0.89f, 0.92f, 0.95f, 0.98f, 1.0f, 1.06f, 1.12f, 1.18f, 1.24f, 1.30f, 1.36f, 1.42f, 1.48f, 1.54f, 1.60f,
            1.66f, 1.72f, 1.78f, 1.84f, 1.90f, 1.96f, 2.0f, 2.12f, 2.25f, 2.37f, 2.50f, 2.62f, 2.75f, 2.87f, 3.0f,
            3.2f, 3.4f, 3.6f, 3.8f, 4.0f, 4.3f, 4.7f, 4.9f, 5.0f, 5.5f, 6.0f, 6.5f, 6.8f, 7.0f, 7.3f, 7.5f, 7.8f, 8.0f,
            8.4f, 8.7f, 9.0f, 9.4f, 9.6f, 9.8f, 10.0f };

    /**
     * @param cm
     *            The ColorMatrix instance to apply the contrast adjustment to.
     * @param val
     *            Value between -100 and 100
     */
    public static void adjustContrast(final ColorMatrix cm, final float val)
    {
        final float value = ValueTools.limitFloat(val, -100, 100);
        if (value != 0)
        {
            float x;

            if (value < 0)
            {
                x = 127 + value / 100 * 127;
            }
            else
            {
                x = value % 1;
                if (x == 0)
                {
                    x = DELTA_INDEX[(int) value];
                }
                else
                {
                    x = DELTA_INDEX[((int) value << 0)] * (1 - x) + DELTA_INDEX[((int) value << 0) + 1] * x;
                }
                x = x * 127 + 127;
            }
            float[] mat = new float[]
            { x / 127, 0, 0, 0, 0.5f * (127 - x), 0, x / 127, 0, 0, 0.5f * (127 - x), 0, 0, x / 127, 0,
                    0.5f * (127 - x), 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
            cm.postConcat(new ColorMatrix(mat));
        }
    }
}
