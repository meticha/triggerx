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

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meticha.triggerx.permission.rememberAppPermissionState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val permissionState = rememberAppPermissionState()
    val coroutines = rememberCoroutineScope()


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(
                onClick = {
                    coroutines.launch {
                        if (permissionState.allRequiredGranted()) {
                            Toast.makeText(
                                context,
                                "Scheduled for 1 minute. Now you can close the App and chill",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.scheduleOneMinuteAlarm(
                                context
                            )
                        } else {
                            permissionState.requestPermission()
                        }
                    }
                }
            ) {
                Text("Schedule Activity")
            }
        }
    }
}