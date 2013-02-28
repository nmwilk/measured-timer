package com.measuredsoftware.android.timer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TimerPrefs extends PreferenceActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
  }
}
