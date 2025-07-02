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