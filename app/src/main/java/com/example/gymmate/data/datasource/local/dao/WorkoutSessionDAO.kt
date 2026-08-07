package com.example.gymmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionEntity

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insertSession(
        session: WorkoutSessionEntity
    ): Long

    @Query(
        """
        SELECT * FROM workout_sessions
        WHERE status = 'IN_PROGRESS'
        ORDER BY startedAt DESC
        LIMIT 1
        """
    )
    suspend fun getActiveSession(): WorkoutSessionEntity?

    @Query(
        """
        UPDATE workout_sessions
        SET finishedAt = :finishedAt,
            status = 'COMPLETED'
        WHERE id = :sessionId
        """
    )
    suspend fun finishSession(
        sessionId: Long,
        finishedAt: Long
    )
}