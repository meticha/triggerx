package com.meticha.triggerxexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.meticha.triggerxexample.home.HomeScreen
import com.meticha.triggerxexample.ui.theme.TriggerXExampleTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriggerXExampleTheme {
                HomeScreen()
            }
        }
    }
}

