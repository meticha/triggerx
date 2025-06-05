package com.meticha.triggerx.logger

import android.util.Log

/**
 * Default implementation of [TriggerXLogger] that logs messages using Android's [Log] utility.
 *
 * This logger uses a predefined tag from [LoggerConfig.TAG] for all log messages.
 */
internal object DefaultTriggerXLogger : TriggerXLogger {
    /**
     * Logs a debug message.
     *
     * @param message The message to be logged.
     */
    override fun d(message: String) {
        Log.d(LoggerConfig.TAG, message)
    }

    /**
     * Logs an error message along with an optional [Throwable].
     *
     * @param message The error message to be logged.
     * @param throwable An optional [Throwable] to log alongside the message (e.g., an exception).
     */
    override fun e(message: String, throwable: Throwable?) {
        Log.e(LoggerConfig.TAG, message, throwable)
    }

    /**
     * Logs an informational message.
     *
     * @param message The message to be logged.
     */
    override fun i(message: String) {
        Log.i(LoggerConfig.TAG, message)
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to be logged.
     */
    override fun w(message: String) {
        Log.w(LoggerConfig.TAG, message)
    }
}
