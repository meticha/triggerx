package com.meticha.triggerxexample

import android.app.Application
import com.meticha.triggerx.dsl.TriggerX
import com.meticha.triggerxexample.alarm.AppAlarmActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TriggerXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TriggerX.init(applicationContext) {
            useDefaultNotification(
                title = "My App Alarm",
                message = "Your alarm is ringing!",
                channelName = "My Awesome App Notifications"
            )
            activityClass = AppAlarmActivity::class.java
        }
    }
}