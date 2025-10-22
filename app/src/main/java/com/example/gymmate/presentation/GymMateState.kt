package com.example.gymmate.presentation

import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.model.Exercise

data class GymMateUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val errorMessage: String? = null
)