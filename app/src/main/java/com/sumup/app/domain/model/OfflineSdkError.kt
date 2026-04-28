package com.sumup.app.domain.model

internal sealed class OfflineSdkError(message: String) : Exception(message) {

    class StartSessionFailed(val resultName: String) :
        OfflineSdkError("Failed to start offline session: $resultName")

    class StopSessionFailed(val resultName: String) :
        OfflineSdkError("Failed to stop offline session: $resultName")

    data object SecurityPatchUpdateFailed :
        OfflineSdkError("Failed to update offline security patch.")

    class UploadFailed(val reason: String?) :
        OfflineSdkError("Failed to upload offline transactions: ${reason ?: "unknown reason"}")
}
