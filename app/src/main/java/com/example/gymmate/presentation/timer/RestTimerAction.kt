package com.example.gymmate.presentation.timer

sealed interface RestTimerAction {

    data object ToggleTimer : RestTimerAction

    data object AddThirtySeconds : RestTimerAction

    data object SubtractThirtySeconds : RestTimerAction

    data object ResetTimer : RestTimerAction
}