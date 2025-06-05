package com.meticha.triggerx.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.meticha.triggerx.logger.LoggerConfig


/**
 * A Composable side-effect that observes lifecycle events to re-evaluate and request permissions.
 *
 * This is primarily used to detect when the user returns to the app after potentially changing
 * permissions in the system settings. When the specified [lifecycleEvent] (defaulting to `ON_RESUME`)
 * occurs, if `permissionState.resumedFromSettings` is true, it triggers a new permission request
 * sequence.
 *
 * @param permissionState The current [PermissionState] to be monitored and updated.
 * @param lifecycleEvent The [Lifecycle.Event] to observe for triggering the permission check.
 *                       Defaults to [Lifecycle.Event.ON_RESUME].
 */
@Composable
internal fun PermissionLifeCycleCheckEffect(
    permissionState: PermissionState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME,
) {

    val observer = LifecycleEventObserver { _, event ->
        LoggerConfig.logger.d("Event: $event")
        if (event == lifecycleEvent) {
            if (permissionState.resumedFromSettings) {
                permissionState.requestPermission()
                permissionState.resumedFromSettings = false
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}