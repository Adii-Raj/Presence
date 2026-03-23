package com.application.presence

import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.application.presence.Navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDeveloperModeEnabled = Settings.Global.getInt(
            contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) != 0

        if (!isDeveloperModeEnabled) {
            Toast.makeText(
                this,
                "Please disable Developer Mode to use this app.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return // Stop execution here so setContent is never called
        }

        setContent {
            NavGraph()
        }
    }
}
