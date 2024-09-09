package com.example.gymmate.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey
    @ColumnInfo("Key")
    val id: Int,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val date: String

)