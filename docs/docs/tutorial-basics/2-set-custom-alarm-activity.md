---
title: 🎬 Set Your Custom Alarm Activity
description: Before anything else, tell TriggerX what to show when the alarm fires. 
sidebar_position: 2
---

Before anything else, tell TriggerX what to show when the alarm fires. This is done by passing your
custom `Activity` class, typically one that extends `TriggerXActivity`. This activity becomes the
entry point for your UI, even when the app is killed or in background.

Once you extend your class with `TriggerXActivity`, you can override the `AlarmContent` composable
and put your custom UI over there. Also, since `TriggerxActivity` extends the `ComponentActivity`
itself, you can also do all sorts of stuff that you might be doing in other activities. Like playing
the music in the background while the activity is showing 😉

Here's an example of a custom `AppAlarmActivity`:

```kotlin
class AppAlarmActivity : TriggerXActivity() {

    @Composable
    override fun AlarmContent() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Trigger Icon",
                    tint = Color(0xFF111111),
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TriggerX",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Example",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
```
