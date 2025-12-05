package com.ariapay.data.api

import com.ariapay.domain.model.*

interface AriaPayApi {
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun logout(): Boolean
    suspend fun getCurrentUser(): User?
    suspend fun getWallet(): Wallet?
    suspend fun addCard(card: PaymentCard): PaymentCard?
    suspend fun removeCard(cardId: String): Boolean
    suspend fun setDefaultCard(cardId: String): Boolean
    suspend fun createTransaction(request: TransactionRequest): TransactionResponse
    suspend fun getTransaction(transactionId: String): Transaction?
    suspend fun getTransactionHistory(page: Int = 1, pageSize: Int = 20): TransactionListResponse
    suspend fun processNfcPayment(paymentData: NfcPaymentData, amount: Double, merchantId: String, merchantName: String): PaymentResult
    suspend fun validateNfcToken(tokenId: String): Boolean
}
