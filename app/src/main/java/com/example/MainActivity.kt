package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.firebase.configureAppCheck
import com.example.ui.navigation.HidayahApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Must configure Firebase App Check before any Firebase SDK or ViewModel initializes.
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            HidayahApp()
        }
    }
}
