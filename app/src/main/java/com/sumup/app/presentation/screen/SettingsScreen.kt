package com.sumup.app.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sumup.app.R
import com.sumup.app.domain.model.CheckoutConfiguration
import com.sumup.app.domain.model.ConnectedReader
import com.sumup.app.domain.model.MerchantInfo
import com.sumup.app.domain.model.OfflineSession
import com.sumup.app.domain.model.ReaderType
import com.sumup.app.presentation.model.UiState
import com.sumup.app.presentation.theme.AppTheme
import com.sumup.app.presentation.theme.ButtonLabelStyle
import com.sumup.app.presentation.theme.DarkBlue
import com.sumup.app.presentation.theme.LightSand
import com.sumup.app.presentation.theme.ReaderGreen
import com.sumup.app.presentation.theme.SandDivider
import com.sumup.app.presentation.theme.SectionTitleStyle
import com.sumup.app.presentation.theme.SettingsHeadingStyle
import com.sumup.app.presentation.theme.SparkBlue
import com.sumup.app.presentation.theme.SubText
import com.sumup.app.presentation.theme.SystemBarsAppearance
import com.sumup.app.presentation.theme.ToggleSubtitleStyle
import com.sumup.app.presentation.theme.ToggleTitleStyle
import com.sumup.app.presentation.theme.Violet
import java.util.concurrent.TimeUnit

private val CardShape = RoundedCornerShape(16.dp)

@Composable
internal fun SettingsScreen(
    uiState: UiState,
    onCloseClick: () -> Unit,
    onTippingChanged: (Boolean) -> Unit,
    onTipAmountChanged: (String) -> Unit,
    onStartOfflineSessionClick: () -> Unit,
    onEndOfflineSessionClick: () -> Unit,
    onShowSuccessScreenChanged: (Boolean) -> Unit,
    onSuccessScreenTimeoutChanged: (Int?) -> Unit,
    onShowResultDialogChanged: (Boolean) -> Unit,
    onUpdateSecurityPatchClick: () -> Unit,
    onUploadOfflineTransactionsClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    SystemBarsAppearance(darkIcons = true)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightSand)
            .statusBarsPadding(),
    ) {
        SettingsTopBar(onCloseClick = onCloseClick)

        val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp + navBarBottom,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { TippingCard(uiState, onTippingChanged, onTipAmountChanged) }
            item { OfflineModeCard(uiState, onStartOfflineSessionClick, onEndOfflineSessionClick, onUploadOfflineTransactionsClick, onUpdateSecurityPatchClick) }
            item { DevelopmentOptionsCard(uiState, onShowResultDialogChanged, onShowSuccessScreenChanged, onSuccessScreenTimeoutChanged) }
            item { AccountCard(uiState, onLogoutClick) }
        }
    }
}

// ---- Settings Section Cards -------------------------------------------------

@Composable
private fun TippingCard(
    uiState: UiState,
    onTippingChanged: (Boolean) -> Unit,
    onTipAmountChanged: (String) -> Unit,
) {
    SettingsCard {
        SettingsSectionHeader(
            icon = Icons.Default.AttachMoney,
            title = stringResource(R.string.settings_tipping_section),
        )
        val tippingSubtitleRes = when {
            !uiState.isLoggedIn || uiState.connectedReader == null ->
                R.string.settings_tipping_subtitle_no_reader

            uiState.isTipOnCardReaderAvailable ->
                R.string.settings_tipping_subtitle_on_reader

            else ->
                R.string.settings_tipping_subtitle_manual
        }
        SettingsToggleRow(
            title = stringResource(R.string.settings_tipping_title),
            subtitle = stringResource(tippingSubtitleRes),
            checked = uiState.checkoutConfiguration.tippingEnabled,
            onCheckedChange = onTippingChanged,
        )
        if (!uiState.isTipOnCardReaderAvailable) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 44.dp, end = 24.dp),
                color = SandDivider,
            )
            TipAmountField(
                tipAmountInCents = uiState.checkoutConfiguration.tipAmountInCents,
                currencyCode = uiState.merchantInfo?.currencyCode,
                enabled = uiState.checkoutConfiguration.tippingEnabled,
                onAmountChanged = onTipAmountChanged,
            )
        }
    }
}

