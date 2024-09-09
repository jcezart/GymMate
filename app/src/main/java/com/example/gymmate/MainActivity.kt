package com.example.gymmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.gymmate.data.ExerciseDAO
import com.example.gymmate.data.GymMateDataBase
import com.example.gymmate.ui.theme.GymMateTheme

class MainActivity : ComponentActivity() {

    private lateinit var exerciseDAO: ExerciseDAO
    private lateinit var db: GymMateDataBase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database and DAO
        db = GymMateDataBase.getDatabase(this)
        exerciseDAO = db.exerciseDao()

        //fillDatabaseWithInitialData()

        setContent {
            GymMateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GymMateScreen(exerciseDao = exerciseDAO)
                }
            }
        }
    }
//
//
//    private fun fillDatabaseWithInitialData() {
//        val exercises = listOf(
//            Exercise(exerciseName = "Supino", weight = 20f, sets = 3, reps = 10, date = "2024-09-01", id = 3),
//            Exercise(exerciseName = "Agachamento", weight = 50f, sets = 4, reps = 12, date = "2024-09-02", id = 1),
//            Exercise(exerciseName = "Puxada", weight = 30f, sets = 3, reps = 8, date = "2024-09-03", id = 2),
//            Exercise(exerciseName = "Remada", weight = 40f, sets = 4, reps = 10, date = "2024-09-04", id = 0)
//        )
//
//        CoroutineScope(Dispatchers.IO).launch {
//            exerciseDAO.insertExercises(exercises)
//        }
//    }
}