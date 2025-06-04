package com.meticha.triggerx.provider

import android.os.Bundle

/**
 * Contract that a host-app implements so TriggerX can fetch
 * the latest, type-specific data **just before** the alarm UI
 * is shown (even after process death).
 *
 * Trigger flow
 * ------------
 * 1.  Your app schedules an alarm with an *id* and *type*
 *     via [TriggerXAlarmScheduler.scheduleAlarm].
 * 2.  When the alarm fires, TriggerX’ foreground-service calls
 *     [provideData] on the implementation you registered in
 *     `TriggerX.init { dataProvider = … }`.
 * 3.  The returned [Bundle] is forwarded to your custom
 *     `TriggerXAlarmActivity` via the “ALARM_DATA” intent extra.
 *
 * Guidelines for implementers
 * ---------------------------
 * • **Suspend-friendly** – you can call Room, Retrofit, etc.
 * • **Return only what you need** – keep the bundle small
 *   (strings, numbers, parcelables).
 * • **Always handle missing records** (return an empty Bundle).
 *
 * Example
 * -------
 * ```kotlin
 * class MyAlarmDataProvider @Inject constructor(
 *     private val meetingDao: MeetingDao,
 *     private val workoutDao: WorkoutDao
 * ) : TriggerXDataProvider {
 *
 *     override suspend fun provideData(alarmId: Int, alarmType: String): Bundle {
 *         return when (alarmType) {
 *             "MEETING" -> {
 *                 meetingDao.getMeetingById(alarmId)
 *                     ?.let { bundleOf("title" to it.title, "location" to it.location) }
 *                     ?: bundleOf()
 *             }
 *             "WORKOUT" -> {
 *                 workoutDao.getPlan(alarmId)
 *                     ?.let { bundleOf("routine" to it.name) }
 *                     ?: bundleOf()
 *             }
 *             else -> bundleOf()
 *         }
 *     }
 * }
 * ```
 *
 * @param alarmId   Unique ID you passed when scheduling the alarm.
 * @param alarmType Developer-defined string identifying the alarm category
 *                  (e.g. "MEETING", "WORKOUT").  Use it to decide which
 *                  data source to query.
 * @return          A [Bundle] containing serialisable key/value pairs that
 *                  your alarm activity will consume.  Return an **empty**
 *                  bundle if nothing is available.
 */
interface TriggerXDataProvider {

    /**
     * Fetch (or build) the data to display when the alarm fires.
     *
     * This method is invoked on a background coroutine (`Dispatchers.IO`);
     * it must complete quickly so the alarm UI can appear without delay.
     */
    suspend fun provideData(alarmId: Int, alarmType: String): Bundle
}