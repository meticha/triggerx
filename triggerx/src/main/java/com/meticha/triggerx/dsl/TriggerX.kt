package com.meticha.triggerx.dsl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.meticha.triggerx.logger.DefaultTriggerXLogger
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.preference.TriggerXPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TriggerX {

    // Holds the global configuration for TriggerX. This is set during initialization.
    internal var config: TriggerXConfig? = null

    /**
     * Initializes the TriggerX library with a custom configuration.
     *
     * @param context The application context
     * @param configure Lambda to configure the TriggerXConfig instance
     */
    fun init(context: Context, configure: TriggerXConfig.() -> Unit) {
        val conf = TriggerXConfig().apply(configure)
        config = conf

        LoggerConfig.logger = conf.customLogger ?: DefaultTriggerXLogger
        LoggerConfig.logger.d("TriggerX initialized with notification title: ${conf.notificationTitle}")

        CoroutineScope(Dispatchers.IO).launch {
            TriggerXPreferences.save(context.applicationContext, conf)
        }

        setupNotificationChannel(context.applicationContext)
    }

    /**
     * Sets up the notification channel required for foreground service notifications.
     * This is required on Android 8.0+ (API 26+).
     *
     * @param context The application context
     */
    private fun setupNotificationChannel(context: Context) {
        val channelName = config?.notificationChannelName ?: "TriggerX Alarms"
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for TriggerX alarms"
        }
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    const val DEFAULT_CHANNEL_ID = "triggerx_channel"

    internal fun getNotificationTitle(): String = config?.notificationTitle ?: "Alarm"
    internal fun getNotificationMessage(): String =
        config?.notificationMessage ?: "Alarm is ringing"
}
