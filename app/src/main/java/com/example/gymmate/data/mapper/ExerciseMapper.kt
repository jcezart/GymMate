package com.example.gymmate.data.mapper

import com.example.gymmate.data.local.entity.ExerciseEntity
import com.example.gymmate.domain.model.Exercise

fun ExerciseEntity.toDomain(): Exercise =
    Exercise(
        id = id,
        exerciseName = exerciseName,
        sets = sets,
        reps = reps,
        weight = weight,
        date = date,
        category = category
    )

fun Exercise.toEntity(): ExerciseEntity =
    ExerciseEntity(
        id = id,
        exerciseName = exerciseName,
        sets = sets,
        reps = reps,
        weight = weight,
        date = date,
        category = category
    )
