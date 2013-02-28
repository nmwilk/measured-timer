package com.measuredsoftware.android.library2.game;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.measuredsoftware.android.library2.utils.FixedSizeIntArray;

public abstract class SoundHandler {
  protected final static int DEFAULT_ONEOFF_SOUNDS_LIST_SIZE = 20;
  
  protected boolean mSoundsLoaded;

  protected int[]         mOneoffStreamIds;
  protected SoundEffect[] mOneoffSoundEffects;
  protected SoundEffect[] mContSoundEffects;
  protected final int mContSoundsCount;
  protected final int mOneoffSoundsCount;
  
  protected SoundPool mSoundPool;
  protected AudioManager mAudioManager;
  
  protected boolean mSoundOnOff;
  
  protected boolean mSoundsPaused;
  
  private FixedSizeIntArray mOneOffEventList;

  public SoundHandler(SoundEffect[] contSoundEffects, SoundEffect[] oneoffSoundEffects) {
    mContSoundEffects = contSoundEffects;
    if (contSoundEffects != null)
      mContSoundsCount = contSoundEffects.length;
    else
      mContSoundsCount = 0;

    mOneoffSoundEffects = oneoffSoundEffects;
    if (oneoffSoundEffects != null) {
      mOneoffSoundsCount = oneoffSoundEffects.length;
      mOneoffStreamIds   = new int[oneoffSoundEffects.length];
    } else { 
      mOneoffSoundsCount = 0;
    }
    
    mSoundsLoaded = false;
    mOneOffEventList = new FixedSizeIntArray(DEFAULT_ONEOFF_SOUNDS_LIST_SIZE);
    
    mSoundOnOff = true;
  }
  
  public int getSoundsCount() {
    return (mContSoundsCount+mOneoffSoundsCount);
  }
  
  public void setSound(AudioManager audioManager, SoundPool soundPool) {
    mSoundPool = soundPool;
    mAudioManager = audioManager;
  }
  
  public void switchOnOff(boolean onOff) {
    mSoundOnOff = onOff;
  }

  public boolean soundIsOn() {
    return mSoundOnOff;
  }
  
  public void loadSounds() {
    if (mSoundsLoaded || (mOneOffEventList == null && mContSoundEffects == null) || mSoundPool == null || mAudioManager == null)
      return;
    
    for(int i=0; i < mOneoffSoundsCount; i++) {
      final SoundEffect soundEffect = mOneoffSoundEffects[i]; 
      soundEffect.mSoundId = mSoundPool.load( soundEffect.mAssetFile, soundEffect.mPriority );
    }
    
    for(int i=0; i < mContSoundsCount; i++) {
      final SoundEffect soundEffect = mContSoundEffects[i]; 
      soundEffect.mSoundId = mSoundPool.load( soundEffect.mAssetFile, soundEffect.mPriority );
      
      // start all continuous sounds at vol 0
      //soundEffect.mStreamId = mSoundPool.play(soundEffect.mSoundId, 0.0f, 0.0f, 1, -1, soundEffect.mRate);
      //Log.d("testgame", "played " + soundEffect.mSoundId + ", got stream id " + soundEffect.mStreamId);
    }

    mSoundsLoaded = true;
  }
  
  public abstract void resetSounds();
  
  public void queueSound(int id) {
    if (!mSoundOnOff)
      return;
    
    mOneOffEventList.set(id);
  }

  public void adjustRateSpont(int id, float rate) {
    if (!mSoundOnOff)
      return;

    if (mOneoffSoundEffects == null || id >= mOneoffSoundsCount)
      return;

    synchronized(mOneoffSoundEffects[id]) {
      if (mOneoffSoundEffects[id].mRate == rate)
        return;
      
      mOneoffSoundEffects[id].mRate = rate;
      mOneoffSoundEffects[id].mRateAdjusted = true;
    }    
  }
  
  public void adjustRate(int id, float rate) {
    if (!mSoundOnOff)
      return;

    if (mContSoundEffects == null || id >= mContSoundsCount)
      return;

    synchronized(mContSoundEffects[id]) {
      if (mContSoundEffects[id].mRate == rate)
        return;
      
      mContSoundEffects[id].mRate = rate;
      mContSoundEffects[id].mRateAdjusted = true;
    }
  }
  
  public void adjustVolSpont(int id, float vol) {
    if (!mSoundOnOff)
      return;

    if (mOneoffSoundEffects == null || id >= mOneoffSoundsCount)
      return;    

    synchronized(mOneoffSoundEffects[id]) {
      if (mOneoffSoundEffects[id].mVolume == vol)
        return;
      
      mOneoffSoundEffects[id].mVolAdjusted = true;
      mOneoffSoundEffects[id].mVolume = vol;
    }
  }
  
