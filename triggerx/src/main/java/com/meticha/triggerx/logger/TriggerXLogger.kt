package com.meticha.triggerx.logger

interface TriggerXLogger {
    fun d(message: String)
    fun e(message: String, throwable: Throwable? = null)
    fun i(message: String)
    fun w(message: String)
}