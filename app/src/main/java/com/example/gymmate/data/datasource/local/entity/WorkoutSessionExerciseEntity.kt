package com.example.gymmate.data.datasource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "workout_session_exercises",
    primaryKeys = ["sessionId", "exerciseId"]
)
data class WorkoutSessionExerciseEntity(

    val sessionId: Long,

    val exerciseId: String,

    val exerciseName: String,

    val sets: Int,

    val reps: Int,

    val weight: Float,

    val position: Int,

    val completedSets: Int = 0
)