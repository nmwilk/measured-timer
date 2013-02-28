package com.measuredsoftware.android.timer;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

/**
 * @author neil
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Alarms.ALARM_KILLED.equals(intent.getAction())) return;

        AlarmAlertWakeLock.acquireCpuWakeLock(context);

        final Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);

        boolean asleep = false;
        final KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode())
        {
            asleep = true;
        }

        final Intent alarmAlert = new Intent(context, TimerActivity.class);
        alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION
                | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        alarmAlert.putExtra(TimerActivity.INTENT_VAR_ALARM_RINGING, true);
        alarmAlert.putExtra(TimerActivity.INTENT_VAR_DEVICE_ASLEEP, asleep);
        context.startActivity(alarmAlert);

        final Intent playAlarm = new Intent(Alarms.ALARM_ALERT_ACTION);
        context.startService(playAlarm);
    }
}
