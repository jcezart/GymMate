package com.example.gymmate.data.mapper

import com.example.gymmate.data.datasource.local.entity.WorkoutSessionEntity
import com.example.gymmate.domain.model.WorkoutSession

fun WorkoutSessionEntity.toDomain(): WorkoutSession {
    return WorkoutSession(
        id = id,
        category = category,
        startedAt = startedAt,
        finishedAt = finishedAt,
        status = status
    )
}

fun WorkoutSession.toEntity(): WorkoutSessionEntity {
    return WorkoutSessionEntity(
        id = id,
        category = category,
        startedAt = startedAt,
        finishedAt = finishedAt,
        status = status
    )
}