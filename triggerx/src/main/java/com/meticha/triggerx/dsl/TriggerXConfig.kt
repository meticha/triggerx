package com.meticha.triggerx.dsl

import android.app.Activity
import com.meticha.triggerx.DefaultTriggerActivity
import com.meticha.triggerx.logger.TriggerXLogger
import kotlin.jvm.java

class TriggerXConfig internal constructor() {
    internal var notificationTitle: String? = null
    internal var notificationMessage: String? = null
    internal var notificationChannelName: String = "TriggerX Alarms"

    internal var customLogger: TriggerXLogger? = null
    var activityClass: Class<out Activity> = DefaultTriggerActivity::class.java


    fun useDefaultNotification(
        title: String,
        message: String,
        channelName: String = "TriggerX Alarms"
    ) {
        notificationTitle = title
        notificationMessage = message
        notificationChannelName = channelName
    }

    fun logging(logger: TriggerXLogger) {
        customLogger = logger
    }
}
