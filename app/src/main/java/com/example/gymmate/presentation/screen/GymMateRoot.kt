package com.example.gymmate.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.presentation.GymMateAction
import com.example.gymmate.presentation.GymMateUiState
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GymMateRoot(
    viewModel: GymMateViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GymMateScreen(
        state = uiState,
        onAction = { action -> viewModel.dispatch(action) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GymMateRootPreview() {
    // Preview com dados fake
    val fakeState = GymMateUiState(
        isLoading = false,
        categories = listOf(
            Category("Workout A"),
            Category("Workout B"),
            Category("Workout C")
        ),
        selectedCategory = "Workout A",
        exercises = listOf(
            Exercise(
                id = "1",
                exerciseName = "Bench Press",
                sets = 3,
                reps = 10,
                weight = 60f,
                date = SimpleDateFormat("dd/MM", Locale.ENGLISH).format(Date()),
                category = "Workout A"
            ),
            Exercise(
                id = "2",
                exerciseName = "Squat",
                sets = 4,
                reps = 8,
                weight = 80f,
                date = SimpleDateFormat("dd/MM", Locale.ENGLISH).format(Date()),
                category = "Workout A"
            )
        ),
        errorMessage = null
    )

    GymMateScreen(
        state = fakeState,
        onAction = { /* No-op for preview */ }
    )
}