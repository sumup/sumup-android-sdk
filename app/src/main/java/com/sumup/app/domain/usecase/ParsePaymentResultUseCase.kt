package com.sumup.app.domain.usecase

import android.os.Bundle
import android.util.Log
import androidx.core.os.BundleCompat
import com.sumup.app.domain.model.SdkResult
import com.sumup.checkout.core.models.TransactionInfo
import com.sumup.merchant.reader.api.SumUpAPI

/**
 * Parses the [Bundle] returned by the SumUp SDK for a checkout result, producing a
 * [SdkResult.Payment] that carries both the status fields and transaction-specific
 * details (transaction code, receipt flag and transaction info).
 *
 * Returns [Result.success] with `null` when the caller passes a `null` bundle — for
 * example when the checkout activity is cancelled without delivering a result.
 */
internal class ParsePaymentResultUseCase {
    operator fun invoke(bundle: Bundle?): Result<SdkResult.Payment?> = runCatching {
        if (bundle == null) return@runCatching null

        val receiptSent = if (bundle.containsKey(SumUpAPI.Response.RECEIPT_SENT)) {
            bundle.getBoolean(SumUpAPI.Response.RECEIPT_SENT)
        } else {
            null
        }

        SdkResult.Payment(
            resultCode = bundle.getInt(SumUpAPI.Response.RESULT_CODE),
            message = bundle.getString(SumUpAPI.Response.MESSAGE).orEmpty(),
            transactionCode = bundle.getString(SumUpAPI.Response.TX_CODE),
            receiptSent = receiptSent,
            // TX_INFO is a TransactionInfo Parcelable in the SDK; toString() is enough
            // for surfacing it in the debug/result dialog of this sample app.
            transactionInfo = BundleCompat.getParcelable(
                bundle,
                SumUpAPI.Response.TX_INFO,
                TransactionInfo::class.java
            )?.toString(),
        )
    }
        .onSuccess { payment ->
            Log.d(
                TAG,
                "Parsed payment result: resultCode=${payment?.resultCode ?: "none"}, " +
                    "txCode=${payment?.transactionCode ?: "none"}",
            )
        }
        .onFailure { error -> Log.w(TAG, "Failed to parse payment result bundle", error) }

    private companion object {
        const val TAG = "ParsePaymentResultUC"
    }
}
