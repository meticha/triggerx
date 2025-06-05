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
