package com.meticha.triggerx.logger

/**
 * Interface for logging within the TriggerX library.
 * Allows for custom logger implementations to be plugged into TriggerX.
 */
interface TriggerXLogger {
    /**
     * Logs a debug message.
     *
     * @param message The message to be logged.
     */
    fun d(message: String)

    /**
     * Logs an error message, optionally with an associated [Throwable].
     *
     * @param message The error message to be logged.
     * @param throwable An optional [Throwable] (e.g., an exception) to log with the message.
     */
    fun e(message: String, throwable: Throwable? = null)

    /**
     * Logs an informational message.
     *
     * @param message The message to be logged.
     */
    fun i(message: String)

    /**
     * Logs a warning message.
     *
     * @param message The message to be logged.
     */
    fun w(message: String)
}
