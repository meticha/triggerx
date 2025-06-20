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
import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.lang.ref.WeakReference
import java.lang.reflect.Method

/**
 * Manages permission checks and Intent creation for various Android permissions
 * required by the TriggerX library.
 */
internal object AlarmPermissionManager {
    /**
     * Checks if the app has permission to schedule exact alarms.
     *
     * @param context The application context.
     * @return `true` if exact alarm permission is granted or not required (pre-Android S), `false` otherwise.
     */
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        } else {
            true // On older versions, this permission is implicitly granted
        }
    }

    /**
     * Checks if the app has permission to draw overlays on top of other apps.
     *
     * @param context The application context.
     * @return `true` if overlay permission is granted, `false` otherwise.
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Checks if the app is whitelisted from battery optimizations.
     *
     * @param context The application context.
     * @return `true` if the app is ignoring battery optimizations, `false` otherwise.
     */
    fun hasBatteryOptimizationPermission(context: Context): Boolean {
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val packageName = context.packageName
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    /**
     * Checks if the "Show on Lock Screen" permission is enabled, typically for Xiaomi (MIUI) devices.
     * This uses reflection to access a hidden API and may not work on all devices or future Android versions.
     *
     * @param context The application context.
     * @return `true` if the permission appears to be enabled, `false` otherwise or if an error occurs.
     */
    @SuppressLint("DiscouragedPrivateApi")
    // TODO("Need to check in future")
    fun isShowOnLockScreenPermissionEnable(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
            val method: Method = AppOpsManager::class.java.getDeclaredMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result =
                method.invoke(manager, 10020, Binder.getCallingUid(), context.packageName) as Int
            AppOpsManager.MODE_ALLOWED == result
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Checks if the app has permission to post notifications.
     *
     * @param context The application context.
     * @return `true` if notification permission is granted or not required (pre-Android Tiramisu), `false` otherwise.
     */
    fun isNotificationPermissionEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notifications don't need explicit permission pre-Android 13
        }
    }

    /**
     * Checks if a specific [PermissionType] is granted.
     *
     * @param context The application context.
     * @param permission The [PermissionType] to check.
     * @return `true` if the specified permission is granted, `false` otherwise.
     */
    fun isGranted(context: Context, permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.ALARM -> hasExactAlarmPermission(context)
            PermissionType.OVERLAY -> hasOverlayPermission(context)
            PermissionType.BATTERY_OPTIMIZATION -> hasBatteryOptimizationPermission(context)
            PermissionType.LOCK_SCREEN -> isShowOnLockScreenPermissionEnable(context)
            PermissionType.NOTIFICATION -> isNotificationPermissionEnabled(context)
        }
    }

    /**
     * Creates an [Intent] to request a specific [PermissionType] from the user.
     * The user should be directed to the system settings screen corresponding to this intent.
     *
     * @param context The application context.
     * @param permissionType The [PermissionType] for which to create the request intent.
     * @return An [Intent] that can be used to launch the relevant system settings screen.
     *         Returns an empty Intent for [PermissionType.NOTIFICATION] on pre-Tiramisu devices
     *         as no explicit system settings intent is typically used.
     */
    fun createPermissionIntent(context: Context, permissionType: PermissionType): Intent {
        when (permissionType) {
            PermissionType.ALARM -> {
                return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
            }

            PermissionType.OVERLAY -> {
                return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = "package:${context.packageName}".toUri()
                }
            }

            PermissionType.BATTERY_OPTIMIZATION -> {
                return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${context.packageName}".toUri()
                }
            }

            PermissionType.LOCK_SCREEN -> {
                // This intent is specific to MIUI and might not work on other devices.
                return Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity"
                    )
                    putExtra("extra_pkgname", context.packageName)
                }
            }

            PermissionType.NOTIFICATION -> {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                } else {
                    Intent() // No standard intent pre-Tiramisu, permission granted by default.
                }
            }
        }
    }
}

/**
 * Represents the different types of permissions that the TriggerX library
 * may need to check or request.
 */
enum class PermissionType {
    /** Permission to schedule exact alarms (Android S+). */
    ALARM,

    /** Permission to draw overlays on top of other applications. */
    OVERLAY,

    /** Permission to be exempt from battery optimizations. */
    BATTERY_OPTIMIZATION,