  public void adjustVol(int id, float vol) {
    if (!mSoundOnOff)
      return;

    if (mContSoundEffects == null || id >= mContSoundsCount)
      return;    

    synchronized(mContSoundEffects[id]) {
      if (mContSoundEffects[id].mVolume == vol)
        return;
      
      mContSoundEffects[id].mVolAdjusted = true;
      mContSoundEffects[id].mVolume = vol;
    }
  }
  
  public void stopSound(int id) {
    if (!mSoundOnOff)
      return;
    
    if (mOneoffStreamIds == null)
      return;
    
    if (mOneoffStreamIds[id] != 0) {
      //Log.d("whs","stopping streamid "+ mOneoffStreamIds[id]);
      mSoundPool.stop(mOneoffStreamIds[id]);
      mOneoffStreamIds[id] = 0;
    }
  }
  
  public void doSounds() {
    if (!mSoundOnOff)
      return;
    
    if (mSoundsPaused) {
      mSoundsPaused = false;
      mSoundPool.autoResume();
    }
    
    if (mOneoffStreamIds != null) {
      int id = mOneOffEventList.getNext();
      while(id != -1) {
        SoundEffect sound = mOneoffSoundEffects[id];
        // play the sound
        mOneoffStreamIds[id] = mSoundPool.play(sound.mSoundId, sound.mVolume, sound.mVolume, 1, (sound.mLoops) ? -1 : 0, sound.mRate);
        if (mOneoffStreamIds[id] != 0) {
          //Log.d("whs","sound "+ id + " started, streamid " + mOneoffStreamIds[id]);
          sound.mStreamId = mOneoffStreamIds[id];
        }
        id = mOneOffEventList.getNext();
      }
    }
    
    if (mContSoundEffects == null)
      return;
    
    // now loop through continuous sounds
    for(int i=0; i < mContSoundEffects.length; i++) {
      final SoundEffect sound = mContSoundEffects[i];
      synchronized(sound) {
        if (!sound.mStarted) {
          sound.mStreamId = mSoundPool.play(sound.mSoundId, sound.mVolume, sound.mVolume, i+1, -1, sound.mRate);
          if (sound.mStreamId != 0) {
            sound.mStarted = true;
          }
        }         
        
        if (sound.mOnlyDoEvery == 0 || sound.mOnlyDoEvery == ++(sound.mDoneCounter)) {
          // reset the counter
          sound.mDoneCounter = 0;
          
          if(sound.mRateAdjusted && sound.mStarted) {
            mSoundPool.setRate(sound.mStreamId, sound.mRate);
            sound.mRateAdjusted = false;
          }
                  
          float vol = sound.mVolume;
          final float fade = sound.mFadeAmount;
          if (sound.mVolAdjusted) {
            if (sound.mStarted) {
              mSoundPool.setVolume(sound.mStreamId, vol, vol);
            }
            sound.mVolAdjusted = false;
          } else if (vol > fade) { // reduce if playing
            sound.mVolume = (vol -= fade);
            mSoundPool.setVolume(sound.mStreamId, vol, vol);
          } else if (sound.mVolume > 0.0f) {
            sound.mVolume = 0.0f;
            vol = 0.0f;
            mSoundPool.setVolume(sound.mStreamId, vol, vol);
          }
        }
      }
    }
  }
  
  public void onPause() {
    pauseSounds();
  }
  
  public void pauseSounds() {
    if (!mSoundOnOff)
      return;

    if (Build.VERSION.SDK_INT < 8) { 
      stopSounds();
      return;
      //////
    }
    
    mSoundsPaused = true;
    mSoundPool.autoPause();
  }
  
  private void stopSounds() {
    if (!mSoundOnOff)
      return;
    
    mSoundsPaused = false;

    if (mContSoundEffects != null) {
      // stop all continuous sounds
      for(int i=0; i < mContSoundEffects.length; i++) {
        SoundEffect sound = mContSoundEffects[i];
        sound.mVolume = 0;
        sound.mVolAdjusted = true;
        if (sound.mStreamId != 0) {
          mSoundPool.stop(sound.mStreamId);
          sound.mStreamId = 0;
          sound.mStarted = false;
          sound.mDoneCounter = 0;
        }
      }
    }  
    
    // stop all spontaneous sounds (some may be long or loop)
    if (this.mOneoffSoundEffects == null)
      return;
    
    for(int i=0; i < this.mOneoffSoundsCount; i++) {
      stopSound(i);
      SoundEffect sound = this.mOneoffSoundEffects[i];
      if (sound.mStreamId != 0) {
        sound.mStreamId = 0;
        sound.mStarted = false;
      }
    }
  }
  
  public void shutdown() {
    stopSounds();
    
    if (mSoundPool != null)
      mSoundPool.release();
  }
}
