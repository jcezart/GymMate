package com.example.gymmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
//import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.datasource.local.entity.CategoryEntity
//import com.example.gymmate.data.datasource.local.entity.ExerciseEntity
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.repository.CategoryRepository
import com.example.gymmate.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymMateViewModel(

    //private val exerciseDAO: ExerciseDAO
    private val categoryRepository: CategoryRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
//    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
//    val exercises: StateFlow<List<ExerciseEntity>> get() = _exercises
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> get() = _exercises

//    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
//    val categories: StateFlow<List<CategoryEntity>> get() = _categories

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    init {
        viewModelScope.launch {
//            exerciseDAO.getAllExercises().collect { exerciseList ->
//                _exercises.value = exerciseList
//            }
            exerciseRepository.getAllExercises().collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }
        viewModelScope.launch {
           categoryRepository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
                if (categoryList.isEmpty()) {
                    categoryRepository.addCategory(Category("Workout A"))
                    categoryRepository.addCategory(Category("Workout B"))
                    categoryRepository.addCategory(Category("Workout C"))
                }
        }
       // viewModelScope.launch {
//            exerciseDAO.getAllCategories().collect { categoryList ->
//                _categories.value = categoryList
//                if (categoryList.isEmpty()) {
//                    exerciseDAO.insertCategory(CategoryEntity("Workout A"))
//                    exerciseDAO.insertCategory(CategoryEntity("Workout B"))
//                    exerciseDAO.insertCategory(CategoryEntity("Workout C"))
//                }
//            }
        }
    }

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            //exerciseDAO.insertExercise(exercise)
            exerciseRepository.addExercise(exercise)
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
           // exerciseDAO.updateExercise(exercise)
            exerciseRepository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
//            exerciseDAO.deleteExercise(exercise)
            exerciseRepository.deleteExercise(exercise)
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            //exerciseDAO.insertCategory(category)
            categoryRepository.addCategory(Category(category.nameDb))
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            exerciseRepository.moveExercisesToCategory(oldName, newName)
            categoryRepository.deleteCategory(oldName)
            categoryRepository.addCategory(Category(newName))
//            exerciseDAO.updateExercisesCategory(oldName, newName)
//            exerciseDAO.deleteCategory(oldName)
//            exerciseDAO.insertCategory(CategoryEntity(newName))
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            exerciseRepository.deleteExercisesByCategory(categoryName)
            categoryRepository.deleteCategory(categoryName)
//            exerciseDAO.deleteExercisesByCategory(categoryName)
//            exerciseDAO.deleteCategory(categoryName)
        }
    }
}

//class GymMateViewModelFactory(private val exerciseDAO: ExerciseDAO) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(GymMateViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return GymMateViewModel(exerciseDAO) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}