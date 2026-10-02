package com.example.gymmate.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface BillingRepository {

    val premiumState: StateFlow<PremiumUiState>

    fun purchaseMonthly(activity: Activity)

    fun purchaseLifetime(activity: Activity)

    fun restorePurchases()
}