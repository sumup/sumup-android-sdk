package com.sumup.app.presentation

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumup.app.domain.model.SdkResult
import com.sumup.app.domain.repository.ReaderSdkRepository
import com.sumup.app.domain.usecase.CreateCheckoutRequestUseCase
import com.sumup.app.domain.usecase.CreateLoginRequestUseCase
import com.sumup.app.domain.usecase.ParsePaymentResultUseCase
import com.sumup.app.domain.usecase.ParseSdkStatusUseCase
import com.sumup.app.presentation.model.SdkActionRequest
import com.sumup.app.presentation.model.UiEvent
import com.sumup.app.presentation.model.UiState
import com.sumup.app.util.CoroutinesDispatcherProvider
import com.sumup.app.util.handleFailure
import com.sumup.app.util.safelySuspend
import com.sumup.merchant.reader.api.SumUpAPI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val createLoginRequestUseCase: CreateLoginRequestUseCase,
    private val createCheckoutRequestUseCase: CreateCheckoutRequestUseCase,
    private val parseSdkStatusUseCase: ParseSdkStatusUseCase,
    private val parsePaymentResultUseCase: ParsePaymentResultUseCase,
    private val readerSdkRepository: ReaderSdkRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    init {
        // The SumUp v7 SDK exposes no listener for reader connection state, so we
        // poll it via the repository and reflect changes in the UI as soon as they
        // happen rather than waiting for the next onResume / Activity result.
        // WhileSubscribed stops the polling when the UI goes to the background.
        readerSdkRepository.connectedReaderFlow()
            .onEach { reader ->
                _uiState.update { it.copy(connectedReader = reader) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
            .launchIn(viewModelScope)
    }

    //region Welcome screen
    fun onStartClick() {
        viewModelScope.launch {
            val loggedIn = safelySuspend(TAG, "isLoggedIn") { readerSdkRepository.isLoggedIn() }
                .getOrDefault(false)
            if (loggedIn) {
                refreshMerchantStateInternal()
                _events.send(UiEvent.NavigateToCheckout)
                return@launch
            }
            createLoginRequestUseCase(affiliateKey = DEFAULT_AFFILIATE_KEY, accessToken = "")
                .onSuccess { login ->
                    _events.send(UiEvent.ExecuteSdkAction(SdkActionRequest.OpenLogin(login)))
                }
                .handleFailure("Failed to build login request") { showError(it) }
        }
    }

    fun onLoginResult(data: Bundle?) {
        viewModelScope.launch {
            parseSdkStatusUseCase(data)
                .onSuccess { status ->
                    when {
                        status == null -> Unit
                        status.resultCode == SumUpAPI.Response.ResultCode.SUCCESSFUL -> {
                            refreshMerchantStateInternal()
                            _events.send(UiEvent.NavigateToCheckout)
                        }

                        else -> surfaceStatusMessage(status, fallback = "Login failed")
                    }
                }
                .handleFailure("Failed to parse login result") { showError(it) }
        }
    }

    //endregion

    //region Checkout screen

    fun onDigitPressed(digit: Int) {
        val next = (_uiState.value.amountInCents * 10 + digit).coerceAtMost(9_999_999_99L)
        _uiState.update { it.copy(amountInCents = next) }
    }

    fun onDoubleZeroPressed() {
        val next = (_uiState.value.amountInCents * 100).coerceAtMost(9_999_999_99L)
        _uiState.update { it.copy(amountInCents = next) }
    }

    fun onBackspace() {
        _uiState.update { it.copy(amountInCents = it.amountInCents / 10) }
    }

    fun onChargeClick() {
        viewModelScope.launch {
            val state = _uiState.value
            createCheckoutRequestUseCase(
                amountInCents = state.amountInCents,
                currencyCode = state.merchantInfo?.currencyCode,
                configuration = state.checkoutConfiguration,
            )
                .onSuccess { payment ->
                    _events.send(UiEvent.ExecuteSdkAction(SdkActionRequest.StartCheckout(payment)))
                }
                .handleFailure("Failed to build checkout request") { showError(it) }
        }
    }

    fun onOpenCardReaderPageClick() {
        _events.trySend(UiEvent.ExecuteSdkAction(SdkActionRequest.OpenCardReaderPage))
    }

    fun onPaymentResult(data: Bundle?) {
        handleSdkResult(
            parse = { parsePaymentResultUseCase(data) },
            fallbackMessage = "Failed to parse payment result",
            clearAmount = true,
        )
    }

    fun onCardReaderPageResult(data: Bundle?) {
        handleSdkResult(
            parse = { parseSdkStatusUseCase(data) },
            fallbackMessage = "Failed to parse card reader result",
            clearAmount = false,
        )
    }

    fun onResultDismiss() {
        _uiState.update { it.copy(latestSdkResult = null) }
    }

    //endregion

    //region Settings screen

    fun onTippingEnabledChanged(enabled: Boolean) {
        _uiState.update {
            it.copy(checkoutConfiguration = it.checkoutConfiguration.copy(tippingEnabled = enabled))
        }
    }

    fun onTipAmountChanged(decimalText: String) {
        val amountInCents = parseDecimalToCents(decimalText)
        _uiState.update {
            it.copy(
                checkoutConfiguration = it.checkoutConfiguration.copy(
                    tipAmountInCents = amountInCents,
                ),
            )
        }
    }

    fun onShowSuccessScreenChanged(show: Boolean) {
        _uiState.update {
            it.copy(checkoutConfiguration = it.checkoutConfiguration.copy(showSuccessScreen = show))
        }
    }

    fun onShowResultDialogChanged(show: Boolean) {
        _uiState.update { it.copy(showResultDialog = show) }
    }

    fun onSuccessScreenTimeoutChanged(seconds: Int?) {
        val sanitized = seconds?.takeIf { it > 0 }
        _uiState.update {
            it.copy(
                checkoutConfiguration = it.checkoutConfiguration.copy(
                    successScreenTimeoutSeconds = sanitized,
                ),
            )
        }
    }

    fun onStartOfflineSessionClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            safelySuspend(TAG, "startOfflineSession") { readerSdkRepository.startOfflineSession() }
                .onSuccess { refreshOfflineSession() }
                .handleFailure("Failed to start offline session") {
                    showError(
                        it,
                        clearLoading = true
                    )
                }
        }
    }

    fun onEndOfflineSessionClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            safelySuspend(TAG, "stopOfflineSession") { readerSdkRepository.stopOfflineSession() }
                .onSuccess {
                    refreshOfflineSession()
                }
                .handleFailure("Failed to stop offline session") {
                    showError(
                        it,
                        clearLoading = true
                    )
                }
        }
    }

    fun onUpdateSecurityPatchClick() {
        runLoadingTask(
            task = {
                safelySuspend(TAG, "updateOfflineSecurityPatch") {
                    readerSdkRepository.updateOfflineSecurityPatch()
                }
            },
            successMessage = "Security patch updated successfully.",
            failureMessage = "Failed to update security patch.",
        )
    }

    fun onUploadOfflineTransactionsClick() {
        runLoadingTask(
            task = {
                safelySuspend(TAG, "uploadOfflineTransactions") {
                    readerSdkRepository.uploadOfflineTransactions()

                }.onSuccess {
                    refreshOfflineSession()
                }
            },
            successMessage = "Offline transactions uploaded successfully.",
            failureMessage = "Failed to upload offline transactions.",
        )
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            safelySuspend(TAG, "logout") { readerSdkRepository.logout() }
                .onSuccess {
                    _uiState.update { UiState() }
                    _events.send(UiEvent.NavigateToWelcome)
                }
                .handleFailure("Logout failed") { showError(it) }
        }
    }

    //endregion

    //region Shared / host Activity

    fun onMessageShown() {
        _uiState.update { it.copy(latestMessage = null) }
    }

    fun refreshMerchantState() {
        viewModelScope.launch { refreshMerchantStateInternal() }
    }

    fun refreshOfflineSessionState() {
        viewModelScope.launch { refreshOfflineSession() }
    }

    //endregion

    //region Private helpers
    private fun handleSdkResult(
        parse: suspend () -> Result<SdkResult?>,
        fallbackMessage: String,
        clearAmount: Boolean,
    ) {
        viewModelScope.launch {
            refreshMerchantStateInternal()
            parse()
                .onSuccess { result ->
                    if (result == null) return@onSuccess
                    _uiState.update { state ->
                        state.copy(
                            latestSdkResult = result.takeIf { state.showResultDialog },
                            amountInCents = if (clearAmount) 0L else state.amountInCents,
                        )
                    }
                }
                .handleFailure(fallbackMessage) { showError(it) }
        }
    }

    private suspend fun refreshOfflineSession() {
        safelySuspend(TAG, "getCurrentOfflineSession") {
            readerSdkRepository.getCurrentOfflineSession()
        }
            .onSuccess { session ->
                _uiState.update { it.copy(isLoading = false, offlineSession = session) }
            }
            .handleFailure("Failed to refresh offline session") {
                showError(
                    it,
                    clearLoading = true
                )
            }
    }

    private suspend fun refreshMerchantStateInternal() {
        val loggedIn = safelySuspend(TAG, "isLoggedIn") { readerSdkRepository.isLoggedIn() }
            .getOrDefault(false)

        val merchant = if (loggedIn) {
            safelySuspend(TAG, "getCurrentMerchant") {
                readerSdkRepository.getCurrentMerchant()
            }.getOrNull()
        } else {
            null
        }

        val tipOnReader = loggedIn &&
                runCatching { readerSdkRepository.isTipOnCardReaderAvailable() }
                    .getOrDefault(false)

        if (loggedIn) {
            safelySuspend(TAG, "prepareForCheckout") { readerSdkRepository.prepareForCheckout() }
            refreshOfflineSession()
        }

        _uiState.update {
            it.copy(
                isLoggedIn = loggedIn,
                merchantInfo = merchant,
                isTipOnCardReaderAvailable = tipOnReader,
            )
        }
    }

    private fun runLoadingTask(
        task: suspend () -> Result<*>,
        successMessage: String,
        failureMessage: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            task()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, latestMessage = successMessage) }
                }
                .handleFailure(failureMessage) { showError(it, clearLoading = true) }
        }
    }

    private fun showError(message: String, clearLoading: Boolean = false) {
        _uiState.update { state ->
            if (clearLoading) {
                state.copy(isLoading = false, latestMessage = message)
            } else {
                state.copy(latestMessage = message)
            }
        }
    }

    private fun surfaceStatusMessage(status: SdkResult.Status, fallback: String) {
        val message = status.message.takeUnless { it.isBlank() } ?: fallback
        _uiState.update { it.copy(latestMessage = message) }
    }

    private fun parseDecimalToCents(text: String): Long {
        if (text.isEmpty() || text == ".") return 0L
        val parts = text.split('.', limit = 2)
        val whole = parts[0].toLongOrNull() ?: 0L
        val fraction =
            if (parts.size == 2) parts[1].padEnd(2, '0').take(2).toLongOrNull() ?: 0L else 0L
        return whole * 100L + fraction
    }

    //endregion

    private companion object {
        const val TAG = "SampleApp.MainViewModel"

        /**
         * Affiliate key for the sample app.
         * Get yours at https://me.sumup.com/developers by entering your application ID.
         */
        const val DEFAULT_AFFILIATE_KEY = "7ca84f17-84a5-4140-8df6-6ebeed8540fc"
    }
}
