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
