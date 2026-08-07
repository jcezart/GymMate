package com.example.gymmate.data.repository

import com.example.gymmate.data.datasource.local.dao.WorkoutSessionDao
import com.example.gymmate.data.datasource.local.dao.WorkoutSessionExerciseDao
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionEntity
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionExerciseEntity
import com.example.gymmate.data.mapper.toDomain
import com.example.gymmate.data.mapper.toEntity
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.model.ExerciseHistory
import com.example.gymmate.domain.model.WorkoutSession
import com.example.gymmate.domain.model.WorkoutSessionExercise
import com.example.gymmate.domain.repository.WorkoutSessionRepository

class WorkoutSessionRepositoryImpl(
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutSessionExerciseDao: WorkoutSessionExerciseDao
) : WorkoutSessionRepository {

    override suspend fun startSession(
        category: String,
        exercises: List<Exercise>
    ): WorkoutSession {

        val session = WorkoutSessionEntity(
            category = category,
            startedAt = System.currentTimeMillis(),
            status = "IN_PROGRESS"
        )

        val sessionId = workoutSessionDao.insertSession(session)

        val sessionExercises = exercises.map { exercise ->
            WorkoutSessionExerciseEntity(
                sessionId = sessionId,
                exerciseId = exercise.id,
                exerciseName = exercise.exerciseName,
                sets = exercise.sets,
                reps = exercise.reps,
                weight = exercise.weight,
                position = exercise.position,
                completedSets = 0
            )
        }

        workoutSessionExerciseDao.insertExercises(sessionExercises)

        return session.copy(id = sessionId).toDomain()
    }

    override suspend fun getActiveSession(): WorkoutSession? {
        return workoutSessionDao
            .getActiveSession()
            ?.toDomain()
    }

    override suspend fun getSessionExercises(
        sessionId: Long
    ): List<WorkoutSessionExercise> {
        return workoutSessionExerciseDao
            .getExercisesBySession(sessionId)
            .map { it.toDomain() }
    }

    override suspend fun saveSessionExercise(
        exercise: WorkoutSessionExercise
    ) {
        workoutSessionExerciseDao.upsertExercise(
            exercise.toEntity()
        )
    }

    override suspend fun finishSession(
        sessionId: Long
    ) {
        workoutSessionDao.finishSession(
            sessionId = sessionId,
            finishedAt = System.currentTimeMillis()
        )
    }

    override suspend fun getExerciseHistory(
        exerciseId: String
    ): List<ExerciseHistory> {
        return workoutSessionExerciseDao
            .getExerciseHistory(exerciseId)
            .map { item ->
                ExerciseHistory(
                    sessionId = item.sessionId,
                    performedAt = item.performedAt,
                    exerciseName = item.exerciseName,
                    sets = item.sets,
                    reps = item.reps,
                    weight = item.weight,
                    completedSets = item.completedSets
                )
            }
    }

    override suspend fun getPersonalRecord(
        exerciseId: String
    ): Float? {
        return workoutSessionExerciseDao
            .getPersonalRecord(exerciseId)
    }
}