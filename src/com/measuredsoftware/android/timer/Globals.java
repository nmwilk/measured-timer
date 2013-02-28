package com.measuredsoftware.android.timer;

import android.content.res.Resources;
import android.graphics.Typeface;

/**
 * Place to put all global consts.
 * 
 * @author neil
 *
 */
public class Globals
{
    /* the name of the font resource */
    private static final String FONT_STRING = "creative.ttf";

    /** */
    public static final boolean DEBUG_LAYOUT = true;

    /** our time format */
    public static final String TIME_FORMAT = "%H:%M:%S";

    /** standard logging tag */
    public static final String TAG = "MT";
    
    private static int tintColour = 0;
    
    private static Typeface mFont;
    
    /**
     * Call once before using Globals. 
     * @param resources
     */
    public static void init(final Resources resources)
    {
        tintColour = resources.getColor(R.color.tint);
        mFont = Typeface.createFromAsset(resources.getAssets(), FONT_STRING);
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
    
}
