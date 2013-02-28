package com.measuredsoftware.android.library2.game;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;

public abstract class GameActivity extends Activity {
  private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();  
  
  public static final int RESULT_CODE_PLAYER_QUIT = 1;
  
  // result
  protected int mResult;

  // paused
  protected boolean mPaused;
  private long mBackPressed;
  
  // state
  protected boolean mGameLoaded;
  
  // graphics
  protected int mViewportHeight;
  protected int mViewportWidth;
  protected float mViewportDensity;

  // sound
  protected SoundPool     mSoundPool;
  protected AudioManager  mAudioManager;
  protected SoundHandler  mSoundHandler;
  
  protected BaseGameLogic mGameLogic;
  protected BaseGameState mGameState;
  protected BaseGLRenderer mRenderer;
  protected BaseGLSurfaceView mSurfaceView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);

    mViewportWidth = dm.widthPixels;
    mViewportHeight = dm.heightPixels;
    mViewportDensity = dm.density;
  }
  
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mGameLoaded && (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK)) {
      if (mPaused) {
        resume();
        mBackPressed = 0;
      }
      else if (mGameState != null && mGameState.userPlaying()){
        pause();
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mGameState != null && mGameState.userPlaying() && mBackPressed == 0) {
        mBackPressed = SystemClock.uptimeMillis();
        return true;
      } else {
        if (!handleBackButton()) {
          mResult = GameActivity.RESULT_CODE_PLAYER_QUIT;
          doShutdown();
          return true;
        }
      }
    }
    
    return super.onKeyDown(keyCode, event);
  }  
  
  // implemented in derived class to handle the back button when in the menu system (e.g. close dialogs, navigate back to a previous menu etc)
  public abstract boolean handleBackButton();
  
  public void doShutdown() {
    doLevelShutdown(); 

    // set results of game
    Intent results = new Intent();
      
    setResult(mResult, results);
    
    finish();
  }
  
  public void doLevelShutdown() {
    if (mGameLogic != null) {
      mGameLogic.setRun(false);
      mGameLogic.onShutdown();

      while(mGameLogic.getRunning()) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
        }
      }
    }
    
    if (mGameState != null) {
      mGameState.userPlaying(false);
    }

    if (mSoundHandler != null)
      mSoundHandler.shutdown();
  }

  protected void pause() {
    mPaused = true;
    if (mGameState != null) {
      if (mGameLoaded)//mGameState.userPlaying())
        mGameLogic.onPause(); // so that the game loop pauses
    }
    mRenderer.pause();    // so that it knows to stop updating the timer
    mSurfaceView.pause();
  }
  
  protected void resume() {
    mPaused = false;
    if (mGameLogic.mPaused)
      mGameLogic.onResume();
    mRenderer.resume();
    mSurfaceView.resume();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    //Log.d("wh","onPause");
    if (mGameLoaded) {
      pause();
      Runtime.getRuntime().gc();
      mSurfaceView.onPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    //Log.d("wh","onResume");
    if (mGameLoaded) {
      Runtime.getRuntime().gc();
      mSurfaceView.onResume();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    //Log.d("wh","onRestart");
  }

  @Override
  protected void onStart() {
    super.onStart();
    //Log.d("wh","onStart");
  }

  @Override
  protected void onStop() {
    super.onStop();
    //Log.d("wh","onStop");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //Log.d("wh","onDestroy");
  }  
  
  
  // sets the sound stream that the vol button controls
  private void setVolumeControl(boolean soundOn) {
    if (!soundOn)
      setVolumeControlStream(AudioManager.STREAM_RING);
    else
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
  }

  
  //////////////////////////////////////////
  // PROTECTED/PUBLIC
  //////////////////////////////////////////
  
  /*
   * derived classes need to call this
   */
  protected void initSound(boolean soundOn, SoundHandler soundHandler, int soundsCount) {
    setVolumeControl(soundOn);

    mSoundPool = null;
    mAudioManager = null;
    
    if (soundOn) {
      mSoundPool = new SoundPool( soundsCount, AudioManager.STREAM_MUSIC, 0);
      mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }
    mSoundHandler = soundHandler;
    
    mSoundHandler.switchOnOff(soundOn);
    mSoundHandler.setSound(mAudioManager, mSoundPool);
    mSoundHandler.loadSounds();
  }
  
  /** 
   * Loads a bitmap 
   */
  public static Bitmap loadBitmap(Context context, int resourceId) {
    Bitmap bitmap = null;
    // Set our bitmaps to 16-bit, 565 format.
    sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    
    if (context != null) {
      InputStream is = context.getResources().openRawResource(resourceId);
      try {
        bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // Ignore.
        }
      }
    }

    return bitmap;
  }    
}
