package com.example.gymmate.billing

data class PremiumUiState(
    val isPro: Boolean = false,
    val isLoading: Boolean = true,
    val isPurchasePending: Boolean = false,
    val monthlyPrice: String? = null,
    val lifetimePrice: String? = null,
    val errorMessage: String? = null
)