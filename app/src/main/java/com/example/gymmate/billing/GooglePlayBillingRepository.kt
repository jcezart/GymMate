package com.example.gymmate.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GooglePlayBillingRepository(
    context: Context
) : BillingRepository {

    private val _premiumState = MutableStateFlow(PremiumUiState())

    override val premiumState: StateFlow<PremiumUiState> =
        _premiumState.asStateFlow()

    private var monthlyProductDetails: ProductDetails? = null
    private var lifetimeProductDetails: ProductDetails? = null

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            when (billingResult.responseCode) {

                BillingClient.BillingResponseCode.OK -> {

                    val receivedPurchases = purchases.orEmpty()

                    receivedPurchases.forEach { purchase ->
                        acknowledgePurchaseIfNeeded(purchase)
                    }

                    val hasPendingPurchase =
                        receivedPurchases.any { purchase ->
                            purchase.purchaseState ==
                                    Purchase.PurchaseState.PENDING
                        }

                    _premiumState.value =
                        _premiumState.value.copy(
                            isPurchasePending = hasPendingPurchase,
                            errorMessage = null
                        )

                    refreshPremiumStatus()
                }

                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    // Usuário cancelou a compra.
                }

                else -> {
                    _premiumState.value =
                        _premiumState.value.copy(
                            errorMessage = billingResult.debugMessage
                        )
                }
            }
        }

    private val billingClient =
        BillingClient.newBuilder(context.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .enableAutoServiceReconnection()
            .build()

    init {
        connectToGooglePlay()
    }

    private fun connectToGooglePlay() {

        billingClient.startConnection(
            object : BillingClientStateListener {

                override fun onBillingSetupFinished(
                    billingResult: BillingResult
                ) {

                    if (
                        billingResult.responseCode ==
                        BillingClient.BillingResponseCode.OK
                    ) {

                        refreshPremiumStatus()
                        loadProductDetails()

                    } else {

                        _premiumState.value =
                            _premiumState.value.copy(
                                isLoading = false,
                                errorMessage = billingResult.debugMessage
                            )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Reconexão automática habilitada.
                }
            }
        )
    }

    override fun restorePurchases() {

        _premiumState.value =
            _premiumState.value.copy(
                isLoading = true,
                errorMessage = null
            )

        if (billingClient.isReady) {

            refreshPremiumStatus()

        } else {

            connectToGooglePlay()
        }
    }

    override fun purchaseMonthly(activity: Activity) {

        val productDetails = monthlyProductDetails

        if (productDetails == null) {

            _premiumState.value =
                _premiumState.value.copy(
                    errorMessage =
                        "Monthly subscription is not available."
                )

            return
        }

        val offerToken =
            productDetails
                .subscriptionOfferDetails
                ?.firstOrNull()
                ?.offerToken

        if (offerToken == null) {

            _premiumState.value =
                _premiumState.value.copy(
                    errorMessage =
                        "Monthly subscription offer is not available."
                )

            return
        }

        launchPurchaseFlow(
            activity = activity,
            productDetails = productDetails,
            offerToken = offerToken
        )
    }

    override fun purchaseLifetime(activity: Activity) {

        val productDetails = lifetimeProductDetails

        if (productDetails == null) {

            _premiumState.value =
                _premiumState.value.copy(
                    errorMessage =
                        "Lifetime purchase is not available."
                )

            return
        }

        val offerToken =
            productDetails
                .oneTimePurchaseOfferDetailsList
                ?.firstOrNull()
                ?.offerToken

        if (offerToken == null) {

            _premiumState.value =
                _premiumState.value.copy(
                    errorMessage =
                        "Lifetime purchase offer is not available."
                )

            return
        }

        launchPurchaseFlow(
            activity = activity,
            productDetails = productDetails,
            offerToken = offerToken
        )
    }

    private fun launchPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ) {

        val productDetailsParams =
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(productDetailsParams)
                )
                .build()

        val billingResult =
            billingClient.launchBillingFlow(
                activity,
                billingFlowParams
            )

        if (
            billingResult.responseCode !=
            BillingClient.BillingResponseCode.OK
        ) {

            _premiumState.value =
                _premiumState.value.copy(
                    errorMessage = billingResult.debugMessage
                )
        }
    }

    private fun refreshPremiumStatus() {

        val subscriptionParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(
                    BillingClient.ProductType.SUBS
                )
                .build()

        billingClient.queryPurchasesAsync(
            subscriptionParams
        ) { billingResult, purchases ->

            if (
                billingResult.responseCode !=
                BillingClient.BillingResponseCode.OK
            ) {

                _premiumState.value =
                    _premiumState.value.copy(
                        isLoading = false,
                        errorMessage = billingResult.debugMessage
                    )

                return@queryPurchasesAsync
            }

            purchases.forEach { purchase ->
                acknowledgePurchaseIfNeeded(purchase)
            }

            val hasMonthlySubscription =
                purchases.any { purchase ->

                    purchase.purchaseState ==
                            Purchase.PurchaseState.PURCHASED &&
                            MONTHLY_PRODUCT_ID in purchase.products
                }

            val hasPendingSubscription =
                purchases.any { purchase ->
                    purchase.purchaseState ==
                            Purchase.PurchaseState.PENDING
                }

            val oneTimeParams =
                QueryPurchasesParams.newBuilder()
                    .setProductType(
                        BillingClient.ProductType.INAPP
                    )
                    .build()

            billingClient.queryPurchasesAsync(
                oneTimeParams
            ) { oneTimeResult, oneTimePurchases ->

                if (
                    oneTimeResult.responseCode !=
                    BillingClient.BillingResponseCode.OK
                ) {

                    _premiumState.value =
                        _premiumState.value.copy(
                            isLoading = false,
                            errorMessage = oneTimeResult.debugMessage
                        )

                    return@queryPurchasesAsync
                }

                oneTimePurchases.forEach { purchase ->
                    acknowledgePurchaseIfNeeded(purchase)
                }

                val hasLifetimePurchase =
                    oneTimePurchases.any { purchase ->

                        purchase.purchaseState ==
                                Purchase.PurchaseState.PURCHASED &&
                                LIFETIME_PRODUCT_ID in purchase.products
                    }

                val hasPendingOneTimePurchase =
                    oneTimePurchases.any { purchase ->
                        purchase.purchaseState ==
                                Purchase.PurchaseState.PENDING
                    }

                _premiumState.value =
                    _premiumState.value.copy(
                        isPro =
                            hasMonthlySubscription ||
                                    hasLifetimePurchase,
                        isPurchasePending =
                            hasPendingSubscription ||
                                    hasPendingOneTimePurchase,
                        isLoading = false,
                        errorMessage = null
                    )
            }
        }
    }

    private fun loadProductDetails() {

        loadMonthlyProductDetails()
        loadLifetimeProductDetails()
    }

    private fun loadMonthlyProductDetails() {

        val product =
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(
                    MONTHLY_PRODUCT_ID
                )
                .setProductType(
                    BillingClient.ProductType.SUBS
                )
                .build()

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(product)
                )
                .build()

        billingClient.queryProductDetailsAsync(
            params
        ) { billingResult, result ->

            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {

                monthlyProductDetails =
                    result.productDetailsList
                        .firstOrNull()

                val formattedPrice =
                    monthlyProductDetails
                        ?.subscriptionOfferDetails
                        ?.firstOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()
                        ?.formattedPrice

                _premiumState.value =
                    _premiumState.value.copy(
                        monthlyPrice = formattedPrice
                    )
            }
        }
    }

    private fun loadLifetimeProductDetails() {

        val product =
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(
                    LIFETIME_PRODUCT_ID
                )
                .setProductType(
                    BillingClient.ProductType.INAPP
                )
                .build()

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(product)
                )
                .build()

        billingClient.queryProductDetailsAsync(
            params
        ) { billingResult, result ->

            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {

                lifetimeProductDetails =
                    result.productDetailsList
                        .firstOrNull()

                val formattedPrice =
                    lifetimeProductDetails
                        ?.oneTimePurchaseOfferDetailsList
                        ?.firstOrNull()
                        ?.formattedPrice

                _premiumState.value =
                    _premiumState.value.copy(
                        lifetimePrice = formattedPrice
                    )
            }
        }
    }

    private fun acknowledgePurchaseIfNeeded(
        purchase: Purchase
    ) {

        if (
            purchase.purchaseState !=
            Purchase.PurchaseState.PURCHASED ||
            purchase.isAcknowledged
        ) {
            return
        }

        val params =
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(
                    purchase.purchaseToken
                )
                .build()

        billingClient.acknowledgePurchase(
            params
        ) { billingResult ->

            if (
                billingResult.responseCode !=
                BillingClient.BillingResponseCode.OK
            ) {

                _premiumState.value =
                    _premiumState.value.copy(
                        errorMessage =
                            billingResult.debugMessage
                    )
            }
        }
    }

    companion object {

        const val MONTHLY_PRODUCT_ID =
            "gymmate_pro_monthly"

        const val LIFETIME_PRODUCT_ID =
            "gymmate_pro_lifetime"
    }
}