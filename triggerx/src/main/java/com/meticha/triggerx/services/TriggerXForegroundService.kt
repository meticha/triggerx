package com.meticha.triggerx.services

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.meticha.triggerx.DefaultTriggerActivity
import com.meticha.triggerx.dsl.TriggerX
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.preference.TriggerXPreferences
import com.meticha.triggerx.receivers.TriggerXAlarmReceiver.Companion.ALARM_ACTION
import kotlinx.coroutines.runBlocking
import kotlin.jvm.java

class TriggerXForegroundService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val DEFAULT_CHANNEL_ID = "triggerx_channel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Acquire a wake lock to ensure the device doesn't sleep while processing the alarm
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "triggerx::AlarmWakeLock"
        )
        // Acquire with a timeout, e.g., 1 minute.
        // The wakelock will be released in the finally block regardless of timeout.
        wakeLock.acquire(1 * 60 * 1000L /*1 minute*/)
        LoggerConfig.logger.d("WakeLock acquired")

        try {
            if (intent?.action == ALARM_ACTION) {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)
                val alarmType = intent.getStringExtra("ALARM_TYPE") ?: ""
                val bundle = runBlocking {
                    TriggerX.config?.alarmDataProvider?.provideData(alarmId, alarmType)
                }

                val activityClass = runBlocking { resolveActivityClass(applicationContext) }
                val activityIntent = Intent(this, activityClass).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("ALARM_ID", alarmId)
                    putExtra("ALARM_TYPE", alarmType)
                    putExtra("ALARM_DATA", bundle)
                }
                startActivity(activityIntent)
                LoggerConfig.logger.d("TriggerActivity started")
                stopSelf()
                LoggerConfig.logger.d("stopSelf() called")
            } else {
                LoggerConfig.logger.w("Received intent with unknown or missing action: ${intent?.action}")
                // If the service is started with an unknown action,
                // it should probably stop itself too.
                stopSelf()
                LoggerConfig.logger.d("stopSelf() called due to unknown action")
            }
        } catch (e: Exception) {
            LoggerConfig.logger.e("Error processing alarm", e)
            // Even if there's an error, try to stop the service.
            // The finally block will still run.
            stopSelf()
            LoggerConfig.logger.d("stopSelf() called after exception")
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                LoggerConfig.logger.d("WakeLock released")
            } else {
                LoggerConfig.logger.d("WakeLock was not held at finally block.")
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
        LoggerConfig.logger.d("Service destroyed")
    }

    private fun createNotificationChannel() {
        val channelName = TriggerX.config?.notificationChannelName ?: "TriggerX Alarms"
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for TriggerX alarms"
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setContentTitle(TriggerX.getNotificationTitle())
            .setContentText(TriggerX.getNotificationMessage())
            .setOngoing(true)
            .build()
    }

    suspend fun resolveActivityClass(context: Context): Class<out Activity> {
        return TriggerX.config?.activityClass
            ?: TriggerXPreferences.load(context)?.activityClass
            ?: DefaultTriggerActivity::class.java
    }
}
