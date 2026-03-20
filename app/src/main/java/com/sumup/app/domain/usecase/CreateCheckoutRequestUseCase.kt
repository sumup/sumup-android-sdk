package com.sumup.app.domain.usecase

import android.util.Log
import com.sumup.app.domain.model.CheckoutConfiguration
import com.sumup.app.domain.repository.ReaderSdkRepository
import com.sumup.merchant.reader.api.SumUpPayment
import java.math.BigDecimal
import java.util.UUID

internal class CreateCheckoutRequestUseCase(
    private val readerSdkRepository: ReaderSdkRepository,
) {
    suspend operator fun invoke(
        amountInCents: Long,
        currencyCode: String?,
        configuration: CheckoutConfiguration,
    ): Result<SumUpPayment> = runCatching {
        require(amountInCents > 0L) { "Amount must be greater than zero." }

        val baseAmount = BigDecimal.valueOf(amountInCents, 2)
        val paymentBuilder = SumUpPayment.builder()
            .total(baseAmount)
            .currency(resolveCurrency(currencyCode))
            .title("Reader SDK Sample charge")
            .receiptEmail("customer@mail.com")
            .receiptSMS("+3531234567890")
            .foreignTransactionId(UUID.randomUUID().toString())
            .configureRetryPolicy(2_000, 60_000, true)
            // optional: Add metadata
            .addAdditionalInfo("AccountId", "taxi0334")
            .addAdditionalInfo("From", "Paris")
            .addAdditionalInfo("To", "Berlin")

        // Tip handling:
        //  - If the reader can collect the tip on its own screen, delegate to it
        //    via tipOnCardReader(). The customer picks the tip on the reader and
        //    the amount is returned as part of the transaction response.
        //  - Otherwise, fall back to a device-side tip passed through `.tip(...)`
        //    (ignored by the SDK when `tipOnCardReader()` is also set; see
        //    README: "ignored if `tipOnCardReader()` is present").
        if (configuration.tippingEnabled) {
            if (readerSdkRepository.isTipOnCardReaderAvailable()) {
                paymentBuilder.tipOnCardReader()
            } else if (configuration.tipAmountInCents > 0L) {
                paymentBuilder.tip(BigDecimal.valueOf(configuration.tipAmountInCents, 2))
            }
        }

        if (!configuration.showSuccessScreen) {
            paymentBuilder.skipSuccessScreen()
        } else {
            configuration.successScreenTimeoutSeconds
                ?.takeIf { it > 0 }
                ?.let { paymentBuilder.successScreenTimeout(it) }
        }
        if (!configuration.showFailedScreen) {
            paymentBuilder.skipFailedScreen()
        }

        paymentBuilder.build()
    }
        .onSuccess { Log.d(TAG, "Created checkout request for amount=$amountInCents cents") }
        .onFailure { error ->
            Log.w(TAG, "Failed to create checkout request (amount=$amountInCents cents)", error)
        }

    private fun resolveCurrency(currencyCode: String?): SumUpPayment.Currency {
        val normalized = currencyCode?.trim()?.uppercase()
        if (normalized.isNullOrEmpty()) return DEFAULT_CURRENCY
        return runCatching { SumUpPayment.Currency.valueOf(normalized) }
            .onFailure {
                Log.w(TAG, "Unsupported merchant currency '$normalized', falling back to $DEFAULT_CURRENCY")
            }
            .getOrDefault(DEFAULT_CURRENCY)
    }

    private companion object {
        const val TAG = "CreateCheckoutRequestUC"
        val DEFAULT_CURRENCY: SumUpPayment.Currency = SumUpPayment.Currency.EUR
    }
}
