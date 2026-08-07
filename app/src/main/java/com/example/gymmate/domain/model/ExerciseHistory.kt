package com.example.gymmate.domain.model

data class ExerciseHistory(
    val sessionId: Long,
    val performedAt: Long,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val completedSets: Int
)