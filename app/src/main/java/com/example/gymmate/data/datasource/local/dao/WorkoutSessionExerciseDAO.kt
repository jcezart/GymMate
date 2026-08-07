package com.example.gymmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionExerciseEntity

@Dao
interface WorkoutSessionExerciseDao {

    @Insert
    suspend fun insertExercises(
        exercises: List<WorkoutSessionExerciseEntity>
    )

    @Upsert
    suspend fun upsertExercise(
        exercise: WorkoutSessionExerciseEntity
    )

    @Query(
        """
        SELECT * FROM workout_session_exercises
        WHERE sessionId = :sessionId
        ORDER BY position ASC
        """
    )
    suspend fun getExercisesBySession(
        sessionId: Long
    ): List<WorkoutSessionExerciseEntity>

    @Query(
        """
        SELECT 
            wse.sessionId AS sessionId,
            ws.startedAt AS performedAt,
            wse.exerciseName AS exerciseName,
            wse.sets AS sets,
            wse.reps AS reps,
            wse.weight AS weight,
            wse.completedSets AS completedSets
        FROM workout_session_exercises wse
        INNER JOIN workout_sessions ws
            ON ws.id = wse.sessionId
        WHERE wse.exerciseId = :exerciseId
          AND ws.status = 'COMPLETED'
        ORDER BY ws.startedAt DESC
        """
    )
    suspend fun getExerciseHistory(
        exerciseId: String
    ): List<ExerciseHistoryItem>

    @Query(
        """
        SELECT MAX(wse.weight)
        FROM workout_session_exercises wse
        INNER JOIN workout_sessions ws
            ON ws.id = wse.sessionId
        WHERE wse.exerciseId = :exerciseId
          AND ws.status = 'COMPLETED'
        """
    )
    suspend fun getPersonalRecord(
        exerciseId: String
    ): Float?
}

data class ExerciseHistoryItem(
    val sessionId: Long,
    val performedAt: Long,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val completedSets: Int
)