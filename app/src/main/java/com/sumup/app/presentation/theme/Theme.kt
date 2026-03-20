package com.sumup.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val SparkBlue = Color(0xFF4307DB)
internal val Violet = Color(0xFF9E33E0)
internal val ReaderGreen = Color(0xFF22C55E)
internal val DarkBlue = Color(0xFF101828)
internal val KeypadButtonBg = Color.White.copy(alpha = 0.15f)
internal val KeypadButtonBorder = Color.White.copy(alpha = 0.3f)
internal val LightBlue = Color(0xFF00D3F8)
internal val LightSand = Color(0xFFF6F1EB)
internal val SandDivider = Color(0xFFE6D3C3)
internal val SubText = Color(0xFF646973)

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EEFF),
    background = Color.White,
    surface = Color.White,
    surfaceVariant = KeypadButtonBg,
    onBackground = DarkBlue,
    onSurface = DarkBlue,
    outline = SandDivider,
)

private val AppTypography = Typography(
    displayLarge = AmountTextStyle,
    headlineLarge = WelcomeTitleStyle,
    headlineMedium = SettingsHeadingStyle,
    titleLarge = AppNameStyle,
    titleMedium = ToggleTitleStyle,
    titleSmall = SectionTitleStyle,
    bodyLarge = ToggleTitleStyle,
    bodyMedium = ToggleSubtitleStyle,
    bodySmall = MerchantCodeStyle,
    labelLarge = ButtonLabelStyle,
    labelMedium = BadgeTextStyle,
    labelSmall = AmountLabelStyle,
)

@Composable
internal fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content,
    )
}
