# TriggerX Alarm Scheduling Library

**TriggerX** is an Android library designed to simplify the process of scheduling exact alarms that
can trigger a foreground service and subsequently launch an Activity, even when the app is in the
background or the device is locked. It handles necessary permissions and provides a customizable
foreground service notification.

This library is particularly useful for applications like reminder apps, task schedulers, or any app
that needs to reliably alert the user at a specific time by bringing an activity to the forefront.

## Table of Contents

1. [Key Features](#key-features)
2. [Integration](#integration)
3. [Core Concepts](#core-concepts)
    - [Permission Handling](#permission-handling)
    - [Alarm Flow](#alarm-flow)
4. [Usage](#usage)
    - [Requesting Permissions (Jetpack Compose)](#requesting-permissions-jetpack-compose)
    - [Scheduling an Alarm](#scheduling-an-alarm)
    - [Customizing Foreground Service Notification](#customizing-foreground-service-notification)
5. [Activity Screen Waking](#activity-screen-waking)
6. [Troubleshooting](#troubleshooting)

## Key Features

- Simplified exact alarm scheduling.
- Handles runtime permissions for Exact Alarms, Overlay (Display over other apps), and Battery
  Optimization exemption.
- Provides a `rememberAlarmPermissions` composable for easy permission management in Jetpack
  Compose.
- Includes a foreground service (`TriggerXForegroundService`) that displays a notification and
  launches a target activity (`TriggerActivity`).
- `TriggerActivity` is designed to show over the lock screen and turn the screen on.
- Manages WakeLock to ensure the device wakes up for the alarm.

## Integration

1. **Add the library to your project:**
   Assuming `triggerx` is a local module in your project, ensure it's included in your
   `settings.gradle.kts`:
   ```kotlin
   // settings.gradle.kts
   include(":app", ":triggerx")
   ```

2. **Add dependency to your app module:**
   In your app-level `build.gradle.kts` (`app/build.gradle.kts`):
   ```kotlin
   dependencies {
       implementation(project(":triggerx"))
       // ... other dependencies
   }
   ```

3. **Ensure your app module uses Java 17 (if not already set for the library):
   ```kotlin
   // app/build.gradle.kts
   android {
       // ...
       compileOptions {
           sourceCompatibility = JavaVersion.VERSION_17
           targetCompatibility = JavaVersion.VERSION_17
       }
       kotlinOptions {
           jvmTarget = "17"
       }
   }
   ```

## Core Concepts

### Permission Handling

To reliably schedule exact alarms and display an activity over the lock screen, several permissions
are required:

1. **`SCHEDULE_EXACT_ALARM`**: For scheduling exact alarms (Android 12+).
    - For Android 14+ (API 34), this permission is granted by default for most apps but can be
      revoked. The library helps direct the user to settings if needed.
    - For Android 12 and 13 (API 31, 32, 33), the user must explicitly grant this via a special app
      access screen.
2. **`SYSTEM_ALERT_WINDOW` (Display over other apps)**: For the `TriggerActivity` to reliably
   display over other apps and potentially the lock screen, especially on some manufacturer OS (like
   Xiaomi).
3. **`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`**: To ensure the alarm and foreground service are not
   unduly restricted by battery optimization features.
4. **`POST_NOTIFICATIONS`**: Required on Android 13+ (API 33) to show the foreground service
   notification.
5. **`FOREGROUND_SERVICE`**: For running a foreground service (automatically added if your app
   targets Android P/API 28+).
6. **`WAKE_LOCK`**: To wake the device.

The `triggerx` library provides a Jetpack Compose utility `rememberAlarmPermissions()` to help
manage the user flow for granting these permissions.

### Alarm Flow

1. **Application schedules an alarm:** Your app uses Android's `AlarmManager` to set an exact alarm.
   The `Intent` for this alarm should target `com.meticha.triggerx.receivers.TriggerXAlarmReceiver`
   with the action `com.meticha.triggerx.ALARM_ACTION`.
2. **Alarm triggers `TriggerXAlarmReceiver`:** When the alarm fires, `TriggerXAlarmReceiver` (a
   `BroadcastReceiver`) is invoked.
3. **Receiver starts `TriggerXForegroundService`:** The receiver acquires a `WakeLock` and then
   starts `TriggerXForegroundService`.
4. **`TriggerXForegroundService` shows notification & launches Activity:**
    - The service starts in the foreground, displaying a notification to the user (customizable icon
      needed).
    - It then launches `com.meticha.triggerx.TriggerActivity`.
    - After launching the activity, the service calls `stopSelf()` to stop itself.
    - The `WakeLock` acquired by the receiver is released by the service.
5. **`TriggerActivity` is displayed:** This activity is configured to show when the device is locked
   and turn the screen on, bringing the alert to the user's attention.

## Usage

### Requesting Permissions (Jetpack Compose)

The library includes `rememberAlarmPermissions()` composable function in
`TriggerXPermissionComposable.kt` to streamline permission requests.

```kotlin
// In your Composable screen
import com.meticha.triggerx.permission.rememberAlarmPermissions

@Composable
fun MyScreenWithAlarmFeature() {
    val alarmPermissionController = rememberAlarmPermissions(
        onAllPermissionsGranted = {
            // This callback is invoked when all necessary permissions are granted.
            // You can now proceed to schedule alarms.
            Log.d("MyScreen", "All permissions granted! Ready to schedule alarms.")
            // scheduleYourAlarmHere() // Example function call
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        if (!alarmPermissionController.state.allPermissionsGranted) {
            Button(onClick = { alarmPermissionController.requestPermissions() }) {
                Text("Grant Necessary Permissions for Alarms")
            }

            // Display detailed status (optional)
            if (!alarmPermissionController.state.hasExactAlarmPermission) {
                Text("Exact Alarm Permission: Needed", color = Color.Red)
            }
            if (!alarmPermissionController.state.hasOverlayPermission) {
                Text("Display Over Other Apps: Needed", color = Color.Red)
            }
            if (!alarmPermissionController.state.isBatteryOptimizationDisabled) {
                Text("Battery Optimization Exemption: Needed", color = Color.Red)
            }
            if (alarmPermissionController.state.shouldShowXiaomiSpecificOverlaySteps) {
                Text(
                    "Xiaomi devices need an extra step for 'Display over other apps'. Follow prompts carefully.",
                    color = Color.Yellow
                )
            }
        } else {
            Text("All permissions are granted. You can schedule alarms.", color = Color.Green)
            // Button to schedule alarm, etc.
            Button(onClick = { /* Call your alarm scheduling logic */ }) {
                Text("Schedule Alarm")
            }
        }
    }
}
```

**Key parts of `alarmPermissionController.state`:**

- `hasExactAlarmPermission: Boolean`
- `hasOverlayPermission: Boolean`
- `isBatteryOptimizationDisabled: Boolean`
- `hasNotificationPermission: Boolean` (automatically handled by the permission flow for API 33+)
- `allPermissionsGranted: Boolean` (True if all critical permissions are granted)
- `shouldRequestNotificationPermission: Boolean` (True if on API 33+ and notification permission is
  needed)
- `shouldShowXiaomiSpecificOverlaySteps: Boolean` (Indicates if special handling for Xiaomi overlay
  permission is active)

**Calling `alarmPermissionController.requestPermissions()` will:**

1. Request Notification permission first (if on API 33+ and not granted).
2. Then, guide the user to grant Exact Alarm permission.
3. Then, guide the user to grant Overlay permission (with special steps for Xiaomi if detected).
4. Finally, guide the user to disable Battery Optimization for your app.

The flow uses `ActivityResultLauncher` and handles returning from system settings screens to
re-check permissions.

### Scheduling an Alarm

Once permissions are granted, you can schedule an alarm using the standard Android `AlarmManager`.

```kotlin
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

// Action string must match the one in TriggerXAlarmReceiver's intent filter
const val TRIGGERX_ALARM_ACTION = "com.meticha.triggerx.ALARM_ACTION"

fun scheduleSingleAlarm(context: Context, timeInMillis: Long, requestCode: Int = 1001) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent =
        Intent(context, com.meticha.triggerx.receivers.TriggerXAlarmReceiver::class.java).apply {
            action = TRIGGERX_ALARM_ACTION
            // You can add extras to the intent if TriggerActivity or TriggerXForegroundService needs them
            // e.g., putExtra("ALARM_ID", alarmId)
        }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        // This should ideally be handled by the permission flow from rememberAlarmPermissions
        // If still not granted, direct user to settings or inform them.
        Log.w("Scheduler", "Cannot schedule exact alarms. Permissions missing.")
        // Optionally, direct to settings again or show a message
        // val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        // context.startActivity(settingsIntent)
        return
    }

    try {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Log.d(
            "Scheduler",
            "Alarm scheduled for: ${
                Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
            }"
        )
    } catch (se: SecurityException) {
        Log.e(
            "Scheduler",
            "SecurityException: Cannot schedule exact alarms. Check permissions.",
            se
        )
        // This might happen if canScheduleExactAlarms() check is somehow bypassed or race condition
    }
}

// Example: Schedule an alarm for 10 seconds from now
// val triggerTime = System.currentTimeMillis() + 10000
// scheduleSingleAlarm(LocalContext.current, triggerTime)
```

### Customizing Foreground Service Notification

The `TriggerXForegroundService` displays a notification. You **must** provide a small icon for this
notification.

1. **Add a small icon** to your `triggerx` library's `drawable` resources (e.g.,
   `triggerx/src/main/res/drawable/ic_alarm_notification.xml`). This icon should be a simple,
   single-color vector asset.
2. **Update the icon in `TriggerXForegroundService.kt`:**
   Open `TriggerXForegroundService.kt` and find the `getNotification()` method. Change the
   `setSmallIcon()` line:

   ```kotlin
   // Inside TriggerXForegroundService.kt -> getNotification()
   private fun getNotification(): Notification {
       // ... (channel creation)
       return NotificationCompat.Builder(this, CHANNEL_ID)
           .setContentTitle("Alarm Active")
           .setContentText("Your scheduled alarm is ringing.")
           .setSmallIcon(com.meticha.triggerx.R.drawable.ic_your_actual_icon) // <-- CHANGE THIS
           .setPriority(NotificationCompat.PRIORITY_HIGH)
           .setCategory(NotificationCompat.CATEGORY_ALARM)
           .build()
   }
   ```

   Replace `com.meticha.triggerx.R.drawable.ic_your_actual_icon` with the actual resource ID of your
   icon (e.g., `R.drawable.ic_alarm_notification`).

   *Note: The library uses `android.R.drawable.ic_dialog_info` as a placeholder if no specific icon
   is set, which is not ideal for a production app.*

You can further customize the notification's title, text, channel name, etc., directly in
`TriggerXForegroundService.kt` if needed.

## Activity Screen Waking

`TriggerActivity.kt` is responsible for appearing when the alarm triggers. It attempts to:

1. Show over the lock screen.
2. Turn the screen on.

This is handled programmatically within `TriggerActivity.onCreate()`:

- **For API 27 (Android 8.1) and above:** It uses `setShowWhenLocked(true)` and
  `setTurnScreenOn(true)`.
- **For API 26 (Android 8.0):** It uses `Window` flags (
  `WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED`, `FLAG_TURN_SCREEN_ON`, etc.) since the direct
  methods are not available.

This ensures the best effort to bring the activity to the user's attention on relevant API levels.

## Troubleshooting

- **Permissions not granted / Alarm not firing:**
    - Use the `rememberAlarmPermissions()` composable and ensure
      `alarmPermissionController.state.allPermissionsGranted` is true before scheduling.
    - Pay special attention to Xiaomi devices; the "Display over other apps" permission might
      require extra manual steps by the user in the app's settings.
    - Check Logcat for any errors from `AlarmManager`, `TriggerXAlarmReceiver`, or
      `TriggerXForegroundService`.
- **Foreground service notification icon is generic:** Ensure you have replaced the placeholder icon
  in `TriggerXForegroundService.getNotification()` with your app's own small icon resource.
- **Activity not showing over lock screen:** This can be device-dependent. The library uses standard
  methods, but some aggressive OEM customizations might interfere. Ensure all permissions (
  especially Overlay) are granted.
- **Lint warnings for `showWhenLocked` / `turnScreenOn` in Manifest:** The library handles this
  programmatically in `TriggerActivity`. If these attributes are still in your
  `triggerx/src/main/AndroidManifest.xml` for `TriggerActivity`, they can be removed, as the
  programmatic approach provides more nuanced API level handling.

---
