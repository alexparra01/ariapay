package com.ariapay.domain.usecase

import com.ariapay.data.repository.PaymentRepository
import com.ariapay.domain.model.*
import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}

class LogoutUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.logout()
}

class CheckAuthStatusUseCase(private val repository: PaymentRepository) {
    operator fun invoke() = repository.isLoggedIn()
}

class GetCurrentUserUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getCurrentUser()
}

class GetWalletUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getWallet()
}

class GetDefaultCardUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getDefaultCard()
}

class GetTransactionHistoryUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20) = repository.getTransactionHistory(page, pageSize)
}

class ObserveTransactionsUseCase(private val repository: PaymentRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.observeTransactions()
}

class ProcessNfcPaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(
        nfcData: NfcPaymentData,
        amount: Double,
        merchantId: String,
        merchantName: String
    ) = repository.processNfcPayment(nfcData, amount, merchantId, merchantName)
}

class QuickPayUseCase(
    private val getDefaultCardUseCase: GetDefaultCardUseCase,
    private val processNfcPaymentUseCase: ProcessNfcPaymentUseCase
) {
    suspend operator fun invoke(
        amount: Double,
        merchantId: String,
        merchantName: String
    ): Result<PaymentResult> {
        val cardResult = getDefaultCardUseCase()
        if (cardResult.isFailure) return Result.failure(Exception("No payment card available"))
        
        val card = cardResult.getOrNull() ?: return Result.failure(Exception("No default card set"))
        if (!card.nfcEnabled) return Result.failure(Exception("NFC not enabled for this card"))
        
        val nfcData = NfcPaymentData(
            tokenId = card.tokenId,
            cardId = card.id,
            encryptedPayload = "ENC_${card.tokenId}_${amount}",
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
        
        return processNfcPaymentUseCase(nfcData, amount, merchantId, merchantName)
    }
}
