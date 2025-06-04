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
    internal var config: TriggerXConfig? = null

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

    fun getNotificationTitle(): String = config?.notificationTitle ?: "Alarm"
    fun getNotificationMessage(): String = config?.notificationMessage ?: "Alarm is ringing"
}
