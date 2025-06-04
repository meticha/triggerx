package com.meticha.triggerx.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.services.TriggerXForegroundService

class TriggerXAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_ACTION = "com.meticha.triggerx.ALARM_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        LoggerConfig.logger.d("Alarm received!")

        if (intent?.action == ALARM_ACTION) {
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            val alarmType = intent.getStringExtra("ALARM_TYPE") ?: ""

            val serviceIntent = Intent(context, TriggerXForegroundService::class.java).also {
                it.action = ALARM_ACTION
                it.putExtra("ALARM_ID", alarmId)
                it.putExtra("ALARM_TYPE", alarmType)
            }
            context.startForegroundService(serviceIntent)
        } else {
            LoggerConfig.logger.w("Received intent with unknown action: ${intent?.action}")
        }
    }


}