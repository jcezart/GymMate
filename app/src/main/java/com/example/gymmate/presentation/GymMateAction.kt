package com.example.gymmate.presentation

import com.example.gymmate.domain.model.Exercise

sealed interface GymMateAction {
    data object LoadInitial : GymMateAction
    data class SelectCategory(val name: String) : GymMateAction
    data class AddExercise(val exercise: Exercise) : GymMateAction
    data class UpdateExercise(val exercise: Exercise) : GymMateAction
    data class DeleteExercise(val exercise: Exercise) : GymMateAction
    data class AddCategory(val name: String) : GymMateAction
    data class RenameCategory(val oldName: String, val newName: String) : GymMateAction
    data class DeleteCategory(val name: String) : GymMateAction
    data object DismissError : GymMateAction
}
