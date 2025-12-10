package com.example.gymmate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.gymmate.presentation.screen.GymMateRoot
import com.example.gymmate.ui.theme.GymMateTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GymMateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GymMateRoot()
                }
            }
        }
    }
}