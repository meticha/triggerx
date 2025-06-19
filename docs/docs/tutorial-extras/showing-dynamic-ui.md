---
title: Showing dynamic UI
description: Configure TriggerX to show dynamic UI based on your Room database
sidebar_position: 1
---

# 📦 Dynamic UI with Room Database using TriggerX

TriggerX makes it super simple to launch time-triggered UI in Android. But did you know you can also show **dynamic UI backed by Room database**? This example shows how to use `TriggerXDataProvider` to fetch up-to-date data from a Room database and display it in a custom alarm screen.

---

## 🚀 What You'll Build

A fully working demo where:
- A user taps a button to schedule an alarm.
- After 1 minute, a custom activity launches even if the app is killed.
- The UI pulls fresh data from a Room database and displays it.

---

## 🛠️ Prerequisites

- TriggerX library already integrated
- Basic Room setup with DAO and Entity
- Compose UI

---

## 1. Setup TriggerX in `Application` class

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        TriggerX.init(this) {
            activityClass = MyAlarmActivity::class.java

            useDefaultNotification(
                title = "Alarm running",
                message = "Your scheduled screen is opening",
                channelName = "TriggerX Notifications"
            )

            alarmDataProvider = object : TriggerXDataProvider {
                override suspend fun provideData(alarmId: Int, alarmType: String): Bundle {
                    val db = AppDatabase.getInstance(this@MyApplication)
                    val task = db.taskDao().getTaskById(alarmId)
                    return bundleOf(
                        "task_title" to task?.title,
                        "task_description" to task?.description
                    )
                }
            }
        }
    }
}
```

## 2. Build the UI with Compose
Inherit from TriggerXActivity and override AlarmContent() to render your dynamic screen:

```kotlin
class MyAlarmActivity : TriggerXActivity() {

    @Composable
    override fun AlarmContent() {
        val title = intent.getStringExtra("task_title") ?: "No Title"
        val description = intent.getStringExtra("task_description") ?: "No Description"

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, fontSize = 18.sp)
        }
    }
}
```

## 3. Schedule the Alarm
Use this code in your Composable to schedule the alarm after inserting the task:

```kotlin
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val alarmScheduler = remember { TriggerXAlarmScheduler() }
    val permissionState = rememberAppPermissionState()

    val scope = rememberCoroutineScope()
    

    ElevatedButton(
        onClick = {
        if (permissionState.allRequiredGranted()) {
            val db = AppDatabase.getInstance(context)
            scope.launch {
                db.taskDao().insert(Task(1, "Meeting", "Discuss the roadmap"))

                val triggerTime = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 1)
                }.timeInMillis

                alarmScheduler.scheduleAlarm(
                    context = context,
                    triggerAtMillis = triggerTime,
                    type = "TASK",
                    alarmId = 1
                )
            }
        } else {
            permissionState.requestPermission()
        }
    }) {
        Text("Schedule Dynamic Alarm")
    }
}
```

## ✅ Done!
Now, close your app and wait for a minute. You'll see the UI pop up with dynamic data fetched from the Room database even if the app was killed.

Made with ❤️ by [Meticha](https://meticha.com/)