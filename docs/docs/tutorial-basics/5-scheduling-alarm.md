---
title: ⏰ Scheduling Your First Alarm
description: Once you’ve handled the permissions, scheduling an alarm is super easy with TriggerX.
sidebar_position: 5
---

Once you’ve handled the permissions, scheduling an alarm is super easy with TriggerX. In just one
tap, you can schedule a UI to open even if your app is closed or cleared from recents. This ensures
your users never miss the moment.

Here’s how to do it:

```kotlin
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val permissionState = rememberAppPermissionState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(
                onClick = {
                    if (permissionState.allRequiredGranted()) {
                        val triggerTime = Calendar.getInstance().apply {
                            add(Calendar.MINUTE, 1) // Triggers after 1 minute
                        }.timeInMillis

                        val scheduled = TriggerXAlarmScheduler().scheduleAlarm(
                            context = context,
                            triggerAtMillis = triggerTime,
                            type = "MEETING",
                            alarmId = 1
                        )

                        if (scheduled) {
                            Toast.makeText(
                                context,
                                "Alarm scheduled successfully. App can now be closed 🔔",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to schedule alarm. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        permissionState.requestPermission()
                    }
                }
            ) {
                Text("Schedule Activity")
            }
        }
    }
}
```

## 🚀 What Happens Behind the Scenes

- The alarm is set to fire 1 minute later
- `TriggerXAlarmScheduler` ensures the scheduled time is exact and reliable
- Once it fires, your custom activity (`MyAlarmActivity`) opens even if the app has been killed
- Toast messages give instant feedback to the user that the alarm has been scheduled

## Expected output

<img src="/triggerx/docs/img/triggerx_example.gif" alt="TriggerX Example GIF" style={{ height: 600 }} />

This is all you need to schedule a real, reliable UI trigger in your Compose app with just a button
click. TriggerX handles the edge cases so you can focus on delivering delightful user experiences.

