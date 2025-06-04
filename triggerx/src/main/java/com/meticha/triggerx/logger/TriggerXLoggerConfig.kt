package com.meticha.triggerx.logger

object LoggerConfig {
    const val TAG = "TRIGGERX"

    var logger: TriggerXLogger = DefaultTriggerXLogger



    fun setLogger(customLogger: TriggerXLogger) {
        logger = customLogger
    }
}