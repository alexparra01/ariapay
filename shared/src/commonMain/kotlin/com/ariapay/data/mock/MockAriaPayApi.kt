package com.ariapay.data.mock

import com.ariapay.data.api.AriaPayApi
import com.ariapay.domain.model.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockAriaPayApi : AriaPayApi {
    
    private val transactions = mutableListOf<Transaction>()
    private val cards = mutableListOf<PaymentCard>()
    private var currentUser: User? = null
    private var authToken: AuthToken? = null
    
    init {
        setupSampleData()
    }
    
    private fun setupSampleData() {
        currentUser = User(
            id = "user_001",
            email = "demo@ariapay.com",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+1234567890",
            isVerified = true,
            createdAt = currentTimeMillis() - 86400000 * 30
        )
        
        cards.addAll(listOf(
            PaymentCard(
                id = "card_001",
                userId = "user_001",
                cardType = CardType.VISA,
                lastFourDigits = "4242",
                expiryMonth = 12,
                expiryYear = 2027,
                cardholderName = "JOHN DOE",
                isDefault = true,
                nfcEnabled = true,
                tokenId = "nfc_token_visa_001"
            ),
            PaymentCard(
                id = "card_002",
                userId = "user_001",
                cardType = CardType.MASTERCARD,
                lastFourDigits = "5555",
                expiryMonth = 6,
                expiryYear = 2026,
                cardholderName = "JOHN DOE",
                isDefault = false,
                nfcEnabled = true,
                tokenId = "nfc_token_mc_002"
            )
        ))
        
        val merchants = listOf(
            "Coffee House" to "merchant_coffee",
            "Grocery Store" to "merchant_grocery",
            "Gas Station" to "merchant_gas",
            "Restaurant" to "merchant_restaurant"
        )
        
        repeat(10) { index ->
            val merchant = merchants.random()
            transactions.add(
                Transaction(
                    id = "txn_sample_$index",
                    amount = (Random.nextDouble(5.0, 150.0) * 100).toLong() / 100.0,
                    currency = "USD",
                    merchantId = merchant.second,
                    merchantName = merchant.first,
                    cardLastFour = cards.random().lastFourDigits,
                    status = TransactionStatus.COMPLETED,
                    timestamp = currentTimeMillis() - (index * 86400000L),
                    nfcTokenId = cards.first().tokenId
                )
            )
        }
    }
    
    override suspend fun login(request: LoginRequest): LoginResponse {
        delay(800)
        return if (request.email == "demo@ariapay.com" && request.password == "password123") {
            authToken = AuthToken(
                accessToken = "mock_access_token_${currentTimeMillis()}",
                refreshToken = "mock_refresh_token_${currentTimeMillis()}",
                expiresAt = currentTimeMillis() + 3600000
            )
            LoginResponse(success = true, user = currentUser, token = authToken)
        } else {
            LoginResponse(success = false, errorMessage = "Invalid email or password")
        }
    }
    
    override suspend fun logout(): Boolean {
        delay(300)
        authToken = null
        return true
    }
    
    override suspend fun getCurrentUser(): User? {
        delay(400)
        return currentUser
    }
    
    override suspend fun getWallet(): Wallet? {
        delay(500)
        return currentUser?.let {
            Wallet(
                userId = it.id,
                balance = 1250.75,
                currency = "USD",
                cards = cards.filter { c -> c.userId == it.id },
                totalTransactions = transactions.size
            )
        }
    }
    
    override suspend fun addCard(card: PaymentCard): PaymentCard? {
        delay(1000)
        cards.add(card)
        return card
    }
    
    override suspend fun removeCard(cardId: String): Boolean {
        delay(500)
        return cards.removeAll { it.id == cardId }
    }
    
    override suspend fun setDefaultCard(cardId: String): Boolean {
        delay(400)
        cards.forEachIndexed { index, card ->
            cards[index] = card.copy(isDefault = card.id == cardId)
        }
        return true
    }
    
    override suspend fun createTransaction(request: TransactionRequest): TransactionResponse {
        delay(1500)
        val isSuccess = Random.nextFloat() < 0.90f
        
        val transaction = Transaction(
            id = "txn_${currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
            amount = request.amount,
            currency = request.currency,
            merchantId = request.merchantId,
            merchantName = request.merchantName,
            cardLastFour = request.cardLastFour,
            status = if (isSuccess) TransactionStatus.COMPLETED else TransactionStatus.FAILED,
            timestamp = currentTimeMillis(),
            nfcTokenId = request.nfcTokenId
        )
        
        transactions.add(0, transaction)
        
        return TransactionResponse(
            success = isSuccess,
            transaction = transaction,
            errorMessage = if (!isSuccess) "Transaction declined" else null
        )
    }
    
    override suspend fun getTransaction(transactionId: String): Transaction? {
        delay(400)
        return transactions.find { it.id == transactionId }
    }
    
    override suspend fun getTransactionHistory(page: Int, pageSize: Int): TransactionListResponse {
        delay(600)
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, transactions.size)
        val pageTransactions = if (startIndex < transactions.size) {
            transactions.subList(startIndex, endIndex)
        } else emptyList()
        
        return TransactionListResponse(
            transactions = pageTransactions,
            totalCount = transactions.size,
            page = page,
            pageSize = pageSize
        )
    }
    
    override suspend fun processNfcPayment(
        paymentData: NfcPaymentData,
        amount: Double,
        merchantId: String,
        merchantName: String
    ): PaymentResult {
        delay(2000)
        val card = cards.find { it.tokenId == paymentData.tokenId }
            ?: return PaymentResult(success = false, errorMessage = "Invalid NFC token", errorCode = PaymentErrorCode.NFC_ERROR)
        
        val request = TransactionRequest(
            amount = amount,
            merchantId = merchantId,
            merchantName = merchantName,
            cardLastFour = card.lastFourDigits,
            nfcTokenId = paymentData.tokenId
        )
        
        val response = createTransaction(request)
        
        return if (response.success && response.transaction != null) {
            PaymentResult(
                success = true,
                transactionId = response.transaction.id,
                amount = response.transaction.amount,
                merchantName = response.transaction.merchantName,
                timestamp = response.transaction.timestamp
            )
        } else {
            PaymentResult(success = false, errorMessage = response.errorMessage, errorCode = PaymentErrorCode.CARD_DECLINED)
        }
    }
    
    override suspend fun validateNfcToken(tokenId: String): Boolean {
        delay(300)
        return cards.any { it.tokenId == tokenId && it.nfcEnabled }
    }
    
    private fun currentTimeMillis(): Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
}
