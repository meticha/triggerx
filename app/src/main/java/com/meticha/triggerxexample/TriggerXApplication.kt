/*
 * Designed and developed by MetichaHQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            shouldShowAlarmActivityWhenAppIsActive = true
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