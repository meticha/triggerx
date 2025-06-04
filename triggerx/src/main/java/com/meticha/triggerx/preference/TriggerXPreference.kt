package com.meticha.triggerx.preference

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.meticha.triggerx.dsl.TriggerXConfig
import com.meticha.triggerx.logger.LoggerConfig
import kotlinx.coroutines.flow.firstOrNull

object TriggerXPreferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "triggerx_prefs")

    private val KEY_ACTIVITY_CLASS = stringPreferencesKey("activity_class")
    private val KEY_NOTIFICATION_TITLE = stringPreferencesKey("notification_title")
    private val KEY_NOTIFICATION_MESSAGE = stringPreferencesKey("notification_message")

    suspend fun save(context: Context, config: TriggerXConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVITY_CLASS] = config.activityClass.name
            config.notificationTitle?.let {
                prefs[KEY_NOTIFICATION_TITLE] = it
            }
            config.notificationMessage?.let {
                prefs[KEY_NOTIFICATION_MESSAGE] = it
            }
        }
    }

    suspend fun load(context: Context): TriggerXConfig? {
        val prefs = context.dataStore.data.firstOrNull() ?: return null
        val className = prefs[KEY_ACTIVITY_CLASS] ?: return null
        val title = prefs[KEY_NOTIFICATION_TITLE] ?: "Alarm"
        val message = prefs[KEY_NOTIFICATION_MESSAGE] ?: "Alarm is ringing"

        return try {
            val clazz = Class.forName(className) as Class<out Activity>
            TriggerXConfig().apply {
                activityClass = clazz
                notificationTitle = title
                notificationMessage = message
            }
        } catch (e: Exception) {
            LoggerConfig.logger.e("Failed to load activity class from prefs", e)
            null
        }
    }
}
