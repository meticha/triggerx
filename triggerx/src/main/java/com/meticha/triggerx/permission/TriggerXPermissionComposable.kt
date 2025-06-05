package com.meticha.triggerx.permission

import android.annotation.SuppressLint
import android.os.Build
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
import com.meticha.triggerx.logger.LoggerConfig
import com.meticha.triggerx.permission.AlarmPermissionManager.isGranted
import java.lang.ref.WeakReference


/**
 * Remembers and manages the state of essential permissions required by the TriggerX library.
 *
 * This composable function initializes a [PermissionState] that tracks and helps request
 * permissions for exact alarms, overlays (display over other apps), battery optimization exemption,
 * and notifications (on API 33+). It also includes special considerations for Xiaomi devices
 * regarding lock screen display permissions.
 *
 * The [PermissionState] returned by this function can be used to check if all required
 * permissions are granted and to trigger permission requests. It handles the complexities
 * of launching system dialogs for these permissions and reacting to their results.
 *
 * It also sets up a [PermissionLifeCycleCheckEffect] to re-check permissions when the
 * composable's lifecycle resumes, which is useful for when the user returns from system settings.
 *
 * @return A [PermissionState] instance that holds the current status of all relevant permissions
 *         and provides methods to request them.
 */
@SuppressLint("ComposableNaming")
@Composable
fun rememberAppPermissionState(): PermissionState {

    val permissions = buildList {
        addAll(
            listOf(
                PermissionType.ALARM,
                PermissionType.OVERLAY,
                PermissionType.BATTERY_OPTIMIZATION,
                PermissionType.NOTIFICATION
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

            else -> handlePermissionDenial(
                permissionState
            )
        }
    }

    // Display permission rationale popup if needed
    permissionState.currentPermission?.let { permission ->
        when {
            permissionState.showRationalePopUp -> {
                ShowPopup(
                    message = "Permissions are required to proceed further",
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
}



/**
 * A composable function that displays a standard [AlertDialog].
 *
 * This popup is typically used to show rationale to the user before requesting a sensitive permission
 * or to inform them about the necessity of a permission if it has been denied.
 *
 * @param message The main message to be displayed in the dialog.
 * @param onConfirm A lambda function to be executed when the confirm button (e.g., "Grant") is clicked.
 * @param onDismiss A lambda function to be executed when the dismiss button (e.g., "Cancel") is clicked
 *                  or when the dialog is dismissed by tapping outside or pressing the back button.
 */
@Composable
internal fun ShowPopup(
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
                Text(text = "Grant")
            }
        }
    )
}
