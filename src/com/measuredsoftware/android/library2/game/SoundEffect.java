package com.measuredsoftware.android.library2.game;

import android.content.res.AssetFileDescriptor;

public class SoundEffect {
  
  public final static int TYPE_CONTINUOUS = 1;
  public final static int TYPE_ONEOFF     = 2;
  public final static int TYPE_ONEOFFLOOP = 3;

  protected final int mType;
  public int mId;
  public final int mPriority;
  public float mVolume;
  public float mRate;
  public float mFadeAmount;
  public int mOnlyDoEvery;  // used for efficiency so that it isn't processed every loop.
  public int mDoneCounter;  // used to count when it was last processed.
  public boolean mRateAdjusted;
  public boolean mVolAdjusted;
  public AssetFileDescriptor mAssetFile;
  public int mSoundId;        // assigned when loading the sound
  public int mStreamId;       // assigned when playing the sound
  public boolean mStarted;
  public boolean mLoops;
  
  public static class Builder {
    private final int mType;
    private float mVolume;
    private AssetFileDescriptor mAssetFile;

    private float mFadeAmount = 0.0f;
    private int mOnlyDoEvery = 0;
    private float mRate = 1.0f;

    private boolean mLoops;
    private int     mPriority = 1;

    public Builder(AssetFileDescriptor assetFile, int type, float vol) {
      this.mAssetFile = assetFile;
      this.mType = type;
      this.mVolume = vol;
    }
    
    public Builder fadeAmount(float fadeAmount) {
      mFadeAmount = fadeAmount;
      return this;
    }
    
    public Builder onlyDoEvery(int times) {
      mOnlyDoEvery = times;
      return this;
    }
    
    public Builder loops(boolean b) {
      this.mLoops = b;
      return this;
    }
    
    public Builder rate(float rate) {
      mRate = rate;
      return this;
    }
    
    public Builder priority(int p) {
      mPriority = p;
      return this;
    }

    public SoundEffect build() {
      return new SoundEffect(this);
    }
  }
   
  private SoundEffect(Builder builder) {
    mAssetFile = builder.mAssetFile;
    mType = builder.mType;
    mVolume = builder.mVolume;
    mRate = builder.mRate;
    mFadeAmount = builder.mFadeAmount;
    mOnlyDoEvery = builder.mOnlyDoEvery;
    mDoneCounter = 0;
    mRateAdjusted = false;
    mVolAdjusted = false;
    mStarted = false;
    mLoops = builder.mLoops;
    mPriority = builder.mPriority;

    mSoundId = 0;
    mStreamId = -1;
  }
}
