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
package com.meticha.triggerx.preference

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.meticha.triggerx.dsl.TriggerXConfig
import com.meticha.triggerx.logger.LoggerConfig
import kotlinx.coroutines.flow.firstOrNull

/**
 * Manages the persistence of TriggerX configuration settings using Jetpack DataStore.
 * This object is responsible for saving and loading essential configuration details
 * that need to survive app restarts, such as the target activity class and
 * notification content.
 */
internal object TriggerXPreferences {
    /**
     * Provides a [DataStore] instance for storing preferences, named "triggerx_prefs".
     * This is an extension property on [Context].
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "triggerx_prefs")

    /**
     * Preference key for storing the fully qualified class name of the activity to be launched by TriggerX.
     */
    private val KEY_ACTIVITY_CLASS = stringPreferencesKey("activity_class")

    /**
     * Preference key for storing the title of the notification displayed by the foreground service.
     */
    private val KEY_NOTIFICATION_TITLE = stringPreferencesKey("notification_title")

    /**
     * Preference key for storing the message content of the notification displayed by the foreground service.
     */
    private val KEY_NOTIFICATION_MESSAGE = stringPreferencesKey("notification_message")

    /**
     * Preference key for storing whether to show alarm activity when device is active/unlocked.
     */
    private val KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_DEVICE_IS_ACTIVE =
        booleanPreferencesKey("should_show_alarm_activity_when_device_is_active")

    /**
     * Preference key for storing whether to show alarm activity when app is in foreground.
     */
    private val KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_APP_IS_ACTIVE =
        booleanPreferencesKey("should_show_alarm_activity_when_app_is_active")

    /**
     * Saves the provided [TriggerXConfig] to DataStore.
     * This includes the activity class name and optionally the notification title and message.
     *
     * @param context The [Context] used to access DataStore.
     * @param config The [TriggerXConfig] instance containing the settings to save.
     */
    suspend fun save(context: Context, config: TriggerXConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVITY_CLASS] = config.activityClass.name
            config.notificationTitle?.let {
                prefs[KEY_NOTIFICATION_TITLE] = it
            }
            config.notificationMessage?.let {
                prefs[KEY_NOTIFICATION_MESSAGE] = it
            }
            prefs[KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_DEVICE_IS_ACTIVE] =
                config.shouldShowAlarmActivityWhenDeviceIsActive
            prefs[KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_APP_IS_ACTIVE] = config.showAlarmActivityWhenAppIsActive
        }
    }

    /**
     * Loads the [TriggerXConfig] from DataStore.
     * If essential data (like the activity class name) is missing, or if the class
     * cannot be loaded, it returns `null`. Default values are used for notification
     * title and message if they are not found in preferences.
     *
     * @param context The [Context] used to access DataStore.
     * @return A [TriggerXConfig] instance populated with the loaded settings,
     *         or `null` if loading fails or essential data is missing.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun load(context: Context): TriggerXConfig? {
        val prefs = context.dataStore.data.firstOrNull() ?: return null
        val className = prefs[KEY_ACTIVITY_CLASS] ?: return null
        val title = prefs[KEY_NOTIFICATION_TITLE] ?: "Alarm" // Default title
        val message = prefs[KEY_NOTIFICATION_MESSAGE] ?: "Alarm is ringing" // Default message
        val shouldShowAlarmActivity = prefs[KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_DEVICE_IS_ACTIVE] ?: true
        val showAlarmActivityWhenInForeground = prefs[KEY_SHOULD_SHOW_ALARM_ACTIVITY_WHEN_APP_IS_ACTIVE] ?: true

        return try {
            val clazz = Class.forName(className) as Class<out Activity>
            TriggerXConfig().apply {
                activityClass = clazz
                notificationTitle = title
                notificationMessage = message
                shouldShowAlarmActivityWhenDeviceIsActive = shouldShowAlarmActivity
                showAlarmActivityWhenAppIsActive = showAlarmActivityWhenInForeground
            }
        } catch (e: Exception) {
            LoggerConfig.logger.e("Failed to load activity class from prefs", e)
            null
        }
    }
}
