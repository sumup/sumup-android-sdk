package com.sumup.sdksampleapp;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.kaching.merchant.SumUpPayment;

import java.util.HashMap;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button checkout = (Button) findViewById(R.id.button);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create SumUpPayment with mandatory parameters
                String affiliateKey = "7ca84f17-84a5-4140-8df6-6ebeed8540fc";
                double productAmount = 1.23;
                String currency = "EUR";

                // reference YOUR OWN activity here that should receive the payment result
                String resultActivity = "com.sumup.sdksampleapp.ResponseActivity";

                SumUpPayment sumUpPayment =
                        new SumUpPayment(affiliateKey, productAmount, currency, resultActivity, MainActivity.this);

                // optional: add details
                sumUpPayment.setProductTitle("Taxi Ride");
                sumUpPayment.setReceiptEmail("customer@mail.com");
                sumUpPayment.setReceiptSMS("+3531234567890");

                // optional: Add additional metadata
                HashMap<String, String> additionalInfo = new HashMap<>(3);
                additionalInfo.put("AccountId", "taxi0334");
                additionalInfo.put("From", "Here");
                additionalInfo.put("To", "There");
                sumUpPayment.setAdditionalInfo(additionalInfo);

                // start the payment process
                sumUpPayment.openPaymentActivity(MainActivity.this);
            }
        });
    }
}