    /** Special permission for showing on the lock screen, primarily for Xiaomi (MIUI) devices. */
    LOCK_SCREEN,

    /** Permission to post notifications (Android Tiramisu+). */
    NOTIFICATION
}

/**
 * Manages the state of permission requests and their UI flows, particularly for a list
 * of required permissions. This class is typically used in conjunction with Jetpack Compose.
 *
 * @param permissionList The initial list of [PermissionType]s that this state manager will handle.
 */
class PermissionState(
    permissionList: List<PermissionType>,
) {
    /**
     * A [WeakReference] to the [Context] to avoid memory leaks.
     * This should be set by the composable that initializes [PermissionState].
     */
    lateinit var contextRef: WeakReference<Context>

    // All permissions that need to be handled by this instance
    private val allPermissions = mutableStateListOf<PermissionType>()

    // Permissions currently waiting to be processed in the request queue
    private var pendingPermissions = mutableStateListOf<PermissionType>()

    /**
     * The [PermissionType] currently being processed or for which a rationale might be shown.
     * Null if no permission is currently active in the flow.
     */
    internal var currentPermission by mutableStateOf<PermissionType?>(null)

    /**
     * Controls whether a rationale popup should be shown to the user.
     * Set to `true` to indicate a rationale is needed before re-requesting a denied permission.
     */
    internal var showRationalePopUp by mutableStateOf(false)

    /**
     * Flag to indicate if the app has recently resumed from a system settings screen
     * where the user might have changed permissions.
     */
    internal var resumedFromSettings by mutableStateOf(false)

    // Internal state tracking if all required permissions are granted.
    private var isRequiredPermissionGranted by mutableStateOf(false)

    /**
     * The [ManagedActivityResultLauncher] used to launch permission request intents
     * (e.g., system settings screens) and handle their results.
     * This should be initialized and set by the composable that uses [PermissionState].
     */
    internal var launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null

    init {
        allPermissions.addAll(permissionList)
        pendingPermissions.addAll(permissionList)
    }

    /**
     * Checks if all permissions in the `allPermissions` list for this state
     * are currently granted.
     *
     * @return `true` if all required permissions are granted, `false` otherwise.
     *         This also updates an internal state flag.
     */
    fun allRequiredGranted(): Boolean {
        isRequiredPermissionGranted = allPermissions
            .all { isGranted(it) }
        return isRequiredPermissionGranted
    }

    /**
     * Checks if a specific [PermissionType] is granted.
     * Requires [contextRef] to be initialized and valid.
     *
     * @param permission The [PermissionType] to check.
     * @return `true` if the permission is granted, `false` otherwise or if context is unavailable.
     * @throws IllegalStateException if [contextRef] has not been initialized.
     * @throws NullPointerException if [contextRef.get()] returns null after initialization.
     */
    fun isGranted(permission: PermissionType): Boolean {
        val context =
            requireNotNull(contextRef.get()) { "Context not available. Ensure contextRef is set." }
        return AlarmPermissionManager.isGranted(context, permission)
    }

    /**
     * Starts or continues the permission request flow.
     * It processes the permissions from the `pendingPermissions` queue one by one.
     * If a permission is already granted, it moves to the next.
     * Otherwise, it launches the appropriate system intent using the [launcher].
     * Requires [contextRef] and [launcher] to be initialized.
     */
    fun requestPermission() {
        if (pendingPermissions.isNotEmpty()) {
            currentPermission = pendingPermissions.first()
            currentPermission?.let { permission ->
                if (isGranted(permission)) {
                    next() // Permission already granted, move to next
                } else {
                    val context =
                        requireNotNull(contextRef.get()) { "Context not available for launching intent." }
                    launcher?.launch(
                        AlarmPermissionManager.createPermissionIntent(
                            context,
                            permission
                        )
                    )
                }
            }
        } else {
            currentPermission = null // No more pending permissions
        }
    }

    /**
     * Moves to the next permission in the `pendingPermissions` queue
     * and then attempts to request it.
     * This is typically called after a permission request has been handled (granted or denied).
     */
    internal fun next() {
        if (pendingPermissions.isNotEmpty()) {
            pendingPermissions.removeAt(0)
        }
        requestPermission() // Attempt to process the next permission, or clear currentPermission if queue is empty
    }
}
