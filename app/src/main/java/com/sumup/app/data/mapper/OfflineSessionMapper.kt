package com.sumup.app.data.mapper

import com.sumup.app.domain.model.OfflineSession
import com.sumup.contract.offline.OfflineSessionState

internal class OfflineSessionMapper {

    fun map(state: OfflineSessionState): OfflineSession = when (state) {
        is OfflineSessionState.NoActiveSession -> OfflineSession.Inactive
        is OfflineSessionState.PendingUpload -> OfflineSession.Incomplete
        is OfflineSessionState.ActiveSession -> {
            val sessionData = state.sessionData
            if (sessionData.remainingTimeMillis <= 0) {
                OfflineSession.Incomplete
            } else {
                OfflineSession.Active(
                    remainingTimeMillis = sessionData.remainingTimeMillis,
                    approvedTransactionCount = sessionData.approvedTransactionCount,
                    failedTransactionCount = sessionData.failedTransactionCount,
                )
            }
        }
    }
}