@Composable
private fun OfflineModeCard(
    uiState: UiState,
    onStartOfflineSessionClick: () -> Unit,
    onEndOfflineSessionClick: () -> Unit,
    onUploadOfflineTransactionsClick: () -> Unit,
    onUpdateSecurityPatchClick: () -> Unit,
) {
    SettingsCard {
        SettingsSectionHeader(
            icon = Icons.Default.Wifi,
            title = stringResource(R.string.settings_offline_mode_section),
        )
        OfflineSessionContent(
            session = uiState.offlineSession,
            onStartClick = onStartOfflineSessionClick,
            onEndClick = onEndOfflineSessionClick,
            onUploadClick = onUploadOfflineTransactionsClick,
            onCheckUpdatesClick = onUpdateSecurityPatchClick,
        )
    }
}

@Composable
private fun DevelopmentOptionsCard(
    uiState: UiState,
    onShowResultDialogChanged: (Boolean) -> Unit,
    onShowSuccessScreenChanged: (Boolean) -> Unit,
    onSuccessScreenTimeoutChanged: (Int?) -> Unit,
) {
    SettingsCard {
        SettingsSectionHeader(
            icon = Icons.Default.Code,
            title = stringResource(R.string.settings_dev_options_section),
        )
        SettingsToggleRow(
            title = stringResource(R.string.settings_result_dialog_title),
            subtitle = stringResource(R.string.settings_result_dialog_subtitle),
            checked = uiState.showResultDialog,
            onCheckedChange = onShowResultDialogChanged,
        )
        HorizontalDivider(
            modifier = Modifier.padding(start = 44.dp, end = 24.dp),
            color = SandDivider,
        )
        SettingsToggleRow(
            title = stringResource(R.string.settings_tx_success_title),
            subtitle = stringResource(R.string.settings_tx_success_subtitle),
            checked = uiState.checkoutConfiguration.showSuccessScreen,
            onCheckedChange = onShowSuccessScreenChanged,
        )
        SuccessScreenTimeoutField(
            timeoutSeconds = uiState.checkoutConfiguration.successScreenTimeoutSeconds,
            enabled = uiState.checkoutConfiguration.showSuccessScreen,
            onTimeoutChanged = onSuccessScreenTimeoutChanged,
        )
    }
}

@Composable
private fun AccountCard(
    uiState: UiState,
    onLogoutClick: () -> Unit,
) {
    SettingsCard {
        SettingsSectionHeader(
            icon = Icons.Default.AccountCircle,
            title = stringResource(R.string.settings_account_section),
        )
        Column(
            modifier = Modifier.padding(
                start = 44.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = uiState.merchantInfo?.merchantCode
                    ?: stringResource(R.string.top_bar_app_name),
                style = ToggleTitleStyle,
                color = DarkBlue,
            )
            Text(
                text = uiState.merchantInfo?.currencyCode ?: "",
                style = ToggleSubtitleStyle,
                color = SubText,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkBlue),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_logout),
                    style = ButtonLabelStyle,
                    color = Color.White,
                )
            }
        }
    }
}

// ---- Settings Top Bar -------------------------------------------------------

@Composable
private fun SettingsTopBar(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = SettingsHeadingStyle,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onCloseClick,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.settings_close),
                modifier = Modifier.size(24.dp),
                tint = DarkBlue,
            )
        }
    }
}

