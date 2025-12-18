package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository

class CheckAuthStatusUseCase(private val repository: PaymentRepository) {
    operator fun invoke() = repository.isLoggedIn()
}
