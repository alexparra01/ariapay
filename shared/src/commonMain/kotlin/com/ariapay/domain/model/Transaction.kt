package com.ariapay.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val amount: Double,
    val currency: String = "USD",
    val merchantId: String,
    val merchantName: String,
    val cardLastFour: String,
    val status: TransactionStatus,
    val timestamp: Long,
    val nfcTokenId: String
)

@Serializable
enum class TransactionStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED
}

@Serializable
data class TransactionRequest(
    val amount: Double,
    val currency: String = "USD",
    val merchantId: String,
    val merchantName: String,
    val cardLastFour: String,
    val nfcTokenId: String
)

@Serializable
data class TransactionResponse(
    val success: Boolean,
    val transaction: Transaction? = null,
    val errorMessage: String? = null
)

@Serializable
data class TransactionListResponse(
    val transactions: List<Transaction>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)
