package com.example.gymmate.billing

import android.app.Activity
import androidx.lifecycle.ViewModel

class PremiumViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {

    val premiumState = billingRepository.premiumState

    fun purchaseMonthly(activity: Activity) {
        billingRepository.purchaseMonthly(activity)
    }

    fun purchaseLifetime(activity: Activity) {
        billingRepository.purchaseLifetime(activity)
    }

    fun restorePurchases() {
        billingRepository.restorePurchases()
    }
}