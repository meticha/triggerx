---
title: Showing Alarm UI conditionally
description: Configure TriggerX to show dynamic UI based on your Room database
sidebar_position: 2
---

# 💊 Showing Alarm UI conditionally

TriggerX gives you full control over **whether** to show the alarm screen based on your App’s logic, such
as values stored in a Room database, preferences, or feature flags.

You can configure these conditions using the `TriggerX.init()` function inside your `Application` class.

## 📱 1. Skip Alarm UI When App Is Active

If you don’t want the alarm UI to appear while the App is already in the foreground (e.g. user is using the app), set
the following:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        TriggerX.init(this) {
            showAlarmActivityWhenAppIsActive = false
        }
    }
} 
```

This is useful when you want to handle the alarm inside the app manually without launching a separate screen.

## 💡 2. Skip Alarm UI When Device Is Active

If you want to avoid interrupting the user when they’re actively using the device (e.g. screen is on), set this:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        TriggerX.init(this) {
            shouldShowAlarmActivityWhenDeviceIsActive = false
        }
    }
}
```