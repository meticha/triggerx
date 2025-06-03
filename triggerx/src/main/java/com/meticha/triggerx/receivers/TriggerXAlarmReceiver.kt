package com.meticha.triggerx.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.meticha.triggerx.TriggerActivity
import kotlin.jvm.java

class TriggerXAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_ACTION = "com.meticha.triggerx.ALARM_ACTION"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Alarm received!")

        // Acquire a wake lock to ensure the device doesn't sleep while processing the alarm
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "triggerx::AlarmWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes - adjust as needed*/)

        try {
            if (intent?.action == ALARM_ACTION) {
                Log.d(TAG, "Starting TriggerActivity")
                val activityIntent = Intent(context, TriggerActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(activityIntent)
            } else {
                Log.w(TAG, "Received intent with unknown action: ${intent?.action}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing alarm", e)
        } finally {
            wakeLock.release()
            Log.d(TAG, "WakeLock released")
        }
    }


}