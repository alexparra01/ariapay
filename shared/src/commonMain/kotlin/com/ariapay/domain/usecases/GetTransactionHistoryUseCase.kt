package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository

class GetTransactionHistoryUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20) = repository.getTransactionHistory(page, pageSize)
}
