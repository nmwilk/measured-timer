package com.measuredsoftware.android.timer;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.PopupWindow.OnDismissListener;

/**
 * Popups up a hue chooser.
 * 
 * @author neil
 *
 */
public class HueChooser
{
    /** the max value the seek will report, 0 being the min. */
    public static final int SEEK_MAX = 360;
    
    private final PopupWindow popup;

    /** 
     * @param anchor View to anchor the popup below.
     */
    public HueChooser(final View anchor, final float currentValue, final OnDismissListener dismissListener, final SeekBar.OnSeekBarChangeListener seekListener)
    {
        final View contents = new HueChooserView(anchor.getContext(), currentValue, seekListener);
        
        popup = new GeneralPopup(contents, anchor, dismissListener);
    }
    
    private class HueChooserView extends FrameLayout
    {
        public HueChooserView(final Context context, final float currentValue, final SeekBar.OnSeekBarChangeListener seekListener)
        {
            super(context);
            
            setBackgroundResource(R.drawable.popup);
            
            final SeekBar seekBar = new SeekBar(context);
            seekBar.setOnSeekBarChangeListener(seekListener);
            seekBar.setMax(SEEK_MAX);
            seekBar.setProgress(Math.round(currentValue * SEEK_MAX));
            
            final LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            final int margin = getResources().getDimensionPixelSize(R.dimen.hue_slider_margin);
            lp.setMargins(margin, margin, margin, margin);
            addView(seekBar, lp);
        }
    }

    /** */
    public void close()
    {
        if (popup.isShowing())
        {
            popup.dismiss();
        }
    }
}
