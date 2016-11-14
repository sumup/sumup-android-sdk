package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;

import java.util.UUID;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCharge = (Button) findViewById(R.id.button_charge);
        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpPayment payment = SumUpPayment.builder()
                        // mandatory parameters
                        // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                        .affiliateKey("7ca84f17-84a5-4140-8df6-6ebeed8540fc")
                        .productAmount(1.12)
                        .currency(SumUpPayment.Currency.EUR)
                        // optional: add details
                        .productTitle("Taxi Ride")
                        .receiptEmail("customer@mail.com")
                        .receiptSMS("+3531234567890")
                        // optional: Add metadata
                        .addAdditionalInfo("AccountId", "taxi0334")
                        .addAdditionalInfo("From", "Paris")
                        .addAdditionalInfo("To", "Berlin")
                        // optional: foreign transaction ID, must be unique!
                        .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                        .build();

                SumUpAPI.openPaymentActivity(MainActivity.this, payment, 1);
            }
        });

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.logout();
            }
        });

        Button btnClearCardTerminalSettings = (Button) findViewById(R.id.button_clear_card_terminal_settings);
        btnClearCardTerminalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.clearCardTerminalSettings();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Bundle extra = data.getExtras();

            ((TextView) findViewById(R.id.result)).setText(
                    "Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));

            ((TextView) findViewById(R.id.result_msg)).setText(
                    "Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

            String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
            ((TextView) findViewById(R.id.tx_code)).setText(txCode == null ? "" : "Transaction Code: " + txCode);

            boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
            ((TextView) findViewById(R.id.receipt_sent)).setText("Receipt sent: " + receiptSent);

            TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
            ((TextView) findViewById(R.id.tx_info)).setText(
                    transactionInfo == null ? "" : "Transaction Info : " + transactionInfo);
        }
    }
}
