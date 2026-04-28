package com.sumup.app.domain.usecase

import android.util.Log
import com.sumup.merchant.reader.api.SumUpLogin

internal class CreateLoginRequestUseCase {
    operator fun invoke(affiliateKey: String, accessToken: String): Result<SumUpLogin> = runCatching {
        require(affiliateKey.isNotBlank()) { "Affiliate key is required." }

        val trimmedToken = accessToken.trim()
        val builder = SumUpLogin.builder(affiliateKey.trim())
        if (trimmedToken.isNotBlank()) {
            builder.accessToken(trimmedToken)
        }
        builder.build()
    }
        .onSuccess { Log.d(TAG, "Created SumUpLogin request") }
        .onFailure { error -> Log.w(TAG, "Failed to create SumUpLogin request", error) }

    private companion object {
        const val TAG = "CreateLoginRequestUC"
    }
}
