package com.example.gymmate.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.datasource.local.entity.CategoryEntity
import com.example.gymmate.data.datasource.local.entity.ExerciseEntity
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.presentation.component.CustomTooltip
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
//import com.example.gymmate.presentation.viewmodel.GymMateViewModelFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GymMateScreen(

  //  exerciseDao: ExerciseDAO
    gymMateViewModel: GymMateViewModel

) {
//    val gymMateViewModel: GymMateViewModel = viewModel(
//        factory = GymMateViewModelFactory(exerciseDao)
//    )
    val exercises by gymMateViewModel.exercises.collectAsState()
    val categories by gymMateViewModel.categories.collectAsState()
    var selectedCategory by remember { mutableStateOf("Workout A") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf("") }
    var categoryToRename by remember { mutableStateOf("") }
    var newCategoryName by remember { mutableStateOf("") }
    val filteredExercises = exercises.filter { it.category == selectedCategory }

    // Garante que selectedCategory seja válida quando as categorias mudam
//    LaunchedEffect(categories) {
//        if (categories.isNotEmpty()) {
//            if (!categories.map { it.nameDb }.contains(selectedCategory)) {
//                selectedCategory = categories.first().nameDb
//            }
//        }
//    }
    LaunchedEffect(categories) {
        if (categories.isNotEmpty()) {
            if (!categories.map { it.name }.contains(selectedCategory)) {
                selectedCategory = categories.first().name
            }
        } else {
            selectedCategory = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                GymMateFAB {
                    val newExerciseId =
                        (exercises.maxByOrNull { it.id.toInt() }?.id?.toIntOrNull() ?: 0) + 1
//                    val newExercise = ExerciseEntity(
//                        id = newExerciseId.toString(),
//                        exerciseName = "",
//                        sets = 0,
//                        reps = 0,
//                        weight = 0f,
//                        date = SimpleDateFormat("dd/MM", Locale.ENGLISH).format(Date.from(Instant.now())),
//                        category = selectedCategory
//                    )
                    val newExercise = Exercise(
                        id = newExerciseId.toString(),
                        exerciseName = "",
                        sets = 0,
                        reps = 0,
                        weight = 0f,
                        date = SimpleDateFormat("dd/MM", Locale.ENGLISH).format(Date()),
                        category = selectedCategory
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

                    LazyRow(modifier = Modifier.padding(18.dp)) {
                        items(categories) { category ->
                            Button(
                               // onClick = { selectedCategory = category.nameDb },
                                onClick = { selectedCategory = category.name },
                                modifier = Modifier.padding(1.dp),
                               // colors = if (category.nameDb == selectedCategory) ButtonDefaults.buttonColors(containerColor = Color.DarkGray) else ButtonDefaults.buttonColors()
                                colors = if (category.name == selectedCategory) ButtonDefaults.buttonColors(containerColor = Color.DarkGray) else ButtonDefaults.buttonColors()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 4.dp)
                                ) {
                                   // Text(text = category.nameDb)
                                    Text(text = category.name)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
//                                            categoryToRename = category.nameDb
//                                            newCategoryName = category.nameDb
//                                            showRenameDialog = true
                                            categoryToRename = category.name
                                            newCategoryName = category.name
                                            showRenameDialog = true
                                        },
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            contentDescription = "Rename Category",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (categories.size > 1) {
                                        IconButton(
                                            onClick = {
//                                                categoryToDelete = category.nameDb
//                                                showDeleteDialog = true
                                                categoryToDelete = category.name
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier
                                                .size(18.dp)
                                                .padding(start = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                tint = MaterialTheme.colorScheme.error,
                                                contentDescription = "Delete Category",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Button(
                                onClick = { showCategoryDialog = true },
                                modifier = Modifier.padding(1.dp),
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "+")
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(filteredExercises) { exercise ->
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

        if (showCategoryDialog) {
            AddCategoryDialog(
                onConfirm = { newCategoryName ->
                 //   if (newCategoryName.isNotBlank() && newCategoryName !in categories.map { it.nameDb }) {
                    if (newCategoryName.isNotBlank() && newCategoryName !in categories.map { it.name }) {
                        gymMateViewModel.addCategory(CategoryEntity(newCategoryName))
                        selectedCategory = newCategoryName
                    }
                    showCategoryDialog = false
                },
                onDismiss = { showCategoryDialog = false }
            )
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                title = "Delete Category",
                message = "Are you sure you want to delete '$categoryToDelete'? This will also delete all associated exercises.",
                onConfirm = {
                    gymMateViewModel.deleteCategory(categoryToDelete)
                   // if (selectedCategory == categoryToDelete) selectedCategory = categories.first().nameDb
                    if (selectedCategory == categoryToDelete) selectedCategory = categories.first().name
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (showRenameDialog) {
            RenameCategoryDialog(
                currentName = categoryToRename,
                onConfirm = { newName ->
                  //  if (newName.isNotBlank() && newName !in categories.map { it.nameDb }) {
                    if (newName.isNotBlank() && newName !in categories.map { it.name }) {
                        gymMateViewModel.renameCategory(categoryToRename, newName)
                        if (selectedCategory == categoryToRename) selectedCategory = newName
                    }
                    showRenameDialog = false
                },
                onDismiss = { showRenameDialog = false }
            )
        }

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
   // exercise: ExerciseEntity,
    exercise: Exercise,
   // onUpdateExercise: (ExerciseEntity) -> Unit,
    onUpdateExercise: (Exercise) -> Unit,
    //onDeleteExercise: (ExerciseEntity) -> Unit
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

                IconButton(onClick = { showDialog = true }) {
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

                TextField(
                    value = exerciseSets,
                    onValueChange = { exerciseSets = it },
                    label = { Text("Sets") },
                    placeholder = { Text("Sets Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseReps,
                    onValueChange = { exerciseReps = it },
                    label = { Text("Reps") },
                    placeholder = { Text("Reps Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseWeight,
                    onValueChange = { exerciseWeight = it },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseDate,
                    onValueChange = { exerciseDate = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Button(
                    onClick = {
//                        val updatedExercise: ExerciseEntity = exercise.copy(
//                            exerciseName = exerciseName,
//                            sets = exerciseSets.toIntOrNull() ?: 0,
//                            reps = exerciseReps.toIntOrNull() ?: 0,
//                            weight = exerciseWeight.toFloatOrNull() ?: 0f,
//                            date = exerciseDate
//                        )
                        val updatedExercise: Exercise = exercise.copy(
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
    title: String = "Confirm Deletion",
    message: String = "Are you sure you want to delete this exercise?",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
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

@Composable
fun AddCategoryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Category") },
        text = {
            Column {
                Text("Enter the name of the new category:")
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameCategoryDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Category") },
        text = {
            Column {
                Text("Enter the new name for '$currentName':")
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("New Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank() && categoryName != currentName
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}