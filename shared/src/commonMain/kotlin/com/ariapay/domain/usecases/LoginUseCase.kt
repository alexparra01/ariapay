package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository

class LoginUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}
