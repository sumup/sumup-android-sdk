package com.sumup.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sumup.app.R
import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.MerchantInfo
import com.sumup.app.domain.model.ReaderType
import com.sumup.app.presentation.model.UiState
import com.sumup.app.presentation.screen.components.AmountDisplay
import com.sumup.app.presentation.screen.components.CheckoutResultDialog
import com.sumup.app.presentation.screen.components.Keypad
import com.sumup.app.presentation.theme.AppNameStyle
import com.sumup.app.presentation.theme.BadgeTextStyle
import com.sumup.app.presentation.theme.ButtonLabelStyle
import com.sumup.app.presentation.theme.AppTheme
import com.sumup.app.presentation.theme.LightSand
import com.sumup.app.presentation.theme.MerchantCodeStyle
import com.sumup.app.presentation.theme.ReaderGreen
import com.sumup.app.presentation.theme.SandDivider
import com.sumup.app.presentation.theme.SparkBlue
import com.sumup.app.presentation.theme.SystemBarsAppearance
import com.sumup.app.presentation.theme.Violet

private const val SERIAL_SUFFIX_LENGTH = 3

@Composable
internal fun CheckoutScreen(
    uiState: UiState,
    onSettingsClick: () -> Unit,
    onReaderBadgeClick: () -> Unit,
    onDigitPressed: (Int) -> Unit,
    onBackspace: () -> Unit,
    onDoubleZeroPressed: () -> Unit,
    onChargeClick: () -> Unit,
    onResultDismiss: () -> Unit,
) {
    SystemBarsAppearance(darkIcons = false)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SparkBlue)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            CheckoutTopBar(
                merchantCode = uiState.merchantInfo?.merchantCode,
                connectedReader = uiState.connectedReader,
                isOffline = uiState.offlineSessionActive,
                onReaderBadgeClick = onReaderBadgeClick,
                onSettingsClick = onSettingsClick,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                AmountDisplay(
                    amountInCents = uiState.amountInCents,
                    currencyCode = uiState.merchantInfo?.currencyCode,
                )

                Spacer(modifier = Modifier.height(48.dp))

                Keypad(
                    onDigitPressed = onDigitPressed,
                    onBackspace = onBackspace,
                    onDoubleZeroPressed = onDoubleZeroPressed,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val isChargeReady = uiState.isChargeReady
                Button(
                    onClick = onChargeClick,
                    enabled = isChargeReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightSand,
                        contentColor = Violet,
                        disabledContainerColor = LightSand.copy(alpha = 0.5f),
                        disabledContentColor = Violet.copy(alpha = 0.5f),
                    ),
                ) {
                    Text(
                        text = if (uiState.amountInCents > 0) {
                            stringResource(R.string.checkout_charge_button)
                        } else {
                            stringResource(R.string.checkout_enter_amount)
                        },
                        style = ButtonLabelStyle,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // SDK result dialog
    uiState.latestSdkResult?.let { result ->
        CheckoutResultDialog(
            result = result,
            onDismiss = onResultDismiss,
        )
    }
}

// ---- Checkout Top Bar -------------------------------------------------------

@Composable
private fun CheckoutTopBar(
    merchantCode: String?,
    connectedReader: ConnectedReader?,
    isOffline: Boolean,
    onReaderBadgeClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(SparkBlue)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.top_bar_app_name),
                    style = AppNameStyle,
                )
                Text(
                    text = merchantCode ?: "—",
                    style = MerchantCodeStyle,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Reader badge — shows reader name, battery %, and the last three
            // digits of the serial number, separated by vertical bars.
            val badgeText = connectedReader?.let(::readerBadgeText)
                ?: stringResource(R.string.top_bar_no_reader)
            Row(
                modifier = Modifier
                    .clickable(onClick = onReaderBadgeClick)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (connectedReader != null) ReaderGreen else Color(0xFFBBBBBB),
                            shape = CircleShape,
                        ),
                )
                Text(
                    text = badgeText,
                    style = BadgeTextStyle,
                )
            }

            // Offline badge
            if (isOffline) {
                Row(
                    modifier = Modifier
                        .border(1.dp, SandDivider, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = stringResource(R.string.top_bar_offline),
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFFF9800),
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_gear),
                    contentDescription = stringResource(R.string.top_bar_settings),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
            }
        }
    }
}

private fun readerBadgeText(reader: ConnectedReader): String = buildList {
    add(reader.readerType.displayName)
    reader.lastKnownBatteryPercentage?.let { add("$it%") }
    reader.serialNumber
        .takeIf { it.length >= SERIAL_SUFFIX_LENGTH }
        ?.takeLast(SERIAL_SUFFIX_LENGTH)
        ?.let{ add("#$it")}
}.joinToString(separator = " | ")

@Preview(showBackground = true)
@Composable
private fun CheckoutScreenPreview() {
    AppTheme {
        CheckoutScreen(
            uiState = UiState(
                isLoggedIn = true,
                merchantInfo = MerchantInfo(merchantCode = "MPOS94Z3", currencyCode = "EUR"),
                amountInCents = 450,
                connectedReader = ConnectedReader(
                    readerType = ReaderType.SOLO_LITE,
                    serialNumber = "SN12345421",
                    lastKnownBatteryPercentage = 87,
                ),
            ),
            onSettingsClick = {},
            onReaderBadgeClick = {},
            onDigitPressed = {},
            onBackspace = {},
            onDoubleZeroPressed = {},
            onChargeClick = {},
            onResultDismiss = {},
        )
    }
}
