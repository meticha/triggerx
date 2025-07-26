---
title: 🛠 Installation
description: Installing TriggerX on your project
sidebar_position: 1
---

You can install TriggerX using either Gradle dependencies or Version Catalogs, depending on how your
project is set up.

## Option 1: Gradle (Kotlin DSL)

Add this to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.meticha:triggerx:1.1.0")
}
```

## Option 2: Version Catalog (`libs.versions.toml`)
If you're using Version Catalogs:
1. In your `libs.versions.toml`:

```toml
[versions]
triggerx = "1.1.0"

[libraries]
triggerx = { module = "com.meticha:triggerx", version.ref = "triggerx" }
```

2. Then, use it in your module’s `build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.triggerx)
}
```

## 🛡️ Required Permissions

```xml
<!-- Required to avoid getting blocked by battery optimizations -->
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />

<!-- Allows scheduling of exact alarms -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```