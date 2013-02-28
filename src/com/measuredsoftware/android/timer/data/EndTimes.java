package com.measuredsoftware.android.timer.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.measuredsoftware.android.timer.Globals;

import android.util.Log;

/**
 * @author neil
 */
public class EndTimes
{
    /**
     * @author neil
     */
    public class Alarm
    {
        /** the system time the alarm ends */
        public final long time;
        /** the uid (device unique) id of the alarm */
        public final int uid;

        /**
         * 
         * @param time
         * @param uid
         */
        public Alarm(final long time, final int uid)
        {
            this.time = time;
            this.uid = uid;
        }
        
        /**
         * @param string
         */
        public Alarm(final String string)
        {
            final String[] items = string.split("=");
            if (items == null)
            {
                this.time = 0;
                this.uid = 0;
            }
            else
            {
                this.uid = Integer.valueOf(items[0]);
                this.time = Long.valueOf(items[1]);
            }
        }
        
        @Override
        public String toString()
        {
            return String.format("%d=%d", uid, time);
        }

        /**
         * @return true if the alarm is expired.
         */
        public boolean expired()
        {
            return this.time < Globals.getTime();
        }
    }

    private final static String TAG = "EndTimes";

    private final List<Alarm> endTimes = new ArrayList<Alarm>();

    /**
     * @param time
     * @param id 
     */
    public void addEndTime(final long time, final int id)
    {
        final Long endTime = Long.valueOf(time);
        Log.d(TAG, "addEndTime " + id + ", " + endTime);
        endTimes.add(new Alarm(time, id));
    }

    /**
     * @return The alarm removed. Null if none.
     */
    public Alarm removeLast()
    {
        final Alarm alarm;
        
        if (endTimes.size() > 0)
        {
            alarm = endTimes.remove(endTimes.size() - 1);
            Log.d(TAG, "removeLast: removedLast");
        }
        else
        {
            alarm = null;
            Log.d(TAG, "removeLast: none present");
        }
        
        return alarm;
    }

    /**
     * @param index
     * @return End time at the index.
     */
    public Alarm getTime(final int index)
    {
        return endTimes.get(index);
    }

    /**
     * @return Number of alarms.
     */
    public int count()
    {
        return endTimes.size();
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

        for (final Alarm endTime : endTimes)
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
     * @return Non-expired timers in CSV format (<uid>=<endtime>,...)
     */
    public String getAsSaveString()
    {
        final StringBuilder sb = new StringBuilder();

        for (final Alarm endTime : endTimes)
        {
            if (!endTime.expired())
            {
                sb.append(endTime.toString() + ",");
            }
        }

        if (sb.length() >= 2)
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
    
    /**
     * @return Convenience reference to this instance.
     */
    public EndTimes removeExpiredTimers()
    {
        final Collection<Alarm> expiredAlarms = getExpiredAlarms();
        for(final Alarm expiredAlarm : expiredAlarms)
        {
            endTimes.remove(expiredAlarm);
        }
        
        return this;
    }

    /**
     * @param string
     *            A CSV of longs.
     */
    public void load(final String string)
    {
        endTimes.clear();
        final String[] values = string.split(",");
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].length() > 0)
            {
                final Alarm loadedAlarm = new Alarm(values[i]);
                if (loadedAlarm.expired())
                {
                    continue;
                }
                endTimes.add(loadedAlarm);
            }
        }

        Log.d(TAG, "load. " + toString());
    }
    
    /**
     * Returns a new collection of expired alarms. Does not remove them from its list however.
     * @return New collection instance.
     */
    public Collection<Alarm> getExpiredAlarms()
    {
        final List<Alarm> expired = new ArrayList<EndTimes.Alarm>();
        
        for(final Alarm alarm : endTimes)
        {
            if (alarm.expired())
            {
                expired.add(alarm);
            }
        }
        
        return expired;
    }
}
