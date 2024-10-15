package com.example.gymmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymmate.data.Exercise
import com.example.gymmate.data.ExerciseDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymMateViewModel(private val exerciseDAO: ExerciseDAO): ViewModel() {
    // private val _exercises -> o prefixo _ é uma convenção para indicar que a propriedade é privada e não deve ser acessada diretamente pela UI
    // MutableStateFlow<List<Exercise>>(emptyList()) -> inicializa o fluxo com uma lista vazia de exercícios

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())

    /*val exercises: Propriedade pública que será acessada pela UI para observar as mudanças nos exercícios.
    StateFlow<List<Exercise>>: Define que essa propriedade é um fluxo de estado imutável de uma lista de exercícios.
    get() = _exercises: Retorna o fluxo privado _exercises, mas como um StateFlow imutável.*/

    val exercises: StateFlow<List<Exercise>> get() = _exercises

    init {
        viewModelScope.launch {
            exerciseDAO.getAllExercises().collect { exerciseList ->
            _exercises.value = exerciseList}
        }
    }

    fun addExercise(exercise: Exercise) { //função para adicionar um exercício
        viewModelScope.launch { //coroutine para que, quando ViewModel seja destruído, a corrotina seja automaticamente cancelada
            exerciseDAO.insertExercise(exercise) //chamada do DAO para inserir o exercício no banco de dados
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

