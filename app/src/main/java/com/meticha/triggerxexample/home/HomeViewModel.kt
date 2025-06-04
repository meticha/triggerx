package com.meticha.triggerxexample.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.meticha.triggerx.TriggerXAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val appAlarmManager: TriggerXAlarmScheduler = TriggerXAlarmScheduler()

    fun scheduleOneMinuteAlarm(context: Context): Boolean {
        val triggerTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }.timeInMillis

        Log.i(TAG, "Attempting to schedule alarm for 1 minute from now ($triggerTime)")
        return appAlarmManager.scheduleAlarm(context, triggerTime)
    }

    fun cancelCurrentAlarm(context: Context, id: Int) {
        appAlarmManager.cancelAlarm(context, id)
    }
}