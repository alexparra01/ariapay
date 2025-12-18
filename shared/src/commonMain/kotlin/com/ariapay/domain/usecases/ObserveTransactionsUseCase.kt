package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository
import com.ariapay.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase(private val repository: PaymentRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.observeTransactions()
}
