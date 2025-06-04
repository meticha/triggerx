package com.meticha.triggerx.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.meticha.triggerx.TriggerActivity
import com.meticha.triggerx.receivers.TriggerXAlarmReceiver.Companion.ALARM_ACTION

class TriggerXForegroundService : Service() {

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        private const val TAG = "TriggerX Foreground Service"
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Acquire a wake lock to ensure the device doesn't sleep while processing the alarm
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "triggerx::AlarmWakeLock"
        )
        // Acquire with a timeout, e.g., 1 minute.
        // The wakelock will be released in the finally block regardless of timeout.
        wakeLock.acquire(1 * 60 * 1000L /*1 minute*/)
        Log.d(TAG, "WakeLock acquired")

        try {
            if (intent?.action == ALARM_ACTION) {
                val activityIntent = Intent(this, TriggerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(activityIntent)
                Log.d(TAG, "TriggerActivity started")
                stopSelf()
                Log.d(TAG, "stopSelf() called")
            } else {
                Log.w(TAG, "Received intent with unknown or missing action: ${intent?.action}")
                // If the service is started with an unknown action,
                // it should probably stop itself too.
                stopSelf()
                Log.d(TAG, "stopSelf() called due to unknown action")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing alarm", e)
            // Even if there's an error, try to stop the service.
            // The finally block will still run.
            stopSelf()
            Log.d(TAG, "stopSelf() called after exception")
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.d(TAG, "WakeLock released")
            } else {
                Log.d(TAG, "WakeLock was not held at finally block.")
            }
        }
        /**
         * START_NOT_STICKY: If the service is killed by the system,
         * it will not be automatically restarted.
         */
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}
