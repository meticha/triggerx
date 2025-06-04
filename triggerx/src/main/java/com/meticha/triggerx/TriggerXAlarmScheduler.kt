package com.meticha.triggerx

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.receivers.TriggerXAlarmReceiver

class TriggerXAlarmScheduler {
    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        type: String,
        alarmId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    ): Boolean {
        val alarmManager = context.getSystemService<AlarmManager>()!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                LoggerConfig.logger.e("Cannot schedule exact alarms. Permission not granted.")
                // Optionally, redirect to settings or notify user.
                // For now, returning false as the HomeScreen should handle permission request.
                return false
            }
        }

        val intent = Intent(context, TriggerXAlarmReceiver::class.java).apply {
            action = TriggerXAlarmReceiver.ALARM_ACTION
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_TYPE", type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ alarmId,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            // Use setExactAndAllowWhileIdle for alarms that need to fire even in Doze mode.
            // For AlarmClock behavior (shows in status bar), use AlarmManager.AlarmClockInfo
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            LoggerConfig.logger.i("Alarm [$alarmId] scheduled for $triggerAtMillis")
            return true
        } catch (se: SecurityException) {
            LoggerConfig.logger.e(
                "SecurityException while scheduling alarm. Check permissions.",
                se
            )
            return false
        } catch (e: Exception) {
            LoggerConfig.logger.e("Exception while scheduling alarm", e)
            return false
        }
    }

    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        alarmId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    ) = scheduleAlarm(context, triggerAtMillis, "", alarmId)


    fun scheduleMultipleAlarms(
        context: Context,
        events: List<Pair<Int, Long>> // Pair<alarmId, triggerTime>
    ): List<Boolean> {
        return events.map { (id, time) ->
            scheduleAlarm(context, time, "", id)
        }
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        val intent = Intent(context, TriggerXAlarmReceiver::class.java).apply {
            action = TriggerXAlarmReceiver.ALARM_ACTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            LoggerConfig.logger.i("Alarm [$alarmId] cancelled.")
        } else {
            LoggerConfig.logger.i("Alarm [$alarmId] not found to cancel.")
        }
    }
}