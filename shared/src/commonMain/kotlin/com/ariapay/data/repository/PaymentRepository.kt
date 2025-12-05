package com.ariapay.data.repository

import com.ariapay.data.api.AriaPayApi
import com.ariapay.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PaymentRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun logout(): Result<Boolean>
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): Result<User>
    suspend fun getWallet(): Result<Wallet>
    suspend fun getDefaultCard(): Result<PaymentCard?>
    suspend fun createTransaction(request: TransactionRequest): Result<Transaction>
    suspend fun getTransactionHistory(page: Int = 1, pageSize: Int = 20): Result<TransactionListResponse>
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun processNfcPayment(nfcData: NfcPaymentData, amount: Double, merchantId: String, merchantName: String): Result<PaymentResult>
}

class PaymentRepositoryImpl(private val api: AriaPayApi) : PaymentRepository {
    
    private var authToken: AuthToken? = null
    private var cachedUser: User? = null
    private var cachedWallet: Wallet? = null
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    
    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.success) {
                authToken = response.token
                cachedUser = response.user
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Boolean> {
        return try {
            val success = api.logout()
            if (success) {
                authToken = null
                cachedUser = null
                cachedWallet = null
                _transactionsFlow.value = emptyList()
            }
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun isLoggedIn(): Boolean = authToken != null
    
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            cachedUser?.let { return Result.success(it) }
            val user = api.getCurrentUser() ?: throw Exception("User not found")
            cachedUser = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWallet(): Result<Wallet> {
        return try {
            val wallet = api.getWallet() ?: throw Exception("Wallet not found")
            cachedWallet = wallet
            Result.success(wallet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDefaultCard(): Result<PaymentCard?> {
        return try {
            val wallet = cachedWallet ?: api.getWallet()
            cachedWallet = wallet
            val defaultCard = wallet?.cards?.find { it.isDefault } ?: wallet?.cards?.firstOrNull()
            Result.success(defaultCard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createTransaction(request: TransactionRequest): Result<Transaction> {
        return try {
            val response = api.createTransaction(request)
            if (response.success && response.transaction != null) {
                val currentList = _transactionsFlow.value.toMutableList()
                currentList.add(0, response.transaction)
                _transactionsFlow.value = currentList
                Result.success(response.transaction)
            } else {
                Result.failure(Exception(response.errorMessage ?: "Transaction failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionHistory(page: Int, pageSize: Int): Result<TransactionListResponse> {
        return try {
            val response = api.getTransactionHistory(page, pageSize)
            if (page == 1) {
                _transactionsFlow.value = response.transactions
            } else {
                val currentList = _transactionsFlow.value.toMutableList()
                currentList.addAll(response.transactions)
                _transactionsFlow.value = currentList
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeTransactions(): Flow<List<Transaction>> = _transactionsFlow.asStateFlow()
    
    override suspend fun processNfcPayment(
        nfcData: NfcPaymentData,
        amount: Double,
        merchantId: String,
        merchantName: String
    ): Result<PaymentResult> {
        return try {
            val result = api.processNfcPayment(nfcData, amount, merchantId, merchantName)
            if (result.success) {
                getTransactionHistory(page = 1, pageSize = 20)
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
