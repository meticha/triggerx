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


object AlarmPermissionManager {
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        } else {
            true // On older versions, this permission is implicitly granted
        }
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun hasBatteryOptimizationPermission(context: Context): Boolean {
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val packageName = context.packageName
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

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

    fun isGranted(context: Context, permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.ALARM -> hasExactAlarmPermission(context)
            PermissionType.OVERLAY -> hasOverlayPermission(context)
            PermissionType.BATTERY_OPTIMIZATION -> hasBatteryOptimizationPermission(context)
            PermissionType.LOCK_SCREEN -> isShowOnLockScreenPermissionEnable(context)
            PermissionType.NOTIFICATION -> isNotificationPermissionEnabled(context)
        }
    }

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
                    Intent()
                }
            }

        }
    }
}

enum class PermissionType { ALARM, OVERLAY, BATTERY_OPTIMIZATION, LOCK_SCREEN, NOTIFICATION }

/**
 * Manages the state of permission requests and their UI flows
 */
class PermissionState(
    permissionList: List<PermissionType>,
) {
    // WeakReference to context to avoid memory leaks
    lateinit var contextRef: WeakReference<Context>

    // All permissions that need to be handled
    private val allPermissions = mutableStateListOf<PermissionType>()

    // Permissions waiting to be processed
    private var pendingPermissions = mutableStateListOf<PermissionType>()

    // Currently processing permission
    internal var currentPermission by mutableStateOf<PermissionType?>(null)

    // UI state
    internal var showRationalePopUp by mutableStateOf(false)
    internal var resumedFromSettings by mutableStateOf(false)

    // Permission states
    private var isRequiredPermissionGranted by mutableStateOf(false)

    // Permission request launcher
    internal var launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null

    init {
        allPermissions.addAll(permissionList)
        pendingPermissions.addAll(permissionList)
    }

    /**
     * Checks if all required permissions are actually granted
     */
    fun allRequiredGranted(): Boolean {
        isRequiredPermissionGranted = allPermissions
            .all { isGranted(it) }
        return isRequiredPermissionGranted
    }

    /**
     * Checks if a specific permission is granted
     */
    fun isGranted(permission: PermissionType): Boolean {
        val context = requireNotNull(contextRef.get())
        return AlarmPermissionManager.isGranted(context, permission)
    }

    /**
     * Starts or continues the permission request flow
     */
    fun requestPermission() {
        if (pendingPermissions.isNotEmpty()) {
            currentPermission = pendingPermissions.first()
            currentPermission?.let { permission ->
                if (isGranted(permission)) {
                    next() // Permission already granted, move to next
                } else {
                    launcher?.launch(
                        AlarmPermissionManager.createPermissionIntent(
                            requireNotNull(contextRef.get()),
                            permission
                        )
                    )
                }
            }
        }
    }

    /**
     * Moves to the next permission in the queue
     */
    internal fun next() {
        if (pendingPermissions.isNotEmpty()) {
            pendingPermissions.removeAt(0)
        }

        requestPermission()
    }

}

