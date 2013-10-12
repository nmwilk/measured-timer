package com.measuredsoftware.android.timer.util;

/**
 * Various generic value tools.
 * 
 * @author neil
 * 
 */
public class ValueTools
{

    /**
     * @return The progress (0-1) value.
     */
    public static float getProgress(final float actualSpd, final float maxSpd)
    {
        return (maxSpd == 0f) ? 1f : actualSpd / maxSpd;
    }

    /**
     * @return The progress (0-1) of the current, in the range min to max.
     */
    public static float getProgressBetween(final float current, final float min, final float max)
    {
        return getProgress(current - min, max - min);
    }

    /**
     * @return A limited float.
     */
    public static float limitFloat(final float value, final float max)
    {
        return (value < max) ? value : max;
    }

    /**
     * 
     * @return The value between min and max value, at the progress specified.
     */
    public static float getValueBetween(final float progress, final float minValue, final float maxValue)
    {
        return minValue + (progress * (maxValue - minValue));
    }

    /**
     * @return An array of 4 floats representing argb values (each in the range
     *         0-1).
     */
    public static float[] getARGB(final String hexString)
    {
        final float rgba[] = new float[4];

        final int stringOffset;

        if (hexString.length() > 6)
        {
            final int a = Integer.parseInt(hexString.substring(0, 2), 16);
            rgba[0] = (float) a / 255f;
            stringOffset = 0;
        }
        else
        {
            rgba[0] = 1f;
            stringOffset = 2;
        }

        // 01234567
        // ffc01265
        final int r = Integer.parseInt(hexString.substring(2 - stringOffset, 4 - stringOffset), 16);
        final int g = Integer.parseInt(hexString.substring(4 - stringOffset, 6 - stringOffset), 16);
        final int b = Integer.parseInt(hexString.substring(6 - stringOffset, 8 - stringOffset), 16);

        rgba[1] = (float) r / 255f;
        rgba[2] = (float) g / 255f;
        rgba[3] = (float) b / 255f;
        return rgba;
    }

    /**
     * Ensures value is within bounds.
     * 
     * @return value limited to between min and max.
     */
    public static float limitFloat(final float value, final float min, final float max)
    {
        final float theResult;
        if (value < min)
        {
            theResult = min;
        }
        else if (value > max)
        {
            theResult = max;
        }
        else
        {
            theResult = value;
        }

        return theResult;
    }

    /**
     * @return 3 -> "3rd", 127 -> "127th etc.
     */
    public static String getNumberAsFormattedRank(final int rank)
    {
        return String.format("%d%s", rank, getFormattedRankSuffix(rank));
    }
    
    /**
     * @return 3 -> "3rd", 127 -> "127th etc.
     */
    public static String getNumberAsFormattedRank(final String rank)
    {
        return getNumberAsFormattedRank(Integer.valueOf(rank));
    }

    /**
     * @return Just the suffix to apply. E.g. rank = 2, return "nd"
     */
    public static String getFormattedRankSuffix(int rank)
    {
        final String suffix;
        
        final int ones = rank % 10;
        final int tens = (int) (Math.floor(rank / 10) % 10);
        if (tens == 1)
        {
            suffix = "th";
        }
        else
        {
            switch (ones)
            {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
            }
        }
        
        return suffix;
    }
}
