package com.ariapay.di

import com.ariapay.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { PaymentViewModel(get(), get()) }
    viewModel { TransactionHistoryViewModel(get(), get()) }
}
