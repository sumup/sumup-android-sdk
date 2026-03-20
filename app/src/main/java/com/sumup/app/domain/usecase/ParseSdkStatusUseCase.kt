package com.sumup.app.domain.usecase

import android.os.Bundle
import android.util.Log
import com.sumup.app.domain.model.SdkResult
import com.sumup.merchant.reader.api.SumUpAPI

/**
 * Parses the [Bundle] returned by the SumUp SDK for activities that only surface a
 * status (login, card-reader-page). Produces a [SdkResult.Status] carrying the
 * result code and the human-readable message.
 *
 * Returns [Result.success] with `null` when the caller passes a `null` bundle —
 * for example when the user navigates back without a result.
 */
internal class ParseSdkStatusUseCase {
    operator fun invoke(bundle: Bundle?): Result<SdkResult.Status?> = runCatching {
        if (bundle == null) return@runCatching null

        SdkResult.Status(
            resultCode = bundle.getInt(SumUpAPI.Response.RESULT_CODE),
            message = bundle.getString(SumUpAPI.Response.MESSAGE).orEmpty(),
        )
    }
        .onSuccess { status ->
            Log.d(TAG, "Parsed SDK status: resultCode=${status?.resultCode ?: "none"}")
        }
        .onFailure { error -> Log.w(TAG, "Failed to parse SDK status bundle", error) }

    private companion object {
        const val TAG = "ParseSdkStatusUseCase"
    }
}
