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
package com.meticha.triggerx.dsl

import android.app.Activity
import com.meticha.triggerx.DefaultTriggerActivity
import com.meticha.triggerx.logger.TriggerXLogger
import com.meticha.triggerx.provider.TriggerXDataProvider
import kotlin.jvm.java

/**
 * Configuration class for the TriggerX library.
 * Allows customization of various aspects such as notifications, data providers, logging, and target activities.
 *
 * This class is typically used during the initialization of the TriggerX library.
 *
 * Example:
 * ```kotlin
 * TriggerX.init(this) {
 *     activityClass = MyAlarmActivity::class.java
 *     useDefaultNotification(
 *         title = "Alarm Running",
 *         message = "Tap to open",
 *         channelName = "Alarm Notifications"
 *     )
 *     shouldShowAlarmActivityWhenDeviceIsActive = true
 *     alarmDataProvider = MyDataProvider()
 *     logging(MyCustomLogger())
 * }
 * ```
 */
class TriggerXConfig internal constructor() {
    /**
     * The title for the default notification displayed by the foreground service when an alarm fires.
     * This is used if a custom notification setup is not provided.
     *
     * @see useDefaultNotification
     */
    internal var notificationTitle: String? = null

    /**
     * The message body for the default notification displayed by the foreground service.
     * This is used if a custom notification setup is not provided.
     *
     * @see useDefaultNotification
     */
    internal var notificationMessage: String? = null

    /**
     * Controls whether the custom alarm activity should be shown when the device is active/unlocked.
     * When false, the alarm activity will not be shown if the device is currently interactive.
     */
    var shouldShowAlarmActivityWhenDeviceIsActive: Boolean = true

    /**
     * Controls whether the custom alarm activity should be shown when the app is in the foreground.
     * When false, the alarm activity will not be shown if the app is currently visible to the user.
     */
    var showAlarmActivityWhenAppIsActive: Boolean = true

    /**
     * The name for the notification channel used by the default foreground service notification.
     * Defaults to "TriggerX Alarms".
     *
     * @see useDefaultNotification
     */
    internal var notificationChannelName: String = "TriggerX Alarms"

    /**
     * An optional [TriggerXDataProvider] instance.
     * If provided, its `provideData()` method will be called before the alarm activity is launched,
     * allowing dynamic data to be passed to the activity.
     */
    var alarmDataProvider: TriggerXDataProvider? = null

    /**
     * An optional [TriggerXLogger] instance for custom logging within the TriggerX library.
     * If not provided, a default logger (logging to Logcat) will be used.
     *
     * @see logging
     */
    internal var customLogger: TriggerXLogger? = null

    /**
     * The [Activity] class that should be launched when an alarm fires.
     * Defaults to [DefaultTriggerActivity]::class.java.
     * This activity will receive any data provided by the [alarmDataProvider].
     */
    var activityClass: Class<out Activity> = DefaultTriggerActivity::class.java


    /**
     * Configures the details for the default notification shown by the foreground service.
     *
     * @param title The title of the notification.
     * @param message The message body of the notification.
     * @param channelName The name for the notification channel. Defaults to "TriggerX Alarms".
     */
    fun useDefaultNotification(
        title: String,
        message: String,
        channelName: String = "TriggerX Alarms"
    ) {
        notificationTitle = title
        notificationMessage = message
        notificationChannelName = channelName
    }

    /**
     * Sets a custom logger for the TriggerX library.
     *
     * @param logger The [TriggerXLogger] instance to use for logging.
     */
    fun logging(logger: TriggerXLogger) {
        customLogger = logger
    }
}
