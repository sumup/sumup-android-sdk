package com.sumup.app.presentation.model

internal sealed interface UiEvent {
    data class ExecuteSdkAction(val request: SdkActionRequest) : UiEvent
    data object NavigateToCheckout : UiEvent
    data object NavigateToWelcome : UiEvent
}
