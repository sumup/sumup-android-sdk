package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;

import java.math.BigDecimal;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    private TextView mResultCode;
    private TextView mResultMessage;
    private TextView mTxCode;
    private TextView mReceiptSent;
    private TextView mTxInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                SumUpLogin sumupLogin = SumUpLogin.builder("7ca84f17-84a5-4140-8df6-6ebeed8540fc").build();
                SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, REQUEST_CODE_LOGIN);
            }
        });

        Button btnCharge = (Button) findViewById(R.id.button_charge);
        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpPayment payment = SumUpPayment.builder()
                        // mandatory parameters
                        .total(new BigDecimal("1.12")) // minimum 1.00
                        .currency(SumUpPayment.Currency.EUR)
                        // optional: add details
                        .title("Taxi Ride")
                        .receiptEmail("customer@mail.com")
                        .receiptSMS("+3531234567890")
                        // optional: Add metadata
                        .addAdditionalInfo("AccountId", "taxi0334")
                        .addAdditionalInfo("From", "Paris")
                        .addAdditionalInfo("To", "Berlin")
                        // optional: foreign transaction ID, must be unique!
                        .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                        .build();

                SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
            }
        });

        Button paymentSettings = (Button) findViewById(R.id.button_payment_settings);
        paymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.openPaymentSettingsActivity(MainActivity.this, REQUEST_CODE_PAYMENT_SETTINGS);
            }
        });

        Button prepareCardTerminal = (Button) findViewById(R.id.button_prepare_card_terminal);
        prepareCardTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.prepareForCheckout();
            }
        });

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.logout();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetViews();

        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Bundle extra = data.getExtras();

                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

                    String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
                    mTxCode.setText(txCode == null ? "" : "Transaction Code: " + txCode);

                    boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
                    mReceiptSent.setText("Receipt sent: " + receiptSent);

                    TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                    mTxInfo.setText(transactionInfo == null ? "" : "Transaction Info : " + transactionInfo);
                }
                break;

            case REQUEST_CODE_PAYMENT_SETTINGS:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            default:
                break;
        }
    }

    private void resetViews() {
        mResultCode.setText("");
        mResultMessage.setText("");
        mTxCode.setText("");
        mReceiptSent.setText("");
        mTxInfo.setText("");
    }

    private void findViews() {
        mResultCode = (TextView) findViewById(R.id.result);
        mResultMessage = (TextView) findViewById(R.id.result_msg);
        mTxCode = (TextView) findViewById(R.id.tx_code);
        mReceiptSent = (TextView) findViewById(R.id.receipt_sent);
        mTxInfo = (TextView) findViewById(R.id.tx_info);
    }
}
