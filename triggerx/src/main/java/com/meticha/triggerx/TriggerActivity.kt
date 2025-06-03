package com.meticha.triggerx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

class TriggerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flags to show activity over lock screen and turn screen on
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Optional: If you want to explicitly dismiss the keyguard
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//            keyguardManager.requestDismissKeyguard(this, null)
//        }

        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ALARM!",
                    fontSize = 50.sp,
                    color = Color.Red
                )
            }
        }
    }
}
