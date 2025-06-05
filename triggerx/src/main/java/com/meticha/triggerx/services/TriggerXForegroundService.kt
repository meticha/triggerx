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

/**
 * A [Service] that runs in the foreground to handle alarm events.
 *
 * This service is started by [com.meticha.triggerx.receivers.TriggerXAlarmReceiver] when an alarm fires.
 * It's responsible for:
 * 1. Displaying a foreground notification to comply with Android foreground service requirements.
 * 2. Acquiring a wake lock to ensure the device stays awake during processing.
 * 3. Fetching alarm-specific data using [com.meticha.triggerx.provider.TriggerXDataProvider].
 * 4. Launching the configured target [Activity] with the alarm data.
 * 5. Releasing the wake lock and stopping itself once the work is done.
 */
internal class TriggerXForegroundService : Service() {
    /**
     * Companion object for [TriggerXForegroundService].
     * Contains constants used by the service.
     */
    companion object {
        /**
         * Unique ID for the foreground service notification.
         */
        private const val NOTIFICATION_ID = 1001

        /**
         * Default channel ID for the foreground service notification.
         */
        private const val DEFAULT_CHANNEL_ID = "triggerx_channel"
    }

    /**
     * Returns null as this service is not designed for binding.
     * @param intent The Intent that was used to bind to this service.
     * @return Always returns null.
     */
    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Called by the system when the service is first created.
     * Responsible for creating the notification channel for the foreground service.
     */
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Called by the system every time a client starts the service using [Context.startService].
     * This method handles the core logic of the alarm execution.
     *
     * It performs the following steps:
     * - Builds and displays a foreground notification.
     * - Acquires a partial wake lock.
     * - Processes the incoming alarm intent:
     *     - Extracts alarm ID and type.
     *     - Fetches data using [TriggerX.config.alarmDataProvider].
     *     - Resolves the target activity class.
     *     - Starts the target activity with alarm data.
     * - Stops the service itself.
     * - Releases the wake lock in a finally block.
     *
     * @param intent The Intent supplied to [Context.startService]. This should contain
     *               the alarm action, ID, and type.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return [START_NOT_STICKY] to indicate that if the service is killed, it should not be
     *         automatically restarted.
     */
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

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     * Logs the destruction of the service.
     */
    override fun onDestroy() {
        super.onDestroy()
        LoggerConfig.logger.d("Service destroyed")
    }

    /**
     * Creates the notification channel for the foreground service.
     * Uses the channel name from [TriggerX.config] or a default name.
     */
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

    /**
     * Builds the [Notification] instance for the foreground service.
     * Uses the title and message from [TriggerX.getNotificationTitle] and [TriggerX.getNotificationMessage].
     * @return The configured [Notification] object.
     */
    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setContentTitle(TriggerX.getNotificationTitle())
            .setContentText(TriggerX.getNotificationMessage())
            .setOngoing(true)
            .build()
    }

    /**
     * Resolves the [Activity] class that should be launched when the alarm fires.
     * The class is determined in the following order of preference:
     * 1. [TriggerX.config.activityClass]
     * 2. Class loaded from [TriggerXPreferences]
     * 3. [DefaultTriggerActivity]
     *
     * @param context The application context, used for loading preferences.
     * @return The [Class] of the Activity to be launched.
     */
    suspend fun resolveActivityClass(context: Context): Class<out Activity> {
        return TriggerX.config?.activityClass
            ?: TriggerXPreferences.load(context)?.activityClass
            ?: DefaultTriggerActivity::class.java
    }
}
