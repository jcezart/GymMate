package com.example.gymmate.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val category: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val status: String
)
