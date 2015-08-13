package com.sumup.sdksampleapp;

import android.app.Application;
import com.kaching.merchant.SumUpState;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SumUpState.init(this);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SumUpState.updateLocales();
    }
}
