package com.meticha.triggerx.receivers

import android.R.attr.action
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.meticha.triggerx.TriggerActivity
import com.meticha.triggerx.services.TriggerXForegroundService
import kotlin.jvm.java

class TriggerXAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_ACTION = "com.meticha.triggerx.ALARM_ACTION"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Alarm received!")

        if (intent?.action == ALARM_ACTION) {
            val serviceIntent = Intent(context, TriggerXForegroundService::class.java).also {
                it.action = ALARM_ACTION
            }
            context.startForegroundService(serviceIntent)
        } else {
            Log.w(TAG, "Received intent with unknown action: ${intent?.action}")
        }
    }


}