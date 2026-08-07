package com.example.gymmate.data.mapper

import com.example.gymmate.data.datasource.local.entity.WorkoutSessionExerciseEntity
import com.example.gymmate.domain.model.WorkoutSessionExercise

fun WorkoutSessionExerciseEntity.toDomain(): WorkoutSessionExercise {
    return WorkoutSessionExercise(
        sessionId = sessionId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        sets = sets,
        reps = reps,
        weight = weight,
        position = position,
        completedSets = completedSets
    )
}

fun WorkoutSessionExercise.toEntity(): WorkoutSessionExerciseEntity {
    return WorkoutSessionExerciseEntity(
        sessionId = sessionId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        sets = sets,
        reps = reps,
        weight = weight,
        position = position,
        completedSets = completedSets
    )
}