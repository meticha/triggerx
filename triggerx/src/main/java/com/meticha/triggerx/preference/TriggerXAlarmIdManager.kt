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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "triggerx_alarm_ids")

internal object TriggerXAlarmIdManager {

    private val KEY_ALARM_IDS = stringSetPreferencesKey("alarm_ids")

    suspend fun saveAlarmId(context: Context, id: Int) {
        context.dataStore.edit { settings ->
            val currentIds = settings[KEY_ALARM_IDS] ?: emptySet()
            settings[KEY_ALARM_IDS] = currentIds + id.toString()
        }
    }

    suspend fun removeAlarmId(context: Context, id: Int) {
        context.dataStore.edit { settings ->
            val currentIds = settings[KEY_ALARM_IDS] ?: return@edit
            val idString = id.toString()
            if (idString in currentIds) {
                settings[KEY_ALARM_IDS] = currentIds - idString
            }
        }
    }

    suspend fun getAlarmIds(context: Context): Set<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[KEY_ALARM_IDS] ?: emptySet()
            }.first()
    }

    suspend fun clearAllAlarmIds(context: Context) {
        context.dataStore.edit { settings ->
            settings.remove(KEY_ALARM_IDS)
        }
    }
}
