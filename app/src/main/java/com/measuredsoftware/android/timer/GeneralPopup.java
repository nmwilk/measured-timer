package com.measuredsoftware.android.timer;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * General popup window.
 * 
 * @author neil
 * 
 */
public class GeneralPopup extends PopupWindow
{
    /**
     * @param contents
     * @param anchor
     * @param dismissListener 
     */
    public GeneralPopup(final View contents, final View anchor, final OnDismissListener dismissListener)
    {
        this(contents, anchor, dismissListener, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /**
     * @param contents
     * @param anchor
     * @param dismissListener 
     * @param width
     * @param height
     */
    public GeneralPopup(final View contents, final View anchor, final OnDismissListener dismissListener,
            final int width, final int height)
    {
        super(contents);

        setWidth(width);
        setHeight(height);

        setOnDismissListener(dismissListener);
        setBackgroundDrawable(new ColorDrawable(0));
        setOutsideTouchable(true);
        setFocusable(true);
        showAsDropDown(anchor);
    }
}
