---
sidebar_position: 1
---

# Overview

**TriggerX** is a modular, developer-friendly alarm execution library for Android. It simplifies
scheduling exact alarms and showing user-facing UIs at a specific time, even when your App has been
killed. You don't need worry about managing foreground-services, wake-locks, lock-screen flags or
permission management

## ❓Why TriggerX?

If you’ve ever tried to show a UI screen at an exact time on Android, you probably know the pain.

You set up an alarm. You test it a couple of times. It works. You ship it. And then—nothing. The App
was killed. The phone was in Doze mode or the system silently blocked your foreground service. No
screen shows up. No user interaction. Just silence.

We’ve been there too. Way too many times.

At [Meticha](https://meticha.com), we were building features that really depended on reliable,
time-based UI triggers. But we didn’t want to keep fighting the system every time. So we created
TriggerX, a library that just works. Even if the app is cleared from recent apps. Even if the device
is locked or idle. It’s built with Kotlin and Jetpack Compose and made for developers who care about
experience and reliability.

## 📱 What about the Play Store?

That was one of our top concerns too.

TriggerX **only** asks for permissions that are **absolutely necessary**. Things like
`SCHEDULE_EXACT_ALARM` or `SYSTEM_ALERT_WINDOW` are used carefully, and only when required. We’ve
designed it in a way that aligns with Android’s background execution limits, and we’ve tested it
against Play Store guidelines.

So yes, you can use TriggerX in apps you plan to publish on the Play Store. And we’ve included
helper methods to handle permission flows gracefully, so you’re always in control of what the user
sees and when.

TriggerX isn’t just a tool. It’s the developer experience we wished existed when we needed to show
something right when it mattered.