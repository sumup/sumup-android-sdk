package com.sumup.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sumup.app.presentation.model.SdkActionRequest
import com.sumup.app.presentation.model.UiEvent
import com.sumup.app.presentation.model.UiState
import com.sumup.app.presentation.navigation.Route
import com.sumup.app.presentation.screen.CheckoutScreen
import com.sumup.app.presentation.screen.SettingsScreen
import com.sumup.app.presentation.screen.WelcomeScreen
import com.sumup.app.presentation.theme.AppTheme
import com.sumup.merchant.reader.api.SumUpAPI
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ReaderApp(
                    viewModel = viewModel,
                    onSdkAction = ::handleSdkAction,
                )
            }
        }
    }

    @Deprecated("Required by SumUp SDK which uses startActivityForResult")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_LOGIN -> viewModel.onLoginResult(data?.extras)
            REQUEST_CODE_CARD_READER_PAGE -> viewModel.onCardReaderPageResult(data?.extras)
            REQUEST_CODE_PAYMENT -> viewModel.onPaymentResult(data?.extras)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshMerchantState()
    }

    private fun handleSdkAction(actionRequest: SdkActionRequest) {
        when (actionRequest) {
            is SdkActionRequest.OpenLogin ->
                SumUpAPI.openLoginActivity(this, actionRequest.login, REQUEST_CODE_LOGIN)
            is SdkActionRequest.StartCheckout ->
                SumUpAPI.checkout(this, actionRequest.payment, REQUEST_CODE_PAYMENT)
            SdkActionRequest.OpenCardReaderPage ->
                SumUpAPI.openCardReaderPage(this, REQUEST_CODE_CARD_READER_PAGE)
        }
    }

    private companion object {
        private const val REQUEST_CODE_LOGIN = 101
        private const val REQUEST_CODE_PAYMENT = 102
        private const val REQUEST_CODE_CARD_READER_PAGE = 103
    }
}

@Composable
private fun ReaderApp(
    viewModel: MainViewModel,
    onSdkAction: (SdkActionRequest) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.latestMessage) {
        uiState.latestMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is UiEvent.ExecuteSdkAction -> onSdkAction(event.request)

                    UiEvent.NavigateToCheckout -> {
                        navController.navigate(Route.CHECKOUT) {
                            popUpTo(Route.WELCOME) { inclusive = true }
                        }
                    }

                    UiEvent.NavigateToWelcome -> {
                        navController.navigate(Route.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { _ ->
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            uiState = uiState,
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    uiState: UiState,
) {
    NavHost(
        navController = navController,
        startDestination = Route.WELCOME,
    ) {
        composable(Route.WELCOME) {
            WelcomeScreen(
                onStartClick = viewModel::onStartClick,
            )
        }

        composable(Route.CHECKOUT) {
            CheckoutScreen(
                uiState = uiState,
                onSettingsClick = { navController.navigate(Route.SETTINGS) },
                onReaderBadgeClick = viewModel::onOpenCardReaderPageClick,
                onDigitPressed = viewModel::onDigitPressed,
                onBackspace = viewModel::onBackspace,
                onDoubleZeroPressed = viewModel::onDoubleZeroPressed,
                onChargeClick = viewModel::onChargeClick,
                onResultDismiss = viewModel::onResultDismiss,
            )
        }

        composable(Route.SETTINGS) {
            LaunchedEffect(Unit) {
                viewModel.refreshOfflineSessionState()
            }
            SettingsScreen(
                uiState = uiState,
                onCloseClick = { navController.popBackStack() },
                onTippingChanged = viewModel::onTippingEnabledChanged,
                onTipAmountChanged = viewModel::onTipAmountChanged,
                onStartOfflineSessionClick = viewModel::onStartOfflineSessionClick,
                onEndOfflineSessionClick = viewModel::onEndOfflineSessionClick,
                onShowSuccessScreenChanged = viewModel::onShowSuccessScreenChanged,
                onSuccessScreenTimeoutChanged = viewModel::onSuccessScreenTimeoutChanged,
                onShowResultDialogChanged = viewModel::onShowResultDialogChanged,
                onUpdateSecurityPatchClick = viewModel::onUpdateSecurityPatchClick,
                onUploadOfflineTransactionsClick = viewModel::onUploadOfflineTransactionsClick,
                onLogoutClick = viewModel::onLogoutClick,
            )
        }
    }
}
