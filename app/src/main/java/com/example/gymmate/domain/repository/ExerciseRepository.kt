package com.example.gymmate.domain.repository

import com.example.gymmate.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun addExercise(exercise: Exercise)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)

    suspend fun deleteExercisesByCategory(category: String)
    suspend fun moveExercisesToCategory(oldCategory: String, newCategory: String)
}