package com.meticha.triggerxexample

import android.app.Application
import android.os.Bundle
import androidx.core.os.bundleOf
import com.meticha.triggerx.dsl.TriggerX
import com.meticha.triggerx.provider.TriggerXDataProvider
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
            alarmDataProvider = object : TriggerXDataProvider {
                override suspend fun provideData(alarmId: Int, alarmType: String): Bundle {
                    return when (alarmType) {
                        "MEETING" -> {
                            bundleOf("title" to "TriggerX", "location" to "Compose")
                        }

                        else -> bundleOf()
                    }
                }
            }
        }
    }
}