package com.sumup.app.domain.model

internal sealed class OfflineSession {

    data object Inactive : OfflineSession()

    data class Active(
        val remainingTimeMillis: Long,
        val approvedTransactionCount: Int,
        val failedTransactionCount: Int,
        val maxTransactionCount: Int = MAX_TRANSACTIONS_PER_SESSION,
    ) : OfflineSession()

    /**
     * The session is incomplete when offline is disabled but the transaction batch hasn't been
     * uploaded. In that case, the SDK returns that the session is active, but the remaining
     * time is 0. The user must upload the batch manually.
     */
    data object Incomplete: OfflineSession()

    companion object {
        /**
         * The per-session transaction cap enforced by the SumUp Reader SDK's offline mode.
         *
         * The SDK does not expose this value publicly, so it is duplicated here purely for
         * UI progress indicators (e.g. "X / 75"). If the SDK ever changes the cap, update
         * this constant to keep the displayed denominator in sync — otherwise the UI will
         * silently show the wrong limit.
         */
        const val MAX_TRANSACTIONS_PER_SESSION: Int = 75
    }
}
