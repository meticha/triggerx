/*
 * Designed and developed by MetichaHQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meticha.triggerx

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.receivers.TriggerXAlarmReceiver

/**
 * Manages the scheduling and cancellation of alarms using Android's [AlarmManager].
 * This class provides methods to schedule single or multiple alarms and to cancel existing ones.
 */
class TriggerXAlarmScheduler {
    /**
     * Schedules an exact alarm to be triggered at a specific time.
     *
     * This function uses [AlarmManager.setExactAndAllowWhileIdle] to ensure the alarm fires
     * even when the device is in Doze mode. It requires the `SCHEDULE_EXACT_ALARM` permission
     * on Android S (API 31) and above.
     *
     * @param context The application context.
     * @param triggerAtMillis The time in milliseconds at which the alarm should trigger.
     * @param type A string classifying the type of alarm, passed to the receiver.
     * @param alarmId A unique integer identifier for the alarm. Defaults to a value derived
     *                from the current system time.
     * @return `true` if the alarm was successfully scheduled, `false` otherwise (e.g., if
     *         permission is denied or an exception occurs).
     */
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

    /**
     * Schedules an exact alarm with a default empty type string.
     *
     * @param context The application context.
     * @param triggerAtMillis The time in milliseconds at which the alarm should trigger.
     * @param alarmId A unique integer identifier for the alarm. Defaults to a value derived
     *                from the current system time.
     * @return `true` if the alarm was successfully scheduled, `false` otherwise.
     * @see scheduleAlarm
     */
    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        alarmId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    ) = scheduleAlarm(context, triggerAtMillis, "", alarmId)


    /**
     * Schedules multiple alarms based on a list of events.
     * Each event is a pair of alarm ID and trigger time.
     *
     * @param context The application context.
     * @param events A list of [Pair]s, where each pair contains an `Int` (alarmId)
     *               and a `Long` (triggerTime in milliseconds).
     * @return A list of [Boolean] values, where each boolean indicates whether the
     *         corresponding alarm in the `events` list was successfully scheduled.
     */
    fun scheduleMultipleAlarms(
        context: Context,
        events: List<Pair<Int, Long>> // Pair<alarmId, triggerTime>
    ): List<Boolean> {
        return scheduleMultipleAlarms(context, "", events)
    }

    /**
     * Schedules multiple alarms based on a list of events.
     * Each event is a pair of alarm ID and trigger time.
     *
     * @param context The application context.
     * @param type A string classifying the type of alarm, passed to the receiver.
     * @param events A list of [Pair]s, where each pair contains an `Int` (alarmId)
     *               and a `Long` (triggerTime in milliseconds).
     * @return A list of [Boolean] values, where each boolean indicates whether the
     *         corresponding alarm in the `events` list was successfully scheduled.
     */
    fun scheduleMultipleAlarms(
        context: Context,
        type: String,
        events: List<Pair<Int, Long>> // Pair<alarmId, triggerTime>
    ): List<Boolean> {
        return events.map { (id, time) ->
            scheduleAlarm(context, time, type, id)
        }
    }

    /**
     * Schedules multiple alarms based on a list of events and returns a list of successfully scheduled alarm IDs.
     * Each event is a pair of alarm ID and trigger time. This function uses a default empty type string for alarms.
     *
     * @param context The application context.
     * @param events A list of [Pair]s, where each pair contains an `Int` (alarmId)
     *               and a `Long` (triggerTime in milliseconds).
     * @return A list of [Int] values, where each integer is an ID of an alarm that was
     *         successfully scheduled.
     */
    fun scheduleAlarms(
        context: Context,
        events: List<Pair<Int, Long>> // Pair<alarmId, triggerTime>
    ): List<Int> {
        val scheduledIds = mutableListOf<Int>()
        // Call the existing overload that takes a type and returns List<Boolean>
        val results = scheduleMultipleAlarms(context, events)
        events.forEachIndexed { index, event ->
            // If the result at this index is true, the alarm was scheduled
            if (results.getOrElse(index) { false }) {
                scheduledIds.add(event.first) // event.first is the alarmId
            }
        }
        return scheduledIds
    }

    /**
     * Cancels a previously scheduled alarm.
     *
     * If an alarm with the given `alarmId` exists, it will be cancelled.
     * If no such alarm is found, this method does nothing.
     *
     * @param context The application context.
     * @param alarmId The unique integer identifier of the alarm to cancel.
     */
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