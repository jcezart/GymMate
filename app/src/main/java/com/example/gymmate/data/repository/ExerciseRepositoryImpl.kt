package com.example.gymmate.data.repository

import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.mapper.toDomain
import com.example.gymmate.data.mapper.toEntity
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExerciseRepositoryImpl(
    private val dao: ExerciseDAO
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        dao.getAllExercises().map { list -> list.map { it.toDomain() } }

    override suspend fun addExercise(exercise: Exercise) {
        dao.insertExercise(exercise.toEntity())
    }

    override suspend fun updateExercise(exercise: Exercise) {
        dao.updateExercise(exercise.toEntity())
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        dao.deleteExercise(exercise.toEntity())
    }

    override suspend fun deleteExercisesByCategory(category: String) {
        dao.deleteExercisesByCategory(category)
    }

    override suspend fun moveExercisesToCategory(oldCategory: String, newCategory: String) {
        dao.updateExercisesCategory(oldCategory, newCategory)
    }
}
