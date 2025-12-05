package com.ariapay.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NfcPaymentData(
    val tokenId: String,
    val cardId: String,
    val encryptedPayload: String,
    val timestamp: Long,
    val terminalId: String? = null
)

@Serializable
data class PaymentResult(
    val success: Boolean,
    val transactionId: String? = null,
    val amount: Double? = null,
    val merchantName: String? = null,
    val timestamp: Long? = null,
    val errorMessage: String? = null,
    val errorCode: PaymentErrorCode? = null
)

@Serializable
enum class PaymentErrorCode {
    INSUFFICIENT_FUNDS,
    CARD_DECLINED,
    NETWORK_ERROR,
    NFC_ERROR,
    INVALID_MERCHANT,
    CARD_EXPIRED,
    UNKNOWN_ERROR
}
