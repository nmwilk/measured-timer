package com.measuredsoftware.android.timer;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.format.Time;
import com.measuredsoftware.android.timer.views.TimerTextView.TextType;

/**
 * Place to put all global consts.
 * 
 * @author neil
 *
 */
public class Globals
{
    public static final float DEFAULT_HUE_VALUE = 0.4f;

    /** */
    public static final boolean DEBUG_LAYOUT = false;
    
    /** use seconds instead of minutes */
    public static final boolean DEBUG_QUICK_TIME = false;
    
    /* the name of the font resource */
    private static final String FONT_STRING = "creative.ttf";

    /* our time format */
    private static final String TIME_FORMAT = "%H:%M:%S";

    /** standard logging tag */
    @SuppressWarnings("UnusedDeclaration")
    public static final String TAG = "MT";
    
    private static Typeface mFont;

    private static final int TEXT_COLOUR_DEFAULT_COUNTDOWN = 0xFFFFFFFF;
    private static int textColorDefaultEndTime = 0;
    
    /**
     * Call once before using Globals. 
     */
    public static void init(final Resources resources)
    {
        mFont = Typeface.createFromAsset(resources.getAssets(), FONT_STRING);
        textColorDefaultEndTime = resources.getColor(R.color.tint);
    }
    
    /**
     * @return The current time.
     */
    public static long getTime()
    {
        return System.currentTimeMillis();
    }

    private static final Time TIME = new Time();

    /**
     * @return The remaining time formatted as HH:MM:SS.
     */
    public static String getFormattedTimeRemaining(final long secondsRemaining)
    {
        final long seconds = (secondsRemaining < 0) ? 0 : secondsRemaining;
        
        final int secs = (int)(seconds % 60);
        final int mins = (int)(seconds / 60) % 60;
        final int hours = (int)(seconds / 3600);
        
        TIME.set(secs, mins, hours, 1, 1, 1999);
        return TIME.format(TIME_FORMAT);
    }
    
    /**
     * @return The end time formatted as HH:MM:SS.
     */
    public static String getFormattedTimeEnd(final long secondsRemaining)
    {
        final long seconds = (secondsRemaining < 0) ? 0 : secondsRemaining;
        
        TIME.set(getTime() + (seconds * 1000));
        
        return TIME.format(TIME_FORMAT);
    }
    
    /**
     * @return The typeface to use throughout the app.
     */
    public static Typeface getFont()
    {
        if (mFont == null)
        {
            throw new RuntimeException("Font not initialised. Call Globals.init(Resources res).");
        }
        return mFont;
    }

    /**
     * @return ARGB colour
     */
    public static int getTextColor(final TextType type)
    {
        return type == TextType.COUNTDOWN ? TEXT_COLOUR_DEFAULT_COUNTDOWN : textColorDefaultEndTime ;
    }
    
}
