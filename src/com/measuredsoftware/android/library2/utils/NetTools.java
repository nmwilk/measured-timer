package com.measuredsoftware.android.library2.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetTools {

  public static boolean isConnectivity(Context context) {
    
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni == null)
      return false;
    
    return (ni.getState() == NetworkInfo.State.CONNECTED); 
  }
  
  public static NetworkInfo.State getConnectivityState(Context context) {
    NetworkInfo.State state = NetworkInfo.State.UNKNOWN;
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null) {
      state = ni.getState();
    }
    
    return state;
  }
  
  public static NetworkInfo.DetailedState getConnectivityStateDetailed(Context context) {
    NetworkInfo.DetailedState state = NetworkInfo.DetailedState.IDLE;
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null) {
      state = ni.getDetailedState();
    }
    
    return state;
  }
  
  public static boolean weAreOnline(Context context) {
    return (NetTools.getConnectivityState(context) == NetworkInfo.State.CONNECTED);
  }
}
