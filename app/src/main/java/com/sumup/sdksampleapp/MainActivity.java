package com.sumup.sdksampleapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.sumup.SumUpPayment;

import java.util.HashMap;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        if (savedInstanceState == null) {
        //            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        //        }


        Button checkout = (Button) findViewById(R.id.button);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpPayment sumUpPayment =
                        new SumUpPayment("7ca84f17-84a5-4140-8df6-6ebeed8540fc", 12.34, "EUR", "com.sumup.sdksampleapp.ResponseActivity",
                                MainActivity.this);
                // add optional additional details.
                sumUpPayment.setProductTitle("Taxi Ride");
                sumUpPayment.setReceiptEmail("customer@mail.com");
                sumUpPayment.setReceiptSMS("+3531234567890");
                // Create a hashMap to add additional information to the transaction
                HashMap additionalInfo = new HashMap(3);
                additionalInfo.put("AccountId", "taxi0334");
                additionalInfo.put("From", "Here");
                additionalInfo.put("To", "There");
                // add Additional info to transaction history
                sumUpPayment.setAdditionalInfo(additionalInfo);
                // start the payment process
                sumUpPayment.openPaymentActivity(MainActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
