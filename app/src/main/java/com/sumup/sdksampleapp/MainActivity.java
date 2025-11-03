package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sumup.checkout.core.models.TransactionInfo;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;
import com.sumup.merchant.reader.offline.OfflineSessionResult;
import com.sumup.merchant.reader.offline.OfflineUploadFailureReasons;
import com.sumup.merchant.reader.offline.callbacks.OfflineSessionCallback;
import com.sumup.merchant.reader.offline.callbacks.SecurityPatchUpdateCallback;
import com.sumup.merchant.reader.offline.callbacks.UploadOfflineTransactionsStatusListener;

import java.math.BigDecimal;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_CARD_READER_PAGE = 4;

    private TextView mResultCode;
    private TextView mResultMessage;
    private TextView mTxCode;
    private TextView mReceiptSent;
    private TextView mTxInfo;
    private TextView mOfflineSessionInfo;

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

        Button logMerchant = (Button) findViewById(R.id.button_log_merchant);
        logMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SumUpAPI.isLoggedIn()) {
                    mResultCode.setText("Result code: " + SumUpAPI.Response.ResultCode.ERROR_NOT_LOGGED_IN);
                    mResultMessage.setText("Message: Not logged in");
                } else {
                    mResultCode.setText("Result code: " + SumUpAPI.Response.ResultCode.SUCCESSFUL);
                    mResultMessage.setText(
                            String.format("Currency: %s, Merchant Code: %s", SumUpAPI.getCurrentMerchant().getCurrency().getIsoCode(),
                                    SumUpAPI.getCurrentMerchant().getMerchantCode()));
                }
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
                        .configureRetryPolicy(2000, 60000, true)
                        // optional: foreign transaction ID, must be unique!
                        .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                        .optInOfflinePayments()
                        .build();

                SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
            }
        });

        Button cardReaderPage = (Button) findViewById(R.id.button_card_reader_page);
        cardReaderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.openCardReaderPage(MainActivity.this, REQUEST_CODE_CARD_READER_PAGE);
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

        Button btnUpdateSecurityPatch = findViewById(R.id.button_update_security_patch);
        btnUpdateSecurityPatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.updateOfflineSecurityPatch(new SecurityPatchUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Security patch updated successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Failed to update the Security patch", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        Button btnUploadOfflineTransactions = findViewById(R.id.button_upload_offline_tx);
        btnUploadOfflineTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.uploadOfflineTransactions(new UploadOfflineTransactionsStatusListener() {
                    @Override
                    public void onUploadSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Offline transactions uploaded successfully", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void onUploadFailure(@NonNull OfflineUploadFailureReasons offlineUploadFailureReasons) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Failed to upload offline transactions: " + offlineUploadFailureReasons.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        Button btnOfflineSessionInfo = findViewById(R.id.button_offline_session_info);
        btnOfflineSessionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.fetchCurrentOfflineSession(new OfflineSessionCallback() {
                    @Override
                    public void onSessionInfoReceived(@NonNull OfflineSessionResult offlineSessionResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mOfflineSessionInfo.setText(offlineSessionResult.toString());
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetViews();

        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
            case REQUEST_CODE_CARD_READER_PAGE:
                setResultCodeAndMessage(data);
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

            default:
                break;
        }
    }

    private void setResultCodeAndMessage(Intent data) {
        if (data != null) {
            Bundle extra = data.getExtras();
            mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
            mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
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
        mOfflineSessionInfo = findViewById(R.id.tv_offline_session_info);
    }
}
