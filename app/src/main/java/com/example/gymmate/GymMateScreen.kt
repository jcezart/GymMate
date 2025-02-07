package com.example.gymmate

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymmate.data.Exercise
import com.example.gymmate.data.ExerciseDAO
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import com.example.gymmate.CustomTooltip


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GymMateScreen(exerciseDao: ExerciseDAO) {
    val gymMateViewModel: GymMateViewModel = viewModel(
        factory = GymMateViewModelFactory(exerciseDao)
    )
    val exercises by gymMateViewModel.exercises.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                GymMateFAB {
                    val newExerciseId =
                        (exercises.maxByOrNull { it.id.toInt() }?.id?.toIntOrNull() ?: 0) + 1

                    val newExercise = Exercise(
                        id = newExerciseId.toString(),
                        exerciseName = "",
                        sets = 0,
                        reps = 0,
                        weight = 0f,
                        date = SimpleDateFormat(
                            "dd/MM",
                            java.util.Locale.ENGLISH
                        ).format(Date.from(Instant.now()))
                    )
                    gymMateViewModel.addExercise(newExercise)
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "gymmate",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(18.dp),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(exercises) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onUpdateExercise = { updatedExercise ->
                                    gymMateViewModel.updateExercise(updatedExercise)
                                },
                                onDeleteExercise = { exerciseToDelete ->
                                    gymMateViewModel.deleteExercise(exerciseToDelete)
                                }
                            )
                        }
                    }
                }
            }
        )

        if (exercises.isEmpty()) {
            CustomTooltip(
                text = "Add an exercise to start",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-60).dp, y = (-80).dp)
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onUpdateExercise: (Exercise) -> Unit,
    onDeleteExercise: (Exercise) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var exerciseName by remember { mutableStateOf(exercise.exerciseName) }
    var exerciseSets by remember { mutableStateOf(if (exercise.sets == 0) "" else exercise.sets.toString()) }
    var exerciseReps by remember { mutableStateOf(if (exercise.reps == 0) "" else exercise.reps.toString()) }
    var exerciseWeight by remember { mutableStateOf(if (exercise.weight.toDouble() == 0.0) "" else exercise.weight.toString()) }
    var exerciseDate by remember { mutableStateOf(exercise.date) }

    LaunchedEffect(exercise) {
        exerciseName = exercise.exerciseName
        exerciseSets = if (exercise.sets == 0) "" else exercise.sets.toString()
        exerciseReps = if (exercise.reps == 0) "" else exercise.reps.toString()
        exerciseWeight = if (exercise.weight.toDouble() == 0.0) "" else exercise.weight.toString()
        exerciseDate = exercise.date
    }

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
                    modifier = Modifier.weight(1f)
                )
                Text(text = exercise.date)

                IconButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp),
                        contentDescription = "Delete Exercise"
                    )
                }

                if (showDialog) {
                    ConfirmDeleteDialog(
                        onConfirm = {
                            onDeleteExercise(exercise)
                            showDialog = false
                        },
                        onDismiss = { showDialog = false }
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Show less" else "Show more"
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("New Exercise") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                // Campo para o número de sets
                TextField(
                    value = exerciseSets,
                    onValueChange = { exerciseSets = it },
                    label = { Text("Sets") },
                    placeholder = { Text("Sets Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo para o número de reps
                TextField(
                    value = exerciseReps,
                    onValueChange = { exerciseReps = it },
                    label = { Text("Reps") },
                    placeholder = { Text("Reps Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo para o peso
                TextField(
                    value = exerciseWeight,
                    onValueChange = { exerciseWeight = it },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("Weight") },
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
                        onUpdateExercise(updatedExercise)
                        isExpanded = false
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

@Composable
fun GymMateFAB(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFabClick,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add exercise"
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this exercise?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
