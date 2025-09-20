package com.example.gymmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymmate.data.local.dao.ExerciseDAO
import com.example.gymmate.data.local.entity.CategoryEntity
import com.example.gymmate.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymMateViewModel(private val exerciseDAO: ExerciseDAO) : ViewModel() {
    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> get() = _exercises

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> get() = _categories

    init {
        viewModelScope.launch {
            exerciseDAO.getAllExercises().collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }
        viewModelScope.launch {
            exerciseDAO.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
                if (categoryList.isEmpty()) {
                    exerciseDAO.insertCategory(CategoryEntity("Workout A"))
                    exerciseDAO.insertCategory(CategoryEntity("Workout B"))
                    exerciseDAO.insertCategory(CategoryEntity("Workout C"))
                }
            }
        }
    }

    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            exerciseDAO.insertExercise(exercise)
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            exerciseDAO.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            exerciseDAO.deleteExercise(exercise)
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            exerciseDAO.insertCategory(category)
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            exerciseDAO.updateExercisesCategory(oldName, newName)
            exerciseDAO.deleteCategory(oldName)
            exerciseDAO.insertCategory(CategoryEntity(newName))
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            exerciseDAO.deleteExercisesByCategory(categoryName)
            exerciseDAO.deleteCategory(categoryName)
        }
    }
}

class GymMateViewModelFactory(private val exerciseDAO: ExerciseDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymMateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GymMateViewModel(exerciseDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}