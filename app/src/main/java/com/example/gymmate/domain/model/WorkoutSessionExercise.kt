package com.example.gymmate.domain.model

data class WorkoutSessionExercise(
    val sessionId: Long,
    val exerciseId: String,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val position: Int,
    val completedSets: Int = 0
)