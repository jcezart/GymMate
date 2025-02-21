package com.example.gymmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymmate.data.Category
import com.example.gymmate.data.Exercise
import com.example.gymmate.data.ExerciseDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymMateViewModel(private val exerciseDAO: ExerciseDAO) : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> get() = _exercises

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

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
                    exerciseDAO.insertCategory(Category("Workout A"))
                    exerciseDAO.insertCategory(Category("Workout B"))
                    exerciseDAO.insertCategory(Category("Workout C"))
                }
            }
        }
    }

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDAO.insertExercise(exercise)
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDAO.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDAO.deleteExercise(exercise)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            exerciseDAO.insertCategory(category)
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            exerciseDAO.updateExercisesCategory(oldName, newName)
            exerciseDAO.deleteCategory(oldName)
            exerciseDAO.insertCategory(Category(newName))
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