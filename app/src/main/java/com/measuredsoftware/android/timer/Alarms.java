package com.measuredsoftware.android.timer;

import java.util.Calendar;
import java.util.Collection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.measuredsoftware.android.timer.data.EndTimes.Alarm;

/**
 * Various alarm tools.
 * 
 * @author neil
 *
 */
public class Alarms
{
    /** */
    public static final String ALARM_ALERT_ACTION = "com.measuredsoftware.android.timer.ALARM_ALERT";

    /** */
    public static final String ALARM_DONE_ACTION = "com.measuredsoftware.android.timer.ALARM_DONE";

    /** */
    public static final String ALARM_DISMISS_ACTION = "com.measuredsoftware.android.timer.ALARM_DISMISS";

    /** */
    public static final String ALARM_KILLED = "alarm_killed";

    /** */
    public static final String ALARM_KILLED_TIMEOUT = "alarm_killed_timeout";

    /** */
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";

    /** */
    public static final String ALARM_ID = "alarm_id";

    /**
     * @param context
     * @param atTimeInMillis
     * @param alarmUid 
     */
    public static void enableAlert(Context context, final long atTimeInMillis, final int alarmUid)
    {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(ALARM_ALERT_ACTION);

        final PendingIntent sender = PendingIntent.getBroadcast(context, alarmUid, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(atTimeInMillis);
    }

    /**
     * @param context
     * @param alarmUid
     */
    public static void disableAlert(final Context context, final int alarmUid)
    {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent sender = PendingIntent.getBroadcast(context, alarmUid, new Intent(ALARM_ALERT_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }
    
    /**
     * @param context
     * @param endTimes Collection of expired alarms.
     */
    public static void disableExpiredAlerts(final Context context, final Collection<Alarm> endTimes)
    {
        for(final Alarm alarm : endTimes)
        {
            disableAlert(context, alarm.uid);
        }
    }
}
