package com.example.travelagency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.travelagency.ui.navigation.AppRoot
import com.example.travelagency.ui.theme.TravelAgencyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TravelAgencyTheme {
                AppRoot()
            }
        }
    }
}
