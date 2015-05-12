package com.sumup.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.kaching.merchant.SumUpAPI;

public class ResponseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_response);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.getBoolean(SumUpAPI.EXTRA_PAYMENT_SUCESSFUL)) {
                ((TextView) findViewById(R.id.result)).setText("Result: Payment successful");
            } else {
                ((TextView) findViewById(R.id.result)).setText("Result: Payment failed");
            }

            ((TextView) findViewById(R.id.result_msg)).setText(
                    "Message: " + extra.getString(SumUpAPI.EXTRA_RESPONSE_MSG));

            String txCode = extra.getString(SumUpAPI.EXTRA_TRANSACTION_CODE);
            if (txCode != null) {
                ((TextView) findViewById(R.id.tx_code)).setText("Transaction Code: " + txCode);
            }
        }
    }
}
