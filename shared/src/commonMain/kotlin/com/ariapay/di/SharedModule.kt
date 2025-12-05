package com.ariapay.di

import com.ariapay.data.api.AriaPayApi
import com.ariapay.data.mock.MockAriaPayApi
import com.ariapay.data.repository.PaymentRepository
import com.ariapay.data.repository.PaymentRepositoryImpl
import com.ariapay.domain.usecase.*
import org.koin.core.module.Module
import org.koin.dsl.module

val apiModule = module {
    single<AriaPayApi> { MockAriaPayApi() }
}

val repositoryModule = module {
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { CheckAuthStatusUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetWalletUseCase(get()) }
    factory { GetDefaultCardUseCase(get()) }
    factory { GetTransactionHistoryUseCase(get()) }
    factory { ObserveTransactionsUseCase(get()) }
    factory { ProcessNfcPaymentUseCase(get()) }
    factory { QuickPayUseCase(get(), get()) }
}

val sharedModules: List<Module> = listOf(apiModule, repositoryModule, useCaseModule)

fun getSharedKoinModules(): List<Module> = sharedModules
