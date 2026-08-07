package com.example.gymmate.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val category: String,

    val startedAt: Long,

    val finishedAt: Long? = null,

    val status: String
)
