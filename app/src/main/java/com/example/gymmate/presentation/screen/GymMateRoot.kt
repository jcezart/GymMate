package com.example.gymmate.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymmate.billing.PremiumUiState
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.presentation.GymMateUiState
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.gymmate.billing.PremiumViewModel
import com.example.gymmate.presentation.timer.RestTimerAction
import com.example.gymmate.presentation.timer.RestTimerUiState
import com.example.gymmate.presentation.timer.RestTimerViewModel
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GymMateRoot(
    viewModel: GymMateViewModel = koinViewModel(),
    restTimerViewModel: RestTimerViewModel = koinViewModel(),
    premiumViewModel: PremiumViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by restTimerViewModel.uiState.collectAsState()
    val premiumState by premiumViewModel.premiumState.collectAsState()
    val activity = LocalActivity.current
    val lifecycleOwner = activity as? LifecycleOwner

    val shouldRefreshPremium by rememberUpdatedState(
        !premiumState.isLoading
    )

    DisposableEffect(lifecycleOwner) {

        if (lifecycleOwner == null) {
            return@DisposableEffect onDispose {}
        }

        val observer = LifecycleEventObserver { _, event ->

            if (
                event == Lifecycle.Event.ON_RESUME &&
                shouldRefreshPremium
            ) {
                premiumViewModel.restorePurchases()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    GymMateScreen(
        state = uiState,
        timerState = timerState,
        premiumState = premiumState,
        onAction = viewModel::dispatch,
        onTimerAction = restTimerViewModel::dispatch,
        onPurchaseMonthly = premiumViewModel::purchaseMonthly,
        onPurchaseLifetime = premiumViewModel::purchaseLifetime,
        onRestorePurchases = premiumViewModel::restorePurchases
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
        onTimerAction = {},
        premiumState = PremiumUiState(
            isPro = false,
            isLoading = false
        ),
        onPurchaseMonthly = {},
        onPurchaseLifetime = {},
        onRestorePurchases = {}
    )
}