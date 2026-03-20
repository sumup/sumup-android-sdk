package com.sumup.app.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumup.app.R
import com.sumup.app.presentation.theme.AmountLabelStyle
import com.sumup.app.presentation.theme.AmountTextStyle
import com.sumup.app.presentation.theme.AppTheme
import java.util.Currency

@Composable
internal fun AmountDisplay(
    amountInCents: Long,
    currencyCode: String?,
    modifier: Modifier = Modifier,
    baseStyle: TextStyle = AmountTextStyle,
    minFontSize: TextUnit = 24.sp,
) {
    val (symbol, digits) = formatAmountParts(amountInCents, currencyCode)
    val amountText = remember(symbol, digits) {
        buildAnnotatedString {
            append("$symbol ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(digits)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = amountText,
            style = baseStyle.copy(
                textAlign = TextAlign.Center,
                lineHeight = TextUnit.Unspecified,
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = minFontSize,
                maxFontSize = baseStyle.fontSize,
                stepSize = 1.sp,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.checkout_amount_label),
            style = AmountLabelStyle,
        )
    }
}

private fun formatAmountParts(cents: Long, currencyCode: String?): Pair<String, String> {
    val symbol = try {
        currencyCode?.let { Currency.getInstance(it).symbol } ?: "€"
    } catch (_: IllegalArgumentException) {
        "€"
    }
    val digits = "%.2f".format(cents / 100.0)
    return symbol to digits
}

@Composable
@Preview
private fun AmountDisplayPreview() {
    AppTheme {
        AmountDisplay(
            amountInCents = 1245,
            currencyCode = "USD",
        )
    }
}

@Composable
@Preview(widthDp = 320)
private fun AmountDisplayLargeAmountPreview() {
    AppTheme {
        AmountDisplay(
            amountInCents = 9_999_999_99L,
            currencyCode = "USD",
        )
    }
}
