package com.sumup.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.sumup.merchant.api.SumUpAPI;

public class ResponseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_response);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            ((TextView) findViewById(R.id.result)).setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));

            ((TextView) findViewById(R.id.result_msg)).setText(
                    "Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

            String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
            if (txCode != null) {
                ((TextView) findViewById(R.id.tx_code)).setText("Transaction Code: " + txCode);
            }
        }
    }
}
