package com.meticha.triggerx.logger

import android.util.Log

object DefaultTriggerXLogger : TriggerXLogger {
    override fun d(message: String) {
        Log.d(LoggerConfig.TAG, message)
    }

    override fun e(message: String, throwable: Throwable?) {
        Log.e(LoggerConfig.TAG, message, throwable)
    }

    override fun i(message: String) {
        Log.i(LoggerConfig.TAG, message)
    }

    override fun w(message: String) {
        Log.w(LoggerConfig.TAG, message)
    }
}