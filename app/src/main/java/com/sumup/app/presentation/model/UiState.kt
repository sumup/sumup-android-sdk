package com.sumup.app.presentation.model

import com.sumup.app.domain.model.CheckoutConfiguration
import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.MerchantInfo
import com.sumup.app.domain.model.OfflineSession
import com.sumup.app.domain.model.SdkResult

internal data class UiState(
    val amountInCents: Long = 0L,
    val checkoutConfiguration: CheckoutConfiguration = CheckoutConfiguration(),
    val offlineSession: OfflineSession = OfflineSession.Inactive,
    val merchantInfo: MerchantInfo? = null,
    val isLoggedIn: Boolean = false,
    val connectedReader: ConnectedReader? = null,
    val isTipOnCardReaderAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val showResultDialog: Boolean = true,
    val latestSdkResult: SdkResult? = null,
    val latestMessage: String? = null,
) {
    val offlineSessionActive: Boolean
        get() = offlineSession is OfflineSession.Active

    /**
     * The charge button is enabled only when the user is logged in and the amount
     * is at least 1 major currency unit (100 cents). Amounts below that are
     * rejected by the SumUp backend.
     */
    val isChargeReady: Boolean
        get() = isLoggedIn && amountInCents >= MIN_CHARGE_AMOUNT_CENTS

    private companion object {
        const val MIN_CHARGE_AMOUNT_CENTS = 100L
    }
}
