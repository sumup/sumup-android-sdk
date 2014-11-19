package com.sumup.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ResponseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_response);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.getBoolean("Result")) {
                ((TextView) findViewById(R.id.result_text)).setText(extra.getString("Response"));
            } else {
                ((TextView) findViewById(R.id.result_text)).setText(extra.getString("Response"));
            }
            String txId = extra.getString("transactionId");
            if (txId != null) {
                ((TextView) findViewById(R.id.tx_id)).setText(txId);
            }
        }
    }
}