// ---- Reusable Settings Components -------------------------------------------

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = Color.White,
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = SparkBlue,
        )
        Text(
            text = title.uppercase(),
            style = SectionTitleStyle,
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ToggleTitleStyle,
                color = DarkBlue,
            )
            Text(
                text = subtitle,
                style = ToggleSubtitleStyle,
                color = SubText,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SuccessScreenTimeoutField(
    timeoutSeconds: Int?,
    enabled: Boolean,
    onTimeoutChanged: (Int?) -> Unit,
) {
    var text by remember(timeoutSeconds) {
        mutableStateOf(timeoutSeconds?.toString().orEmpty())
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 24.dp, top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_success_timeout_title),
            style = ToggleTitleStyle,
            color = DarkBlue,
        )
        Text(
            text = stringResource(R.string.settings_success_timeout_subtitle),
            style = ToggleSubtitleStyle,
            color = SubText,
        )
        OutlinedTextField(
            value = text,
            onValueChange = { raw ->
                val digits = raw.filter { it.isDigit() }.take(3)
                text = digits
                onTimeoutChanged(digits.toIntOrNull()?.takeIf { it > 0 })
            },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(
                    text = stringResource(R.string.settings_success_timeout_placeholder),
                    style = ToggleSubtitleStyle,
                    color = SubText,
                )
            },
            suffix = {
                Text(
                    text = stringResource(R.string.settings_success_timeout_suffix),
                    style = ToggleSubtitleStyle,
                    color = SubText,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SparkBlue,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TipAmountField(
    tipAmountInCents: Long,
    currencyCode: String?,
    enabled: Boolean,
    onAmountChanged: (String) -> Unit,
) {
    val currencySymbol = remember(currencyCode) { currencySymbolFor(currencyCode) }
    var text by remember(tipAmountInCents) { mutableStateOf(formatCentsAsDecimal(tipAmountInCents)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 24.dp, top = 8.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_tip_amount_title),
            style = ToggleTitleStyle,
            color = DarkBlue,
        )
        Text(
            text = stringResource(R.string.settings_tip_amount_subtitle),
            style = ToggleSubtitleStyle,
            color = SubText,
        )
        OutlinedTextField(
            value = text,
            onValueChange = { raw ->
                val sanitized = sanitizeDecimalInput(raw)
                text = sanitized
                onAmountChanged(sanitized)
            },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            placeholder = {
                Text(
                    text = stringResource(R.string.settings_tip_amount_placeholder),
                    style = ToggleSubtitleStyle,
                    color = SubText,
                )
            },
            prefix = {
                Text(
                    text = currencySymbol,
                    style = ToggleSubtitleStyle,
                    color = SubText,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SparkBlue,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun sanitizeDecimalInput(raw: String): String {
    val sb = StringBuilder()
    var hasDot = false
    var integerDigits = 0
    var fractionalDigits = 0
    for (ch in raw) {
        when {
            ch.isDigit() -> {
                if (hasDot) {
                    if (fractionalDigits < 2) {
                        sb.append(ch)
                        fractionalDigits++
                    }
                } else if (integerDigits < 7) {
                    sb.append(ch)
                    integerDigits++
                }
            }

            (ch == '.' || ch == ',') && !hasDot -> {
                hasDot = true
                if (integerDigits == 0) {
                    sb.append('0')
                    integerDigits = 1
                }
                sb.append('.')
            }
        }
    }
    return sb.toString()
}

private fun formatCentsAsDecimal(cents: Long): String =
    if (cents > 0L) "%.2f".format(cents / 100.0) else ""

private fun currencySymbolFor(code: String?): String = try {
    code?.let { java.util.Currency.getInstance(it).symbol } ?: "€"
} catch (_: IllegalArgumentException) {
    "€"
}

@Composable
private fun SettingsActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (enabled) Violet else Violet.copy(alpha = 0.38f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Violet,
            disabledContentColor = Violet.copy(alpha = 0.38f),
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = ButtonLabelStyle,
        )
    }
}

// ---- Offline Card Components -----------------------------------------------

@Composable
private fun OfflineSessionContent(
    session: OfflineSession,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onUploadClick: () -> Unit,
    onCheckUpdatesClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            start = 24.dp,
            end = 24.dp,
            top = 4.dp,
            bottom = 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.settings_offline_transactions_title),
                style = ToggleTitleStyle,
                color = DarkBlue,
            )
            Text(
                text = stringResource(R.string.settings_offline_transactions_subtitle),
                style = ToggleSubtitleStyle,
                color = SubText,
            )
        }

        when (session) {
            is OfflineSession.Active -> ActiveSessionBody(
                session = session,
                onEndClick = onEndClick,
            )

            is OfflineSession.Incomplete -> InactiveSessionBody(
                startEnabled = false,
                uploadEnabled = true,
                onStartClick = onStartClick,
                onUploadClick = onUploadClick,
                onCheckUpdatesClick = onCheckUpdatesClick,
            )

            OfflineSession.Inactive -> InactiveSessionBody(
                startEnabled = true,
                uploadEnabled = false,
                onStartClick = onStartClick,
                onUploadClick = onUploadClick,
                onCheckUpdatesClick = onCheckUpdatesClick,
            )
        }
    }
}

@Composable
private fun ActiveSessionBody(
    session: OfflineSession.Active,
    onEndClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(ReaderGreen, CircleShape),
        )
        Text(
            text = stringResource(R.string.settings_offline_session_active),
            style = ToggleTitleStyle.copy(fontSize = ToggleTitleStyle.fontSize),
            color = SparkBlue,
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OfflineStatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Schedule,
            value = formatRemainingTime(session.remainingTimeMillis),
            label = stringResource(R.string.settings_offline_time_left),
        )
        OfflineStatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AccountBalanceWallet,
            value = "${session.approvedTransactionCount}/${session.maxTransactionCount}",
            label = stringResource(R.string.settings_offline_transactions_count),
        )
    }

    PrimaryActionButton(
        icon = Icons.Default.Stop,
        text = stringResource(R.string.settings_offline_end),
        onClick = onEndClick,
    )
}

