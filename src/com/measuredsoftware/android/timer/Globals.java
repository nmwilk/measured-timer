package com.measuredsoftware.android.timer;

import com.measuredsoftware.android.timer.views.TimerTextView.TextType;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.format.Time;

/**
 * Place to put all global consts.
 * 
 * @author neil
 *
 */
public class Globals
{
    /** */
    public static final boolean DEBUG_LAYOUT = false;
    
    /** use seconds instead of minutes */
    public static final boolean DEBUG_QUICK_TIME = true;
    
    /* the name of the font resource */
    private static final String FONT_STRING = "creative.ttf";

    /* our time format */
    private static final String TIME_FORMAT = "%H:%M:%S";

    /** standard logging tag */
    public static final String TAG = "MT";
    
    private static int tintColour = 0;
    
    private static Typeface mFont;

    private static final int TEXT_COLOUR_DEFAULT_COUNTDOWN = 0xFFFFFFFF;
    private static int textColorDefaultEndTime = 0;
    
    /**
     * Call once before using Globals. 
     * @param resources
     */
    public static void init(final Resources resources)
    {
        tintColour = resources.getColor(R.color.tint);
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
     * @param secondsRemaining
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
     * @param secondsRemaining
     * @return The end time formatted as HH:MM:SS.
     */
    public static String getFormattedTimeEnd(final long secondsRemaining)
    {
        final long seconds = (secondsRemaining < 0) ? 0 : secondsRemaining;
        
        TIME.set(getTime() + (seconds * 1000));
        
        return TIME.format(TIME_FORMAT);
    }
    
    /**
     * @param colour argb value.
     */
    public static void setTintColour(final int colour)
    {
        tintColour = colour;
    }
    
    /**
     * @return The tint colour in use.
     */
    public static int getTintColour()
    {
        if (tintColour == 0)
        {
            throw new RuntimeException("Global colours not initialised. Call Globals.init(Resources res).");
        }
        return tintColour;
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
     * @param type
     * @return ARGB colour
     */
    public static int getTextColor(final TextType type)
    {
        return type == TextType.COUNTDOWN ? TEXT_COLOUR_DEFAULT_COUNTDOWN : textColorDefaultEndTime ;
    }
    
}
