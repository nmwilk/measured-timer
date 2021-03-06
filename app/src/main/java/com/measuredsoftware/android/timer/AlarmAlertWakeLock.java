package com.measuredsoftware.android.timer;

import android.content.Context;
import android.os.PowerManager;

/**
 * @author neil
 */
public class AlarmAlertWakeLock
{
    private static PowerManager.WakeLock mCpuWakeLock;

    static void acquireCpuWakeLock(Context context)
    {
        if (mCpuWakeLock != null) return;

        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "");
        mCpuWakeLock.acquire();
    }

    static void releaseCpuLock()
    {
        if (mCpuWakeLock != null)
        {
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}
