package com.measuredsoftware.android.timer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpDialog extends Dialog implements OnClickListener {

  ImageView mFinger;
  
  public HelpDialog(Context context) {
    super(context);
  }
  
  @Override
  public void onCreate(Bundle savedInstance)
  {
    requestWindowFeature(Window.FEATURE_NO_TITLE); 

    setContentView(R.layout.help_dialog);
    
    ((TextView)findViewById(R.id.okButton)).setOnClickListener(this);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      dismiss();
      return true;
    }
    
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public void onClick(View v) {
    dismiss();
  }
}
