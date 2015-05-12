package com.sumup.sdksampleapp;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.kaching.merchant.SumUpAPI;
import com.kaching.merchant.SumUpPayment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button checkout = (Button) findViewById(R.id.button);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpPayment payment = SumUpPayment.builder()
                        //mandatory parameters
                        .affiliateKey("7ca84f17-84a5-4140-8df6-6ebeed8540fc")
                        .productAmount(1.23)
                        .currency("EUR")
                                // optional: add details
                        .productTitle("Taxi Ride")
                        .receiptEmail("customer@mail.com")
                        .receiptSMS("+3531234567890")
                                // optional: Add metadata
                        .addAdditionalInfo("AccountId", "taxi0334")
                        .addAdditionalInfo("From", "Berlin")
                        .addAdditionalInfo("To", "Paris")
                        .build();

                SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
            }
        });
    }
}
