package com.example.gymmate.domain.repository

import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.model.ExerciseHistory
import com.example.gymmate.domain.model.WorkoutSession
import com.example.gymmate.domain.model.WorkoutSessionExercise

interface WorkoutSessionRepository {

    suspend fun startSession(
        category: String,
        exercises: List<Exercise>
    ): WorkoutSession

    suspend fun getActiveSession(): WorkoutSession?

    suspend fun getSessionExercises(
        sessionId: Long
    ): List<WorkoutSessionExercise>

    suspend fun saveSessionExercise(
        exercise: WorkoutSessionExercise
    )

    suspend fun finishSession(
        sessionId: Long
    )

    suspend fun getExerciseHistory(
        exerciseId: String
    ): List<ExerciseHistory>

    suspend fun getPersonalRecord(
        exerciseId: String
    ): Float?
}