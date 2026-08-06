package com.example.gymmate.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.presentation.GymMateUiState
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.gymmate.presentation.timer.RestTimerAction
import com.example.gymmate.presentation.timer.RestTimerUiState
import com.example.gymmate.presentation.timer.RestTimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GymMateRoot(
    viewModel: GymMateViewModel = koinViewModel(),
    restTimerViewModel: RestTimerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by restTimerViewModel.uiState.collectAsState()

    GymMateScreen(
        state = uiState,
        timerState = timerState,
        onAction = viewModel::dispatch,
        onTimerAction = restTimerViewModel::dispatch
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
        timerState = RestTimerUiState(),
        onAction = {},
        onTimerAction = {}
    )
}