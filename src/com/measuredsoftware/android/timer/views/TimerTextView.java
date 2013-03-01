package com.measuredsoftware.android.timer.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.measuredsoftware.android.timer.Globals;
import com.measuredsoftware.android.timer.R;

/**
 * @author neil
 */
public class TimerTextView extends TextView
{
    private static final int TEXT_COLOUR_DEFAULT_COUNTDOWN = 0xFFFFFFFF;
    private static int textColorDefaultEndTime;

    /** */
    public enum TextType
    {
        /** countdown time */
        COUNTDOWN,

        /** target time */
        TARGET
    }

    /**
     * @param context
     * @param attrs
     */
    public TimerTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        setTextEndTimeTextColour(getResources());

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView);
        final TextType type = TextType.values()[a.getInt(R.styleable.TimerTextView_textType, 0)];
        a.recycle();

        TimerTextView.styleTextView(this, type);
    }

    private static void setTextEndTimeTextColour(final Resources resources)
    {
        if (textColorDefaultEndTime == 0)
        {
            textColorDefaultEndTime = resources.getColor(R.color.tint);
        }
    }

    /**
     * @param type
     * @param size
     * @return A new Paint instance in a style given the params.
     */
    public static Paint stylePaint(final TextType type, final float size)
    {
        final float textSize = size / (type == TextType.COUNTDOWN ? 9f : 11f);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Globals.getFont());
        paint.setTextSize(textSize);
        paint.setTextAlign(Align.CENTER);
        paint.setColor(type == TextType.COUNTDOWN ? TEXT_COLOUR_DEFAULT_COUNTDOWN : textColorDefaultEndTime);
        paint.setShadowLayer(textSize / 18f, 0, textSize / 20f, 0x7F000000);

        return paint;
    }

    /**
     * @param textView
     * @param type
     */
    public static void styleTextView(final TextView textView, final TextType type)
    {
        setTextEndTimeTextColour(textView.getResources());
        
        textView.setTypeface(Globals.getFont());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(type == TextType.COUNTDOWN ? TEXT_COLOUR_DEFAULT_COUNTDOWN : textColorDefaultEndTime);
        final float textSize = textView.getTextSize();
        textView.setShadowLayer(textSize / 18f, 0, textSize / 20f, 0x7F000000);
    }
}
