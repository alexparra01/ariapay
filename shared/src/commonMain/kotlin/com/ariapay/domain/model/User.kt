package com.ariapay.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val isVerified: Boolean = false,
    val createdAt: Long
)

@Serializable
data class PaymentCard(
    val id: String,
    val userId: String,
    val cardType: CardType,
    val lastFourDigits: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cardholderName: String,
    val isDefault: Boolean = false,
    val nfcEnabled: Boolean = true,
    val tokenId: String
)

@Serializable
enum class CardType {
    VISA, MASTERCARD, AMEX, DISCOVER, OTHER
}

@Serializable
data class Wallet(
    val userId: String,
    val balance: Double,
    val currency: String = "USD",
    val cards: List<PaymentCard>,
    val totalTransactions: Int
)

@Serializable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val token: AuthToken? = null,
    val errorMessage: String? = null
)
