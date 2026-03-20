package com.sumup.app.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val AmountTextStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 72.sp,
    lineHeight = 94.sp,
    color = Color.White,
)

internal val AmountLabelStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 21.sp,
    color = LightBlue,
)

internal val KeypadDigitStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    color = LightBlue,
)

internal val MerchantCodeStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    color = Color.White.copy(alpha = 0.7f),
)

internal val AppNameStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = Color.White,
)

internal val BadgeTextStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    color = Color.White,
)

internal val SectionTitleStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    color = SparkBlue,
)

internal val ToggleTitleStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 23.sp,
    color = DarkBlue,
)

internal val ToggleSubtitleStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    color = SubText,
)

internal val SettingsHeadingStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = SparkBlue,
)

internal val WelcomeTitleStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.6).sp,
    color = Color.White,
)

internal val ButtonLabelStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.44).sp,
)
