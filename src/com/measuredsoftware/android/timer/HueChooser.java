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
     * @param dismissListener 
     * @param seekListener 
     */
    public HueChooser(final View anchor, final OnDismissListener dismissListener, final SeekBar.OnSeekBarChangeListener seekListener)
    {
        final View contents = new HueChooserView(anchor.getContext(), seekListener);
        
        popup = new GeneralPopup(contents, anchor, dismissListener);
    }
    
    private class HueChooserView extends FrameLayout
    {
        public HueChooserView(final Context context, final SeekBar.OnSeekBarChangeListener seekListener)
        {
            super(context);
            
            setBackgroundResource(R.drawable.popup);
            
            final SeekBar seekBar = new SeekBar(context);
            seekBar.setOnSeekBarChangeListener(seekListener);
            seekBar.setMax(SEEK_MAX);
            
            addView(seekBar, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    /** */
    public void close()
    {
        popup.dismiss();
    }
}
