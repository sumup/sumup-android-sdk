package com.sumup.app.presentation.model

import com.sumup.merchant.reader.api.SumUpLogin
import com.sumup.merchant.reader.api.SumUpPayment

internal sealed interface SdkActionRequest {
    data class OpenLogin(val login: SumUpLogin) : SdkActionRequest
    data class StartCheckout(val payment: SumUpPayment) : SdkActionRequest
    data object OpenCardReaderPage : SdkActionRequest
}
