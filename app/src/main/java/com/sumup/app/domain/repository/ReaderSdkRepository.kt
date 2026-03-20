package com.sumup.app.domain.repository

import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.MerchantInfo
import com.sumup.app.domain.model.OfflineSession
import kotlinx.coroutines.flow.Flow

internal interface ReaderSdkRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentMerchant(): MerchantInfo?
    suspend fun logout()
    suspend fun prepareForCheckout()
    suspend fun isTipOnCardReaderAvailable(): Boolean

    /**
     * Polls the SumUp SDK for the currently connected card reader at [intervalMs]
     * and emits a new value whenever the connection state changes.
     *
     * The SumUp v7 SDK does not expose any listener / observer for reader status;
     * polling is the only supported way to keep the UI in sync while the user
     * stays on a screen. This is the single source of truth for reader status —
     * there is intentionally no synchronous getter.
     */
    fun connectedReaderFlow(intervalMs: Long = 2_000L): Flow<ConnectedReader?>

    suspend fun startOfflineSession()
    suspend fun stopOfflineSession()
    suspend fun updateOfflineSecurityPatch()
    suspend fun uploadOfflineTransactions()
    suspend fun getCurrentOfflineSession(): OfflineSession
}
