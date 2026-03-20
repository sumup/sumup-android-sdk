package com.sumup.app.presentation.screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sumup.app.presentation.theme.KeypadButtonBg
import com.sumup.app.presentation.theme.KeypadButtonBorder
import com.sumup.app.presentation.theme.KeypadDigitStyle

@Composable
internal fun Keypad(
    onDigitPressed: (Int) -> Unit,
    onBackspace: () -> Unit,
    onDoubleZeroPressed: () -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("00", "0", "⌫"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { key ->
                    KeypadButton(
                        label = key,
                        contentDesc = when (key) {
                            "⌫" -> "Backspace"
                            "00" -> "00"
                            else -> "Digit $key"
                        },
                        onClick = {
                            when (key) {
                                "⌫" -> onBackspace()
                                "00" -> onDoubleZeroPressed()
                                else -> onDigitPressed(key.toInt())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                    )
                }
            }
        }
    }
}

private val ButtonShape = RoundedCornerShape(16.dp)

@Composable
private fun KeypadButton(
    label: String,
    contentDesc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics { contentDescription = contentDesc }
            .border(1.dp, KeypadButtonBorder, ButtonShape),
        shape = ButtonShape,
        color = KeypadButtonBg,
        tonalElevation = 0.dp,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = label,
                style = KeypadDigitStyle,
            )
        }
    }
}
