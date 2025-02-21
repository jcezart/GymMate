package com.example.gymmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM exercises ORDER BY exerciseName ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    // Novos métodos para Category
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("DELETE FROM categories WHERE name = :name")
    suspend fun deleteCategory(name: String)

    @Query("UPDATE exercises SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateExercisesCategory(oldCategory: String, newCategory: String)

    @Query("DELETE FROM exercises WHERE category = :category")
    suspend fun deleteExercisesByCategory(category: String)
}