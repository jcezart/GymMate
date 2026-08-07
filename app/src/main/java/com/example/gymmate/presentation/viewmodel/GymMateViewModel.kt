package com.example.gymmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.domain.model.WorkoutSessionExercise
import com.example.gymmate.domain.repository.CategoryRepository
import com.example.gymmate.domain.repository.ExerciseRepository
import com.example.gymmate.presentation.GymMateAction
import com.example.gymmate.presentation.GymMateUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.gymmate.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.first

class GymMateViewModel(
    private val categoryRepository: CategoryRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymMateUiState(isLoading = true))
    val uiState: StateFlow<GymMateUiState> = _uiState.asStateFlow()

    private var activeSessionChecked = false


    init {
        loadInitialData()
    }

    fun dispatch(action: GymMateAction) {
        when (action) {
            is GymMateAction.LoadInitial -> loadInitialData()
            is GymMateAction.SelectCategory -> handleSelectCategory(action.name)
            is GymMateAction.AddExercise -> handleAddExercise(action.exercise)
            is GymMateAction.UpdateExercise -> handleUpdateExercise(action.exercise)
            is GymMateAction.ReorderExercises -> handleReorderExercises(action.exercises)
            is GymMateAction.DeleteExercise -> handleDeleteExercise(action.exercise)
            is GymMateAction.AddCategory -> handleAddCategory(action.name)
            is GymMateAction.RenameCategory -> handleRenameCategory(action.oldName, action.newName)
            is GymMateAction.DeleteCategory -> handleDeleteCategory(action.name)
            is GymMateAction.DismissError -> handleDismissError()
            is GymMateAction.StartWorkout -> handleStartWorkout()
            is GymMateAction.FinishWorkout -> handleFinishWorkout()
            is GymMateAction.UpdateCompletedSets -> handleUpdateCompletedSets(
                                                        exerciseId = action.exerciseId,
                                                        completedSets = action.completedSets)
            is GymMateAction.OpenExerciseHistory -> handleOpenExerciseHistory(action.exerciseId)
            is GymMateAction.CloseExerciseHistory -> handleCloseExerciseHistory()
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

                    if (!activeSessionChecked) {
                        activeSessionChecked = true
                        loadActiveSession()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    private var selectCategoryJob: Job? = null
    private fun handleSelectCategory(name: String) {
        selectCategoryJob?.cancel()
        selectCategoryJob = viewModelScope.launch {
            exerciseRepository.getAllExercises().map { exercises ->
                exercises.filter { it.category == name }}
                .collect { filteredExercises ->
                    _uiState.value = _uiState.value.copy(
                        selectedCategory = name,
                        exercises =  filteredExercises
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

    private fun handleUpdateExercise(exercise: Exercise) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val activeSession = currentState.activeSession

                if (activeSession == null) {

                    // Fora de um treino: atualiza normalmente a ficha-base
                    exerciseRepository.updateExercise(exercise)

                } else {

                    // Durante o treino: atualiza somente a cópia da sessão
                    val sessionExercise = currentState.sessionExercises
                        .firstOrNull { it.exerciseId == exercise.id }
                        ?: return@launch

                    val updatedSessionExercise = sessionExercise.copy(
                        exerciseName = exercise.exerciseName,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        weight = exercise.weight
                    )

                    workoutSessionRepository.saveSessionExercise(
                        updatedSessionExercise
                    )

                    _uiState.value = currentState.copy(

                        sessionExercises = currentState.sessionExercises.map {
                            if (it.exerciseId == exercise.id) {
                                updatedSessionExercise
                            } else {
                                it
                            }
                        },

                        exercises = currentState.exercises.map {
                            if (it.id == exercise.id) {
                                it.copy(
                                    exerciseName = updatedSessionExercise.exerciseName,
                                    sets = updatedSessionExercise.sets,
                                    reps = updatedSessionExercise.reps,
                                    weight = updatedSessionExercise.weight
                                )
                            } else {
                                it
                            }
                        }
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update exercise: ${e.message}"
                )
            }
        }
    }

    private fun handleReorderExercises(exercises: List<Exercise>) {
        viewModelScope.launch {
            try {
                val reorderedExercises = exercises.mapIndexed { index, exercise ->
                    exercise.copy(position = index)
                }

                _uiState.value = _uiState.value.copy(
                    exercises = reorderedExercises
                )

                exerciseRepository.updateExercises(reorderedExercises)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to reorder exercises: ${e.message}"
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

    private fun loadActiveSession() {
        viewModelScope.launch {
            try {
                val activeSession = workoutSessionRepository.getActiveSession()

                if (activeSession != null) {

                    val sessionExercises =
                        workoutSessionRepository.getSessionExercises(activeSession.id)

                    val allExercises =
                        exerciseRepository.getAllExercises().first()

                    val categoryExercises = allExercises.filter {
                        it.category == activeSession.category
                    }

                    _uiState.value = _uiState.value.copy(
                        activeSession = activeSession,
                        sessionExercises = sessionExercises,
                        selectedCategory = activeSession.category,
                        exercises = mergeSessionExercises(
                            exercises = categoryExercises,
                            sessionExercises = sessionExercises
                        )
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load active workout: ${e.message}"
                )
            }
        }
    }

    private fun handleStartWorkout() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val category = currentState.selectedCategory ?: return@launch

                if (currentState.activeSession != null) {
                    return@launch
                }

                val session = workoutSessionRepository.startSession(
                    category = category,
                    exercises = currentState.exercises
                )

                val sessionExercises =
                    workoutSessionRepository.getSessionExercises(session.id)

                _uiState.value = currentState.copy(
                    activeSession = session,
                    sessionExercises = sessionExercises
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to start workout: ${e.message}"
                )
            }
        }
    }

    private fun handleFinishWorkout() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val activeSession = currentState.activeSession ?: return@launch

                val sessionExercisesById =
                    currentState.sessionExercises.associateBy { it.exerciseId }

                val updatedExercises = currentState.exercises.map { exercise ->
                    val sessionExercise = sessionExercisesById[exercise.id]

                    if (sessionExercise != null) {
                        exercise.copy(
                            sets = sessionExercise.sets,
                            reps = sessionExercise.reps,
                            weight = sessionExercise.weight
                        )
                    } else {
                        exercise
                    }
                }

                // Deixa a ficha-base com os últimos valores usados
                exerciseRepository.updateExercises(updatedExercises)

                // Fecha oficialmente a sessão
                workoutSessionRepository.finishSession(activeSession.id)

                _uiState.value = _uiState.value.copy(
                    activeSession = null,
                    sessionExercises = emptyList(),
                    exercises = updatedExercises
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to finish workout: ${e.message}"
                )
            }
        }
    }

    private fun mergeSessionExercises(
        exercises: List<Exercise>,
        sessionExercises: List<WorkoutSessionExercise>
    ): List<Exercise> {

        val sessionById = sessionExercises.associateBy { it.exerciseId }

        return exercises.map { exercise ->
            val sessionExercise = sessionById[exercise.id]

            if (sessionExercise != null) {
                exercise.copy(
                    exerciseName = sessionExercise.exerciseName,
                    sets = sessionExercise.sets,
                    reps = sessionExercise.reps,
                    weight = sessionExercise.weight
                )
            } else {
                exercise
            }
        }
    }

    private fun handleUpdateCompletedSets(
        exerciseId: String,
        completedSets: Int
    ) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                if (currentState.activeSession == null) {
                    return@launch
                }

                val sessionExercise = currentState.sessionExercises
                    .firstOrNull { it.exerciseId == exerciseId }
                    ?: return@launch

                val updatedExercise = sessionExercise.copy(
                    completedSets = completedSets
                )

                workoutSessionRepository.saveSessionExercise(updatedExercise)

                _uiState.value = currentState.copy(
                    sessionExercises = currentState.sessionExercises.map {
                        if (it.exerciseId == exerciseId) {
                            updatedExercise
                        } else {
                            it
                        }
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update completed sets: ${e.message}"
                )
            }
        }
    }

    private fun handleOpenExerciseHistory(exerciseId: String) {
        viewModelScope.launch {
            try {
                val history =
                    workoutSessionRepository.getExerciseHistory(exerciseId)

                val personalRecord =
                    workoutSessionRepository.getPersonalRecord(exerciseId)

                _uiState.value = _uiState.value.copy(
                    exerciseHistory = history,
                    personalRecord = personalRecord,
                    historyExerciseId = exerciseId
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load exercise history: ${e.message}"
                )
            }
        }
    }

    private fun handleCloseExerciseHistory() {
        _uiState.value = _uiState.value.copy(
            exerciseHistory = emptyList(),
            personalRecord = null,
            historyExerciseId = null
        )
    }
}