@Composable
private fun InactiveSessionBody(
    startEnabled: Boolean,
    uploadEnabled: Boolean,
    onStartClick: () -> Unit,
    onUploadClick: () -> Unit,
    onCheckUpdatesClick: () -> Unit,
) {
    PrimaryActionButton(
        icon = Icons.Default.PlayArrow,
        text = stringResource(R.string.settings_offline_start),
        onClick = onStartClick,
        enabled = startEnabled,
    )
    HorizontalDivider(color = SandDivider)
    SettingsActionButton(
        icon = Icons.Default.CloudUpload,
        enabled = uploadEnabled,
        text = stringResource(R.string.settings_upload_transactions),
        onClick = onUploadClick,
    )
    SettingsActionButton(
        icon = Icons.Default.Security,
        text = stringResource(R.string.settings_check_for_updates),
        onClick = onCheckUpdatesClick,
    )
}

@Composable
private fun OfflineStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(LightSand.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = SubText,
        )
        Text(
            text = value,
            style = ToggleTitleStyle,
            color = DarkBlue,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = ToggleSubtitleStyle.copy(fontSize = ToggleSubtitleStyle.fontSize),
            color = SubText,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PrimaryActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Violet),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = ButtonLabelStyle,
            color = Color.White,
        )
    }
}

private fun formatRemainingTime(millis: Long): String {
    val safe = millis.coerceAtLeast(0L)
    val hours = TimeUnit.MILLISECONDS.toHours(safe)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(safe) - TimeUnit.HOURS.toMinutes(hours)
    return "%02d:%02d".format(hours, minutes)
}

@Preview(showBackground = true, name = "Inactive Offline Session")
@Composable
private fun SettingsScreenInactivePreview() {
    AppTheme {
        SettingsScreen(
            uiState = UiState(
                isLoggedIn = true,
                merchantInfo = MerchantInfo(merchantCode = "MPOS94Z3", currencyCode = "EUR"),
                checkoutConfiguration = CheckoutConfiguration(tippingEnabled = true),
            ),
            onCloseClick = {},
            onTippingChanged = {},
            onTipAmountChanged = {},
            onStartOfflineSessionClick = {},
            onEndOfflineSessionClick = {},
            onShowSuccessScreenChanged = {},
            onSuccessScreenTimeoutChanged = {},
            onShowResultDialogChanged = {},
            onUpdateSecurityPatchClick = {},
            onUploadOfflineTransactionsClick = {},
            onLogoutClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Active Offline Session")
@Composable
private fun SettingsScreenActivePreview() {
    AppTheme {
        SettingsScreen(
            uiState = UiState(
                isLoggedIn = true,
                merchantInfo = MerchantInfo(merchantCode = "MPOS94Z3", currencyCode = "EUR"),
                offlineSession = OfflineSession.Active(
                    remainingTimeMillis = TimeUnit.HOURS.toMillis(24),
                    approvedTransactionCount = 0,
                    failedTransactionCount = 0,
                ),
            ),
            onCloseClick = {},
            onTippingChanged = {},
            onTipAmountChanged = {},
            onStartOfflineSessionClick = {},
            onEndOfflineSessionClick = {},
            onShowSuccessScreenChanged = {},
            onSuccessScreenTimeoutChanged = {},
            onShowResultDialogChanged = {},
            onUpdateSecurityPatchClick = {},
            onUploadOfflineTransactionsClick = {},
            onLogoutClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Manual Tip Input")
@Composable
private fun SettingsScreenManualTipPreview() {
    AppTheme {
        SettingsScreen(
            uiState = UiState(
                isLoggedIn = true,
                connectedReader = ConnectedReader(
                    readerType = ReaderType.SOLO_LITE,
                    serialNumber = "SN12345421",
                    lastKnownBatteryPercentage = 87,
                ),
                merchantInfo = MerchantInfo(merchantCode = "MPOS94Z3", currencyCode = "USD"),
                isTipOnCardReaderAvailable = false,
                checkoutConfiguration = CheckoutConfiguration(
                    tippingEnabled = true,
                    tipAmountInCents = 150L,
                ),
            ),
            onCloseClick = {},
            onTippingChanged = {},
            onTipAmountChanged = {},
            onStartOfflineSessionClick = {},
            onEndOfflineSessionClick = {},
            onShowSuccessScreenChanged = {},
            onSuccessScreenTimeoutChanged = {},
            onShowResultDialogChanged = {},
            onUpdateSecurityPatchClick = {},
            onUploadOfflineTransactionsClick = {},
            onLogoutClick = {},
        )
    }
}
