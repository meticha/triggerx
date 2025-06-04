
## ✨ Why TriggerX?

| What devs usually do 😩                                    | What you do with TriggerX 😎                  |
|------------------------------------------------------------|----------------------------------------------|
| Fight Doze / idle / OEM quirks                             | `scheduleAlarm(id, type, time)`              |
| Build a foreground-service + notification                  | *We ship it.* You brand it with one line.    |
| Tangle “show over lock-screen” flags                       | Subclass `TriggerXAlarmActivity` – done.     |
| Re-register alarms after process kill / swipe-away         | We persist essentials in **DataStore**.      |
| Handle ∞ permission flows                                  | `rememberAppPermissionState()` covers them.  |
| Pass fresh data from Room / Retrofit at alarm time         | Implement **one** suspend function.          |

Give yourself back a sprint – drop TriggerX in and ship that feature.

---

## 🔑 Key Features

* **Exact Alarms** – wakes device even in Doze; multiple alarms supported.
* **Lock-Screen Activity** – full-screen, show-when-locked, turn-screen-on (API 26+ handled).
* **Kotlin DSL** – one-liner setup. Brand the notification, logger, alarm UI.
* **Dynamic Data** – inject a `TriggerXDataProvider` (Room, network, anything) – returned as `Bundle`.
* **Permission Composable** – covers Exact-Alarm, Overlay, Battery, Post-Notifications.
* **Process-death safe** – essentials stored via **Preferences DataStore**.
* **Pluggable Logging** – use our default, or swap in Timber / Crashlytics.
* **Foreground-service compliant** – Play-Store-safe; low-importance ongoing notif.
* **Minimal App Glue** – call scheduler, supply an activity, you’re done.

---

## 🚀 Quick Start

```kotlin
// Application.kt
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        TriggerX.init(this) {
            // 1️⃣  Brand your lock-screen activity
            activityClass = MyAlarmActivity::class.java

            // 2️⃣  Notification shown while we wake the screen
            useDefaultNotification(
                title = "Alarm ringing",
                message = "Tap to open",
                channelName = "Alarms"
            )

            // 3️⃣  Feed us fresh data whenever an alarm fires
            alarmDataProvider = object : TriggerXDataProvider {
                override suspend fun provideData(alarmId: Int, alarmType: String): Bundle {
                    val meeting = dao.getMeetingById(alarmId)
                    return bundleOf("title" to meeting?.title, "location" to meeting?.location)
                }
            }
        }
    }
}
```

- Schedule to run your activity like this

```kotlin
val scheduler = TriggerXAlarmScheduler()
scheduler.scheduleAlarm(
    context = this,
    alarmId = 42,
    alarmType = "MEETING",
    triggerAtMillis = System.currentTimeMillis() + 10 * 60_000 // 10 min
)
```

## 🖌 Craft Your Alarm Screen
```kotlin
class MyAlarmActivity : TriggerXAlarmActivity() {

    @Composable
    override fun AlarmContent() {
        val data = intent.getBundleExtra("ALARM_DATA")
        val title = data?.getString("title") ?: "Meeting"
        val location = data?.getString("location") ?: "TBD"

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Location: $location", fontSize = 20.sp)
        }
    }
}
```

TriggerXAlarmActivity already handles:
- `setShowWhenLocked`, `setTurnScreenOn`
- Legacy window flags for API 21–25
- Automatic stop of foreground service once activity is launched

## 🔐 One-call Permission Gate

```kotlin
@Composable
fun MyScreen() {
    val permissions = rememberAppPermissionState()

    LaunchedEffect(Unit) {
        // triggers the built-in permission flow UI
        permissions.requestPermission()
    }

    if (permissions.allRequiredGranted()) {
        /* your screen */
    }
}
```
Exact-Alarm (API 31+), Overlay, Ignore-Battery-Optimisation, Post-Notifications handled.

## 🧱 Architecture

```scss
App   →  TriggerXAlarmScheduler
↓ PendingIntent (ALARM_ID + ALARM_TYPE)
AlarmManager  →  TriggerXAlarmReceiver
↓
TriggerXForegroundService
↓ (calls AlarmDataProvider.provideData)
┌─────────┴────────┐
│  starts Activity │
└──────────────────┘
```
Only the ID & Type travel through the system.
Your provider always returns fresh data right before UI appears.

## 🤝 Contributing

PRs welcome!
Open an issue, fork, create a branch, and let’s make alarms awesome together.

## ⭐️ If you like TriggerX…

Smash that Star button on GitHub 💫 and share with your Android friends.
Happy waking-up!






# License
```xml
Designed and developed by 2020 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```