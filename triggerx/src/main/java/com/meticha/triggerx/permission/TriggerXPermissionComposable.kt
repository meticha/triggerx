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
package com.meticha.triggerx.permission

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.meticha.triggerx.permission.AlarmPermissionManager.isGranted
import com.meticha.triggerx.preference.TriggerXPermissionFlagManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
                PermissionType.BATTERY_OPTIMIZATION,
                PermissionType.NOTIFICATION,
                PermissionType.OVERLAY,
            )
        )
        if (Build.MANUFACTURER.equals("Xiaomi", true)) {
            add(PermissionType.LOCK_SCREEN)
        }
        if (Build.MANUFACTURER.equals("oneplus", true)) {
            add(PermissionType.OVERLAY_WHILE_BACKGROUND)
        }
    }

    val context = LocalContext.current

    val permissionState = remember(permissions) { PermissionState(permissions) }
    val coroutineScope = rememberCoroutineScope()

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
        coroutineScope.launch {
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
    }

    // Display permission rationale popup if needed
    permissionState.currentPermission?.let { permission ->
        when {
            permission.isManualPermissionType && permissionState.showPermissionGuidanceDialog -> {
                ShowPermissionGuidanceDialog(
                    permission = permission,
                    permissionState = permissionState,
                    context = context,
                    coroutineScope = coroutineScope
                )
            }

            permissionState.showRationalePopUp                                                -> {
                ShowPopup(
                    message = "Permissions are required to proceed further",
                    onConfirm = {
                        permissionState.showRationalePopUp = false
                        coroutineScope.launch { permissionState.requestPermission() }
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

@Composable
private fun ShowPermissionGuidanceDialog(
    context: Context,
    permission: PermissionType,
    permissionState: PermissionState,
    coroutineScope: CoroutineScope,
) {
    when (permission) {
        PermissionType.OVERLAY_WHILE_BACKGROUND -> {
            val appName =
                remember { context.applicationInfo.loadLabel(context.packageManager).toString() }
            ShowManualPermissionDialog(
                message = "For alarms to reliably appear when the app is in the background, " +
                          "'$appName' needs an additional permission on some phones (like Xiaomi, Oppo, Vivo, etc.).\n\n" +
                          "Please go to your phone's Settings -> Apps -> Manage Apps (or similar) -> Find '$appName' -> " +
                          "Other permissions (or App permissions) -> And ensure 'Display pop-up windows while running in the background' " +
                          "(or a similar sounding option like 'Start in background') is ENABLED.\n\n" +
                          "This reminder is shown once if you click 'Understood'.",
                onDismiss = {
                    permissionState.showPermissionGuidanceDialog = false
                    coroutineScope.launch {
                        TriggerXPermissionFlagManager.savePermissionDialogResponse(
                            context,
                            permissionType = permission,
                            acknowledged = false
                        )
                    }
                },
                onConfirm = {
                    permissionState.showPermissionGuidanceDialog = false
                    coroutineScope.launch {
                        TriggerXPermissionFlagManager.savePermissionDialogResponse(
                            context,
                            permissionType = permission,
                            acknowledged = true
                        )
                        permissionState.next()
                    }
                },
            )
        }

        else                                    -> {}
    }
}

/**
 * Handles permission denial, showing appropriate UI based on denial context
 */
private fun handlePermissionDenial(permissionState: PermissionState) {
    permissionState.showRationalePopUp = true
}

@Composable
internal fun ShowManualPermissionDialog(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = {
            Text(
                message
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Acknowledge")
            }
        }
    )
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

