package com.example.gymmate.domain.model

data class Exercise(
    val id: String,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val date: String,
    val category: String
)
