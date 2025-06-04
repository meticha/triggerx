package com.meticha.triggerx

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.receivers.TriggerXAlarmReceiver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriggerXAlarmScheduler @Inject constructor() {
    companion object {
        const val ALARM_REQUEST_CODE = 1001
    }

    fun scheduleAlarm(context: Context, triggerAtMillis: Long): Boolean {
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
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Use setExactAndAllowWhileIdle for alarms that need to fire even in Doze mode.
            // For AlarmClock behavior (shows in status bar), use AlarmManager.AlarmClockInfo
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            LoggerConfig.logger.i("Alarm scheduled for $triggerAtMillis with action: ${intent.action}")
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

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TriggerXAlarmReceiver::class.java).apply {
            action = TriggerXAlarmReceiver.ALARM_ACTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            LoggerConfig.logger.i("Alarm cancelled for action: ${intent.action}")
        } else {
            LoggerConfig.logger.i("Alarm not found to cancel for action: ${intent.action}")
        }
    }
}