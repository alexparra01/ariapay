package com.ariapay.domain.usecases

import com.ariapay.data.repository.PaymentRepository
import com.ariapay.domain.model.NfcPaymentData

class ProcessNfcPaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(
        nfcData: NfcPaymentData,
        amount: Double,
        merchantId: String,
        merchantName: String
    ) = repository.processNfcPayment(nfcData, amount, merchantId, merchantName)
}
