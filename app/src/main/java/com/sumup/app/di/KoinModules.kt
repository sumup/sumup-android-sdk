package com.sumup.app.di

import com.sumup.app.data.mapper.ConnectedReaderMapper
import com.sumup.app.data.mapper.MerchantInfoMapper
import com.sumup.app.data.mapper.OfflineSessionMapper
import com.sumup.app.data.repository.ReaderSdkRepositoryImpl
import com.sumup.app.domain.repository.ReaderSdkRepository
import com.sumup.app.domain.usecase.CreateCheckoutRequestUseCase
import com.sumup.app.domain.usecase.CreateLoginRequestUseCase
import com.sumup.app.domain.usecase.ParsePaymentResultUseCase
import com.sumup.app.domain.usecase.ParseSdkStatusUseCase
import com.sumup.app.presentation.MainViewModel
import com.sumup.app.util.CoroutinesDispatcherProvider
import com.sumup.app.util.DefaultCoroutinesDispatcherProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {
    single<CoroutinesDispatcherProvider> { DefaultCoroutinesDispatcherProvider() }

    factory { OfflineSessionMapper() }
    factory { ConnectedReaderMapper() }
    factory { MerchantInfoMapper() }
    single<ReaderSdkRepository> {
        ReaderSdkRepositoryImpl(
            dispatcherProvider = get(),
            offlineSessionMapper = get(),
            connectedReaderMapper = get(),
            merchantInfoMapper = get(),
        )
    }

    factory { CreateLoginRequestUseCase() }
    factory { CreateCheckoutRequestUseCase(readerSdkRepository = get()) }
    factory { ParseSdkStatusUseCase() }
    factory { ParsePaymentResultUseCase() }

    viewModel {
        MainViewModel(
            createLoginRequestUseCase = get(),
            createCheckoutRequestUseCase = get(),
            parseSdkStatusUseCase = get(),
            parsePaymentResultUseCase = get(),
            readerSdkRepository = get(),
        )
    }
}
