package com.measuredsoftware.android.library2.dbg;

import android.os.SystemClock;

import com.measuredsoftware.android.library2.utils.ValueTools;

/** 
 * Implements a simple runtime profiler.  The profiler records start and stop
 * times for several different types of profiles and can then return min, max
 * and average execution times per type.  Profile types are independent and may
 * be nested in calling code. This object is a singleton for convenience.
 */
public class ProfileRecorder {
    // A type for recording actual draw command time.
    public static final int PROFILE_DRAW    = 0;        // Renderer onDrawFrame
    public static final int PROFILE_INPUT   = 1;
    public static final int PROFILE_PHYSICS = 2;
    public static final int PROFILE_FRAME   = 3;       // entire gamelogic tick
    public static final int PROFILE_CAMERA  = 4;
    public static final int PROFILE_STATE   = 5;
    public static final int PROFILE_SOUNDS  = 6;
    private static final int PROFILE_COUNT  = PROFILE_SOUNDS + 1;
    
    private ProfileRecord[] mProfiles;
    private int mCounts[]; // one per profile
    
    public static ProfileRecorder sSingleton = new ProfileRecorder();
    
    public ProfileRecorder() {
        mProfiles = new ProfileRecord[PROFILE_COUNT];
        for (int x = 0; x < PROFILE_COUNT; x++) {
            mProfiles[x] = new ProfileRecord();
        }
        mCounts = new int[PROFILE_COUNT];
    }
    
    /** Starts recording execution time for a specific profile type.*/
    public void start(int profileType) {
        if (profileType < PROFILE_COUNT) {
            mProfiles[profileType].start(SystemClock.uptimeMillis());
        }
    }
    
    /** Stops recording time for this profile type. */
    public void stop(int profileType) {
        if (profileType < PROFILE_COUNT) {
            mProfiles[profileType].stop(SystemClock.uptimeMillis());
            ++mCounts[profileType];
        }
    }
    
    /** Stops recording time for this profile type. with a supplied time */
    public void stop(int profileType, long time) {
        if (profileType < PROFILE_COUNT) {
            mProfiles[profileType].stop(time);
            ++mCounts[profileType];
        }
    }
    
    /* Flushes all recorded timings from the profiler. */
    public void resetAll() {
        for (int x = 0; x < PROFILE_COUNT; x++) {
            mProfiles[x].reset();
            mCounts[x] = 0;
        }
    }
    
    /* Returns the average execution time, in milliseconds, for a given type. */
    public float getAverageTime(int profileType) {
        float time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getAverageTime(mCounts[profileType]);
        }
        return time;
    }
    
    /* Returns the minimum execution time in milliseconds for a given type. */
    public long getMinTime(int profileType) {
        long time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getMinTime();
        }
        return time;
    }
    
    /* Returns the maximum execution time in milliseconds for a given type. */
    public long getMaxTime(int profileType) {
        long time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getMaxTime();
        }
        return time;
    }
    
    /** 
     * A simple class for storing timing information about a single profile
     * type.
     */
    protected class ProfileRecord {
        private long mStartTime;
        private long mTotalTime;
        private long mMinTime;
        private long mMaxTime;
        
        public void start(long time) {
            mStartTime = time;
        }
        
        public void stop(long time) {
            final long timeDelta = time - mStartTime;
            mTotalTime += timeDelta;
            if (mMinTime == 0 || timeDelta < mMinTime) {
                mMinTime = timeDelta;
            }
            if (mMaxTime == 0 || timeDelta > mMaxTime) {
                mMaxTime = timeDelta;
            }
        }
        
        public float getAverageTime(int frameCount) {
            float time = frameCount > 0 ? ValueTools.roundFloatToDecPlaces((mTotalTime / (float)frameCount), 2) : 0;
            return time;
        }
        
        public long getMinTime() {
            return mMinTime;
        }
        
        public long getMaxTime() {
            return mMaxTime;
        }
        
        public void startNewProfilePeriod() {
            mTotalTime = 0;
        }
        
        public void reset() {
            mTotalTime = 0;
            mStartTime = 0;
            mMinTime = 0;
            mMaxTime = 0;
        }
    }
}
