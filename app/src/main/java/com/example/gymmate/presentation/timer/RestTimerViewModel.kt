package com.example.gymmate.presentation.timer

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RestTimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RestTimerUiState())
    val uiState: StateFlow<RestTimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var endTimeMillis: Long? = null

    fun dispatch(action: RestTimerAction) {
        when (action) {
            RestTimerAction.ToggleTimer -> toggleTimer()
            RestTimerAction.AddThirtySeconds -> changeTime(30)
            RestTimerAction.SubtractThirtySeconds -> changeTime(-30)
            RestTimerAction.ResetTimer -> resetTimer()
        }
    }

    private fun toggleTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        val remainingSeconds = _uiState.value.remainingSeconds

        if (remainingSeconds <= 0) return

        endTimeMillis =
            SystemClock.elapsedRealtime() + remainingSeconds * 1_000L

        _uiState.update { it.copy(isRunning = true) }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val endTime = endTimeMillis ?: break

                val remainingMillis =
                    (endTime - SystemClock.elapsedRealtime())
                        .coerceAtLeast(0L)

                val remainingSeconds =
                    ((remainingMillis + 999L) / 1_000L).toInt()

                _uiState.update {
                    it.copy(
                        remainingSeconds = remainingSeconds,
                        isRunning = remainingSeconds > 0
                    )
                }

                if (remainingSeconds == 0) {
                    endTimeMillis = null
                    break
                }

                delay(200L)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()

        val remainingSeconds = endTimeMillis?.let { endTime ->
            val remainingMillis =
                (endTime - SystemClock.elapsedRealtime())
                    .coerceAtLeast(0L)

            ((remainingMillis + 999L) / 1_000L).toInt()
        } ?: _uiState.value.remainingSeconds

        endTimeMillis = null

        _uiState.update {
            it.copy(
                remainingSeconds = remainingSeconds,
                isRunning = false
            )
        }
    }

    private fun changeTime(seconds: Int) {
        val currentSeconds = if (_uiState.value.isRunning) {
            endTimeMillis?.let { endTime ->
                val remainingMillis =
                    (endTime - SystemClock.elapsedRealtime())
                        .coerceAtLeast(0L)

                ((remainingMillis + 999L) / 1_000L).toInt()
            } ?: _uiState.value.remainingSeconds
        } else {
            _uiState.value.remainingSeconds
        }

        val newSeconds = (currentSeconds + seconds).coerceAtLeast(0)

        if (_uiState.value.isRunning && newSeconds > 0) {
            endTimeMillis =
                SystemClock.elapsedRealtime() + newSeconds * 1_000L
        }

        if (newSeconds == 0) {
            timerJob?.cancel()
            endTimeMillis = null
        }

        _uiState.update {
            it.copy(
                remainingSeconds = newSeconds,
                isRunning = it.isRunning && newSeconds > 0
            )
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        endTimeMillis = null
        _uiState.value = RestTimerUiState()
    }
}