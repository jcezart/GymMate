package com.example.gymmate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymmate.data.Exercise
import com.example.gymmate.data.ExerciseDAO
import kotlinx.coroutines.launch

@Composable
fun GymMateScreen(exerciseDao: ExerciseDAO) {
    var exercises by remember { mutableStateOf(listOf<Exercise>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            exerciseDao.getAllExercises().collect { list ->
                exercises = list
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        PerfilCard()
        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre o PerfilCard e a lista

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Margem em volta da lista
        ) {
            items(exercises) { exercise ->
                ExerciseCard(exercise = exercise, exerciseDao = exerciseDao)
            }
        }
    }

}

@Composable
fun PerfilCard() {
    var trainerName by remember { mutableStateOf("") }
    var trainerWeight by remember { mutableStateOf("") }
    var trainerHeight by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = trainerName,
                onValueChange = { trainerName = it },
                label = { Text("Trainer Name") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = trainerWeight,
                onValueChange = { trainerWeight = it },
                label = { Text("Trainer Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = trainerHeight,
                onValueChange = { trainerHeight = it },
                label = { Text("Trainer Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, exerciseDao: ExerciseDAO) {
    var isExpanded by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf(exercise.exerciseName) }
    var exerciseSets by remember { mutableStateOf(exercise.sets.toString()) }
    var exerciseReps by remember { mutableStateOf(exercise.reps.toString()) }
    var exerciseWeight by remember { mutableStateOf(exercise.weight.toString()) }
    var exerciseDate by remember { mutableStateOf(exercise.date) }

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(text = exercise.date)
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Show less" else "Show more"

                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                TextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo para o número de sets
                TextField(
                    value = exerciseSets,
                    onValueChange = { exerciseSets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo para o número de reps
                TextField(
                    value = exerciseReps,
                    onValueChange = { exerciseReps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo para o peso
                TextField(
                    value = exerciseWeight,
                    onValueChange = { exerciseWeight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo para a data
                TextField(
                    value = exerciseDate,
                    onValueChange = { exerciseDate = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                // Botão para salvar as alterações
                Button(
                    onClick = {
                        val updatedExercise = exercise.copy(
                            exerciseName = exerciseName,
                            sets = exerciseSets.toIntOrNull() ?: 0,
                            reps = exerciseReps.toIntOrNull() ?: 0,
                            weight = exerciseWeight.toFloatOrNull() ?: 0f,
                            date = exerciseDate
                        )

                        // Salva no banco de dados
                        scope.launch {
                            exerciseDao.updateExercise(updatedExercise)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}