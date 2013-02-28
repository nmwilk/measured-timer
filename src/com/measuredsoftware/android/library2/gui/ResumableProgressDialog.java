package com.measuredsoftware.android.library2.gui;

import android.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * A subclass of ProgressDialog which fixes 
 * http://code.google.com/p/android/issues/detail?id=4266 (Animation in 
 * indeterminate ProgressDialog freezes in Donut).
 * <p>
 * Usage:
 * <pre>
 * // in an Activity subclass for example
 * protected Dialog onCreateDialog(int id)
 * {
 *     ....
 *     // Create a ResumableProgressDialog wherever you would create a 
 *     // ProgressDialog then forget that it isn't a standard ProgressDialog.
 *     ProgressDialog d = new ResumableProgressDialog(this);
 *     ....
 *     return d;
 * }
 * </pre>
 * 
 * @author Hal Blackburn http://helios.hud.ac.uk/u0661162
 */
public class ResumableProgressDialog extends ProgressDialog
{
  /** The progress bar hosted by the dialog we're subclassing. */
  private ProgressBar mProgress;
  
  /** See {@link ProgressDialog#ProgressDialog(Context, int)}. */
  public ResumableProgressDialog(Context context, int theme)
  { super(context, theme); }

  /** See {@link ProgressDialog#ProgressDialog(Context)}. */
  public ResumableProgressDialog(Context context)
  { super(context); }
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    // Get a handle on our subclassed ProgressDialog's ProgressBar
    View progressBar = findViewById(R.id.progress);
    if(progressBar instanceof ProgressBar) // this also checks for null
      mProgress = (ProgressBar)progressBar;
  }
  
  // Called whenever the dialog is shown
  @Override public void onStart()
  {
    super.onStart();
    
    if(isIndeterminate() && mProgress != null)
    {
      // Remove the spinner from the layout and then re add it by 
      // setting it's visibility to gone then making it visible again.
      // This makes the progress bar animate again.
      mProgress.setVisibility(View.GONE);
      mProgress.setVisibility(View.VISIBLE);
    }
  }
}
