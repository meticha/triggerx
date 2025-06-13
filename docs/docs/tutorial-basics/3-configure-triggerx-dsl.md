---
title: 🧩 Configuring TriggerX with DSL
description: "Setting up TriggerX is as simple as declaring what you want—literally. Using a `Kotlin DSL`"
sidebar_position: 3
---

Setting up TriggerX is as simple as declaring what you want—literally. Using a `Kotlin DSL`, you can
customize the alarm behavior, the UI to be shown, notifications, and even pass dynamic data when the
alarm fires. This setup usually goes inside your Application class and takes just a few lines to get
things rolling.

In your `Application` class, configure the TriggerX library like this:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        TriggerX.init(this) {

            /* UI that opens when the alarm fires */
            activityClass = MyAlarmActivity::class.java

            /* Foreground-service notification */
            useDefaultNotification(
                title = "Alarm running",
                message = "Tap to open",
                channelName = "Alarm Notifications"
            )
        }
    }
}
```

This is the most essential setup you need to get TriggerX working in your app. Let’s break it down:

- `TriggerX.init(this) { ... }` initializes the library inside your Application class. It should be
  called once at App startup.

- `activityClass = MyAlarmActivity::class.java` tells TriggerX which Activity to launch when the
  alarm fires. This should be a class that extends `TriggerXActivity`, where you define the UI shown
  to the user.

- `useDefaultNotification(...)` sets up a default notification. This is required when alarms fire
  while your app is in the background or killed. The notification helps the system prioritize your
  alarm and ensures **compliance** with foreground execution policies.


