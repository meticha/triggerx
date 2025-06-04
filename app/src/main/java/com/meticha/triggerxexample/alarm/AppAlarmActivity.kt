package com.meticha.triggerxexample.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.meticha.triggerx.TriggerXActivity

class AppAlarmActivity : TriggerXActivity() {
    @Composable
    override fun AlarmContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CAVIN!",
                fontSize = 50.sp,
                color = Color.Red
            )
        }
    }
}
