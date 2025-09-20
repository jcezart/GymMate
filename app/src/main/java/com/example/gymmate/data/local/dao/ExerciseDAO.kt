package com.example.gymmate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymmate.data.local.entity.CategoryEntity
import com.example.gymmate.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //OnConflictStrategy.Companion.REPLACE
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises ORDER BY exerciseName ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    // Novos métodos para Category
    @Insert(onConflict = OnConflictStrategy.IGNORE) // OnConflictStrategy.Companion.IGNORE
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM categories WHERE name = :name")
    suspend fun deleteCategory(name: String)

    @Query("UPDATE exercises SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateExercisesCategory(oldCategory: String, newCategory: String)

    @Query("DELETE FROM exercises WHERE category = :category")
    suspend fun deleteExercisesByCategory(category: String)
}