package com.meticha.triggerx

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

/**
 * Base activity for displaying alarm content.
 *
 * This abstract class provides the necessary setup for an activity that is launched when an alarm fires.
 * It handles window flags for showing the activity over the lock screen and turning the screen on.
 *
 * Subclasses must implement [AlarmContent] to define the UI to be displayed.
 */
abstract class TriggerXActivity : ComponentActivity() {

    /**
     * Override this composable function to define the UI for your alarm screen.
     * This is where you will build the content that the user sees when an alarm is triggered.
     */
    @Composable
    abstract fun AlarmContent()

    /**
     * Called when the activity is starting.
     *
     * This method sets up the window flags to ensure the activity is shown over the lock screen
     * and turns the screen on. It then sets the content of the activity to the [AlarmContent]
     * composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState].  <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        
        setContent { AlarmContent() }
    }
}
