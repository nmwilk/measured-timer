package com.measuredsoftware.android.timer.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author neil
 */
public class EndTimes
{
    private final static String TAG = "EndTimes";
    
    private final List<Long> endTimes = new ArrayList<Long>();

    /**
     * @param time
     */
    public void addEndTime(final long time)
    {
        final Long endTime = Long.valueOf(time);
        Log.d(TAG, "addEndTime " + endTime);
        endTimes.add(endTime);
    }

    /** */
    public void removeLast()
    {
        if (endTimes.size() > 0)
        {
            endTimes.remove(endTimes.size() - 1);
            Log.d(TAG, "removeLast");
        }
    }

    /**
     * @param index
     * @return End time at the index.
     */
    public long getTime(final int index)
    {
        return endTimes.get(index);
    }

    /**
     * @return true if there's active timers.
     */
    public boolean timersActive()
    {
        return endTimes.size() > 0;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        for (final Long endTime : endTimes)
        {
            sb.append(endTime.toString() + ",");
        }

        if (sb.length() >= 2)
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        final String string = sb.toString();
        Log.d(TAG, "toString " + string);

        return string;
    }

    /**
     * @param string A CSV of longs.
     */
    public void load(final String string)
    {
        endTimes.clear();
        final String[] values = string.split(",");
        for(int i=0; i < values.length; i++)
        {
            if (values[i].length() > 0)
            {
                endTimes.add(Long.parseLong(values[i]));
            }
        }
        
        Log.d(TAG, "load. " + toString());
    }
}
