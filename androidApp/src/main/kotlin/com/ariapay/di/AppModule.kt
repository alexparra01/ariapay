package com.ariapay.di

import com.ariapay.ui.features.home.HomeViewModel
import com.ariapay.ui.features.auth.AuthenticationViewModel
import com.ariapay.ui.features.payment.PaymentViewModel
import com.ariapay.ui.features.transactionhistory.TransactionHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PaymentViewModel)
    viewModelOf(::TransactionHistoryViewModel)
}
