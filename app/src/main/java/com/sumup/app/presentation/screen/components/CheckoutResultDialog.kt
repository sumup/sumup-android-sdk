package com.sumup.app.presentation.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumup.app.R
import com.sumup.app.domain.model.SdkResult
import com.sumup.app.presentation.theme.DarkBlue
import com.sumup.app.presentation.theme.SandDivider
import com.sumup.app.presentation.theme.SubText

@Composable
internal fun CheckoutResultDialog(
    result: SdkResult,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.result_dialog_title),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ResultRow(
                    label = stringResource(R.string.result_dialog_result_code),
                    value = result.resultCode.toString(),
                )
                ResultRow(
                    label = stringResource(R.string.result_dialog_message),
                    value = result.message,
                )
                if (result is SdkResult.Payment) {
                    PaymentDetails(result)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.result_dialog_dismiss))
            }
        },
    )
}

@Composable
private fun PaymentDetails(payment: SdkResult.Payment) {
    payment.transactionCode?.let { txCode ->
        HorizontalDivider(
            color = SandDivider,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        ResultRow(
            label = stringResource(R.string.result_dialog_tx_code),
            value = txCode,
        )
    }
    payment.receiptSent?.let { sent ->
        ResultRow(
            label = stringResource(R.string.result_dialog_receipt_sent),
            value = sent.toString(),
        )
    }
    payment.transactionInfo?.let { info ->
        HorizontalDivider(
            color = SandDivider,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        ResultRow(
            label = stringResource(R.string.result_dialog_tx_info),
            value = info,
        )
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SubText,
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBlue,
        )
    }
}
