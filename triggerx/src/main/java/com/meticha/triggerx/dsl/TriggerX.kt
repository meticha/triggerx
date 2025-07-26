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

    /**
     * Returns whether alarm activity should be shown when app is in the foreground.
     * Defaults to true if not configured.
     */
    internal fun showAlarmActivityWhenAppIsActive(): Boolean =
        config?.showAlarmActivityWhenAppIsActive ?: true


    /**
     * Returns whether alarm activity should be shown when device is active/unlocked.
     * Defaults to true if not configured.
     */
    internal fun showAlarmActivityWhenDeviceIsActive(): Boolean =
        config?.shouldShowAlarmActivityWhenDeviceIsActive ?: true

    internal fun getNotificationTitle(): String = config?.notificationTitle ?: "Alarm"
    internal fun getNotificationMessage(): String =
        config?.notificationMessage ?: "Alarm is ringing"
}
