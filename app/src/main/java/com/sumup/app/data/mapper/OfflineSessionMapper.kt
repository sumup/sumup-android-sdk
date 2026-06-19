package com.sumup.app.data.mapper

import com.sumup.app.domain.model.OfflineSession
import com.sumup.contract.offline.OfflineSessionState

internal class OfflineSessionMapper {

    fun map(state: OfflineSessionState): OfflineSession = when (state) {
        is OfflineSessionState.NoActiveSession -> OfflineSession.Inactive
        is OfflineSessionState.ActiveSession -> {
            if (state.remainingTimeMillis <= 0) {
                OfflineSession.Incomplete
            } else {
                OfflineSession.Active(
                    remainingTimeMillis = state.remainingTimeMillis,
                    approvedTransactionCount = state.approvedTransactionCount,
                    failedTransactionCount = state.failedTransactionCount,
                )
            }
        }
    }
}
