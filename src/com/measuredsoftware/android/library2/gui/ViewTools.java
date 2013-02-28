package com.measuredsoftware.android.library2.gui;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ViewTools {
  
  public static void showViewGroup(View v) {
    toggleShowView(v, View.VISIBLE);
  }
  
  public static void hideViewGroup(View v) {
    toggleShowView(v, View.INVISIBLE);
  }
  
  private static void toggleShowView(View v, int visibility) {
    v.setVisibility(visibility);
  }
  
  public static void enableView(View v) {
    toggleEnableView(v, true);
  }
  
  public static void disableViewGroup(View v) {
    toggleEnableView(v, false);
  }
  
  private static void toggleEnableView(View v, boolean enabled) {
    v.setEnabled(enabled);
  } 
  
  public static void showView(View v) {
    toggleView(v, View.VISIBLE);
  }
  
  public static void hideView(View v) {
    toggleView(v, View.INVISIBLE);
  }
  
  private static void toggleView(View v, int visibility) {
    v.setVisibility(visibility);
  }
  
  public static Toast showToast(Context context, String msg, int nDuration) {
    Toast t = Toast.makeText(context, msg, nDuration);
    t.show();
    return t;
  }
}
