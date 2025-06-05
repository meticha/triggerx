package com.meticha.triggerx.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.services.TriggerXForegroundService

/**
 * A [BroadcastReceiver] that listens for scheduled alarms and initiates the foreground service
 * to handle the alarm event.
 *
 * This receiver is triggered by the [android.app.AlarmManager] when an exact alarm,
 * scheduled by the application, fires. Its primary responsibility is to start the
 * [TriggerXForegroundService], passing along any relevant alarm details.
 */
internal class TriggerXAlarmReceiver : BroadcastReceiver() {

    /**
     * Companion object for [TriggerXAlarmReceiver].
     */
    companion object {
        /**
         * The specific action string that this receiver listens for.
         * Alarms should be scheduled with an Intent using this action.
         */
        const val ALARM_ACTION = "com.meticha.triggerx.ALARM_ACTION"
    }

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * This method checks if the received Intent matches the [ALARM_ACTION].
     * If it matches, it extracts alarm details (ID and type) from the Intent's extras
     * and starts the [TriggerXForegroundService], passing these details to the service.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received. This should contain the [ALARM_ACTION]
     *               and extras for "ALARM_ID" and "ALARM_TYPE".
     */
    override fun onReceive(context: Context, intent: Intent?) {
        LoggerConfig.logger.d("Alarm received!")

        if (intent?.action == ALARM_ACTION) {
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            val alarmType = intent.getStringExtra("ALARM_TYPE") ?: ""

            val serviceIntent = Intent(context, TriggerXForegroundService::class.java).also {
                it.action = ALARM_ACTION // Set action for the service intent as well
                it.putExtra("ALARM_ID", alarmId)
                it.putExtra("ALARM_TYPE", alarmType)
            }
            context.startForegroundService(serviceIntent)
        } else {
            LoggerConfig.logger.w("Received intent with unknown action: ${intent?.action}")
        }
    }
}
