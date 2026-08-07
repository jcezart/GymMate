package com.example.gymmate.presentation

import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.model.WorkoutSession
import com.example.gymmate.domain.model.WorkoutSessionExercise
import com.example.gymmate.domain.model.ExerciseHistory

data class GymMateUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val errorMessage: String? = null,
    val activeSession: WorkoutSession? = null,
    val sessionExercises: List<WorkoutSessionExercise> = emptyList(),
    val exerciseHistory: List<ExerciseHistory> = emptyList(),
    val personalRecord: Float? = null,
    val historyExerciseId: String? = null
)