package com.example.gymmate.presentation.timer

data class RestTimerUiState(
    val remainingSeconds: Int = 90,
    val isRunning: Boolean = false
) {
    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60

            return "%02d:%02d".format(minutes, seconds)
        }
}
