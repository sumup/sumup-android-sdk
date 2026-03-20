package com.sumup.app

import android.app.Application
import com.sumup.app.di.appModule
import com.sumup.reader.sdk.api.SumUpState
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SumUpState.init(this)
        startKoin {
            androidContext(this@SampleApplication)
            modules(appModule)
        }
    }
}
