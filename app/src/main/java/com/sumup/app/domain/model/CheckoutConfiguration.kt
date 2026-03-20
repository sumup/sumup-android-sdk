package com.sumup.app.domain.model

internal data class CheckoutConfiguration(
    val tippingEnabled: Boolean = false,
    val tipAmountInCents: Long = 0L,
    val showSuccessScreen: Boolean = true,
    val showFailedScreen: Boolean = true,
    val successScreenTimeoutSeconds: Int? = null,
)
