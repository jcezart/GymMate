package com.example.gymmate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.gymmate.data.local.dao.ExerciseDAO
import com.example.gymmate.data.local.db.GymMateDataBase
import com.example.gymmate.presentation.screen.GymMateScreen
import com.example.gymmate.ui.theme.GymMateTheme

class MainActivity : ComponentActivity() {

    private lateinit var exerciseDAO: ExerciseDAO
    private lateinit var db: GymMateDataBase



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        db = GymMateDataBase.getDatabase(this)
        exerciseDAO = db.exerciseDao()


        setContent {
            GymMateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GymMateScreen(exerciseDao = exerciseDAO)
                }
            }
        }
    }
}