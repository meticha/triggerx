package com.meticha.triggerxexample.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.meticha.triggerx.TriggerXActivity

class AppAlarmActivity : TriggerXActivity() {

    @Composable
    override fun AlarmContent() {
        val bundle = remember { intent?.getBundleExtra("ALARM_DATA") }
        val title = bundle?.getString("title") ?: "empty title"
        val location = bundle?.getString("location")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 50.sp,
                    color = Color.Red
                )
                Text(
                    text = location ?: "empty location",
                    fontSize = 50.sp,
                    color = Color.Red
                )
            }
        }
    }
}
