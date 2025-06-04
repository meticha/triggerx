package com.meticha.triggerx.permission

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.meticha.triggerx.permission.AlarmPermissionManager.isGranted
import java.lang.ref.WeakReference


@SuppressLint("ComposableNaming")
@Composable
fun rememberAppPermissionState(): PermissionState {

    val permissions = buildList {
        addAll(
            listOf(
                PermissionType.ALARM,
                PermissionType.OVERLAY,
                PermissionType.BATTERY_OPTIMIZATION
            )
        )
        if (Build.MANUFACTURER.equals("Xiaomi", true)) {
            add(PermissionType.LOCK_SCREEN)
        }
    }

    val context = LocalContext.current

    val permissionState = remember(permissions) { PermissionState(permissions) }

    // Provide context access through composable scope
    permissionState.contextRef = WeakReference(context)


    // Handle lifecycle events
    PermissionLifeCycleCheckEffect(
        permissionState = permissionState
    )

    // Set up permission launcher
    permissionState.launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when {
            isGranted(context, permissionState.currentPermission!!) -> {
                permissionState.allRequiredGranted()
                permissionState.next()
            }

            else                                                    -> handlePermissionDenial(
                permissionState
            )
        }
    }

    // Display permission rationale popup if needed
    permissionState.currentPermission?.let { permission ->
        when {
            permissionState.showRationalePopUp -> {
                ShowPopup(
                    message = "Bhai api do have permission",
                    onConfirm = {
                        permissionState.showRationalePopUp = false
                        permissionState.requestPermission()
                    },
                    onDismiss = {
                        permissionState.showRationalePopUp = false
                    }
                )
            }
        }
    }

    return permissionState
}


/**
 * Handles permission denial, showing appropriate UI based on denial context
 */
private fun handlePermissionDenial(permissionState: PermissionState) {
    permissionState.showRationalePopUp = true
//    permissionState.showSettingsPopUp = true
}


@Composable
fun PermissionLifeCycleCheckEffect(
    permissionState: PermissionState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME,
) {

    val observer = LifecycleEventObserver { _, event ->
        Log.e("COMPOSE PERMISSION", "Event: $event")
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

@Composable
fun ShowPopup(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Permission")
        },
        text = {
            Text(text = message)
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(text = "OK")
            }
        }
    )
}