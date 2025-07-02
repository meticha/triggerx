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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.meticha.triggerx.permission.PermissionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "triggerx_permission_status")

internal object TriggerXManualPermissionStatusManager {

    suspend fun savePermissionDialogResponse(
        context: Context,
        permissionType: PermissionType,
        acknowledged: Boolean,
    ) {
        val key = getPreferenceKeyForType(permissionType)
        context.dataStore.edit { settings ->
            settings[key] = acknowledged
        }
    }

    fun isPermissionDialogAcknowledged(
        context: Context,
        permissionType: PermissionType,
    ): Flow<Boolean> {
        val key = getPreferenceKeyForType(permissionType)
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: false
            }
    }

    private fun getPreferenceKeyForType(permissionType: PermissionType): Preferences.Key<Boolean> {
        return booleanPreferencesKey("permission_dialog_shown_${permissionType.name}")
    }

}