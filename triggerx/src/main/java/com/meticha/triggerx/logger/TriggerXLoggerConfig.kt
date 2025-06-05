package com.meticha.triggerx.logger

/**
 * Configuration object for logging within the TriggerX library.
 * It holds settings such as the default log tag and the active logger instance.
 */
internal object LoggerConfig {
    /**
     * The default tag used for Logcat messages by the [DefaultTriggerXLogger].
     */
    const val TAG = "TRIGGERX"

    /**
     * The [TriggerXLogger] instance used by the library for logging.
     * By default, this is initialized to [DefaultTriggerXLogger].
     * It can be overridden to use a custom logger implementation.
     * @see com.meticha.triggerx.dsl.TriggerXConfig.logging
     */
    var logger: TriggerXLogger = DefaultTriggerXLogger
}
