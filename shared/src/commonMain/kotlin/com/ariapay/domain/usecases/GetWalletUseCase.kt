package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository

class GetWalletUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getWallet()
}
