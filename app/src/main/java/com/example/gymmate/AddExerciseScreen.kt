package com.example.gymmate

// AddExerciseScreen.kt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.gymmate.data.Exercise
import com.example.gymmate.data.ExerciseDAO
import kotlinx.coroutines.launch

@Composable
fun AddExerciseScreen(exerciseDao: ExerciseDAO) {
    var exerciseName by remember { mutableStateOf("") }
    var exerciseWeight by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf(listOf<Exercise>()) }
    val scope = rememberCoroutineScope()


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TextField(
            value = exerciseName,
            onValueChange = { exerciseName = it },
            label = { Text("Nome do Exercício") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { /* handle next action if needed */ }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = exerciseWeight,
            onValueChange = { exerciseWeight = it },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* handle done action if needed */ }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (exerciseName.isNotBlank() && exerciseWeight.isNotBlank()) {
                    val weight = exerciseWeight.toFloatOrNull() ?: 0f
                    val newExercise = Exercise(exerciseName = exerciseName, weight =  weight, sets = 0, reps = 0, date = "01/01/2023", id = 0)
                    scope.launch {
                        exerciseDao.insertExercise(newExercise)
                    }
                    exerciseName = ""
                    exerciseWeight = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Exercício")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExerciseList(exercises)
    }
}

@Composable
fun ExerciseList(exercises: List<Exercise>) {
    LazyColumn {
        items(exercises) { exercise ->
            ExerciseItem(exercise)
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = exercise.exerciseName)
        Text(text = "${exercise.weight} kg")
    }
}
