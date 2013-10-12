package com.measuredsoftware.android.timer.data;

import com.measuredsoftware.android.timer.Globals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        /**
         * the system time the alarm ends. Not final because we allow it to be
         * changed if a timer is cancelled.
         */
        public long ms;
        /** the uid (device unique) id of the alarm */
        public final int uid;

        public Alarm(final long time, final int uid)
        {
            this.ms = time;
            this.uid = uid;
        }

        public Alarm(final String string)
        {
            final String[] items = string.split("=");
            if (items == null)
            {
                this.ms = 0;
                this.uid = 0;
            }
            else
            {
                this.uid = Integer.valueOf(items[0]);
                this.ms = Long.valueOf(items[1]);
            }
        }

        @Override
        public String toString()
        {
            return String.format("%d=%d", uid, ms);
        }

        /**
         * @return The formatted countdown time
         */
        public String getCountdownTime()
        {
            return Globals.getFormattedTimeRemaining((this.ms - Globals.getTime()) / 1000);
        }

        /**
         * @return The formatted target time
         */
        public String getTargetTime()
        {
            return Globals.getFormattedTimeEnd((this.ms - Globals.getTime()) / 1000);
        }

        /**
         * @return true if the alarm is expired.
         */
        public boolean expired()
        {
            return this.ms < Globals.getTime();
        }
    }

    private final static String TAG = "EndTimes";

    private final List<Alarm> endTimes = new ArrayList<Alarm>();

    public void addEndTime(final long time, final int id)
    {
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
        }
        else
        {
            alarm = null;
        }

        return alarm;
    }

    /**
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
            sb.append(endTime.toString()).append(",");
        }

        if (sb.length() >= 2)
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
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
                sb.append(endTime.toString()).append(",");
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
        for (final Alarm expiredAlarm : expiredAlarms)
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
    }

    /**
     * Returns a new collection of expired alarms. Does not remove them from its
     * list however.
     * 
     * @return New collection instance.
     */
    public Collection<Alarm> getExpiredAlarms()
    {
        final List<Alarm> expired = new ArrayList<EndTimes.Alarm>();

        for (final Alarm alarm : endTimes)
        {
            if (alarm.expired())
            {
                expired.add(alarm);
            }
        }

        return expired;
    }

    /**
     * Deletes an alarm from the list.
     * 
     * @param alarm
     *            The instance to remove.
     */
    public void removeAlarm(final Alarm alarm)
    {
        endTimes.remove(alarm);
    }
}
