package com.example.gymmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.repository.CategoryRepository
import com.example.gymmate.domain.repository.ExerciseRepository
import com.example.gymmate.presentation.GymMateAction
import com.example.gymmate.presentation.GymMateUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GymMateViewModel(
    private val categoryRepository: CategoryRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymMateUiState(isLoading = true))
    val uiState: StateFlow<GymMateUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    class FilmesState(
        val isLoading: Boolean = false,
        val filmes: List<String> = emptyList(),
        val errorMessage: String? = null
    )
    private fun getFilmes(){
        val loading = true
        // getFilmesAPi()

    }

    fun dispatch(action: GymMateAction) {
        when (action) {
            is GymMateAction.LoadInitial -> loadInitialData()
            is GymMateAction.SelectCategory -> handleSelectCategory(action.name)
            is GymMateAction.AddExercise -> handleAddExercise(action.exercise)
            is GymMateAction.UpdateExercise -> handleUpdateExercise(action.exercise)
            is GymMateAction.DeleteExercise -> handleDeleteExercise(action.exercise)
            is GymMateAction.AddCategory -> handleAddCategory(action.name)
            is GymMateAction.RenameCategory -> handleRenameCategory(action.oldName, action.newName)
            is GymMateAction.DeleteCategory -> handleDeleteCategory(action.name)
            is GymMateAction.DismissError -> handleDismissError()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                combine(
                    categoryRepository.getAllCategories(),
                    exerciseRepository.getAllExercises()
                ) { categories, exercises ->
                    Pair(categories, exercises)
                }.collect { (categories, exercises) ->
                    // Se não houver categorias, criar as padrão
                    if (categories.isEmpty()) {
                        categoryRepository.addCategory(Category("Workout A"))
                        categoryRepository.addCategory(Category("Workout B"))
                        categoryRepository.addCategory(Category("Workout C"))
                        return@collect
                    }

                    val selectedCategory = _uiState.value.selectedCategory
                        ?: categories.firstOrNull()?.name

                    // Filtrar exercícios pela categoria selecionada
                    val filteredExercises = selectedCategory?.let { category ->
                        exercises.filter { it.category == category }
                    } ?: emptyList()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        exercises = filteredExercises,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    private fun handleSelectCategory(name: String) {
        viewModelScope.launch {
            try {
                val allExercises = exerciseRepository.getAllExercises()
                allExercises.collect { exercises ->
                    val filteredExercises = exercises.filter { it.category == name }
                    _uiState.value = _uiState.value.copy(
                        selectedCategory = name,
                        exercises = filteredExercises
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to select category: ${e.message}"
                )
            }
        }
    }

    private fun handleAddExercise(exercise: com.example.gymmate.domain.model.Exercise) {
        viewModelScope.launch {
            try {
                exerciseRepository.addExercise(exercise)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add exercise: ${e.message}"
                )
            }
        }
    }

    private fun handleUpdateExercise(exercise: com.example.gymmate.domain.model.Exercise) {
        viewModelScope.launch {
            try {
                exerciseRepository.updateExercise(exercise)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update exercise: ${e.message}"
                )
            }
        }
    }

    private fun handleDeleteExercise(exercise: com.example.gymmate.domain.model.Exercise) {
        viewModelScope.launch {
            try {
                exerciseRepository.deleteExercise(exercise)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete exercise: ${e.message}"
                )
            }
        }
    }

    private fun handleAddCategory(name: String) {
        viewModelScope.launch {
            try {
                categoryRepository.addCategory(Category(name))
                // Após adicionar, selecionar a nova categoria
                _uiState.value = _uiState.value.copy(
                    selectedCategory = name
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add category: ${e.message}"
                )
            }
        }
    }

    private fun handleRenameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            try {
                exerciseRepository.moveExercisesToCategory(oldName, newName)
                categoryRepository.deleteCategory(oldName)
                categoryRepository.addCategory(Category(newName))

                // Se a categoria renomeada estava selecionada, atualizar para o novo nome
                if (_uiState.value.selectedCategory == oldName) {
                    _uiState.value = _uiState.value.copy(
                        selectedCategory = newName
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to rename category: ${e.message}"
                )
            }
        }
    }

    private fun handleDeleteCategory(name: String) {
        viewModelScope.launch {
            try {
                exerciseRepository.deleteExercisesByCategory(name)
                categoryRepository.deleteCategory(name)

                // Se a categoria deletada estava selecionada, selecionar a primeira disponível
                if (_uiState.value.selectedCategory == name) {
                    val newSelectedCategory = _uiState.value.categories
                        .firstOrNull { it.name != name }?.name

                    _uiState.value = _uiState.value.copy(
                        selectedCategory = newSelectedCategory,
                        exercises = emptyList()
                    )

                    // Se houver nova categoria selecionada, carregar seus exercícios
                    newSelectedCategory?.let { handleSelectCategory(it) }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete category: ${e.message}"
                )
            }
        }
    }

    private fun handleDismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}