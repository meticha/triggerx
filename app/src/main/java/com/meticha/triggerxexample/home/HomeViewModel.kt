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
package com.meticha.triggerxexample.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meticha.triggerx.TriggerXAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val appAlarmManager: TriggerXAlarmScheduler = TriggerXAlarmScheduler()

    fun scheduleOneMinuteAlarm(context: Context) {
        val triggerTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }.timeInMillis

        Log.i(TAG, "Attempting to schedule alarm for 1 minute from now ($triggerTime)")
        viewModelScope.launch {
            appAlarmManager.scheduleAlarm(
                context,
                triggerTime,
                "MEETING",
                1
            )
        }

    }

    fun cancelCurrentAlarm(context: Context, id: Int) {
        viewModelScope.launch { appAlarmManager.cancelAlarm(context, id) }
    }
}