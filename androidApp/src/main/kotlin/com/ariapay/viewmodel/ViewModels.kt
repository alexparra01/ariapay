package com.ariapay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariapay.domain.model.*
import com.ariapay.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Auth ViewModel
class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    init { checkAuthStatus() }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (checkAuthStatusUseCase()) {
                getCurrentUserUseCase().fold(
                    onSuccess = { _authState.value = AuthState.Authenticated(it) },
                    onFailure = { _authState.value = AuthState.Unauthenticated }
                )
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            loginUseCase(email, password).fold(
                onSuccess = { response ->
                    val user = response.user
                    if (response.success && user != null) {
                        _loginState.value = LoginState.Success
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _loginState.value = LoginState.Error(response.errorMessage ?: "Login failed")
                    }
                },
                onFailure = { _loginState.value = LoginState.Error(it.message ?: "Login failed") }
            )
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Unauthenticated
            _loginState.value = LoginState.Idle
        }
    }
}

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

// Home ViewModel
class HomeViewModel(
    private val getWalletUseCase: GetWalletUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase
) : ViewModel() {
    
    private val _walletState = MutableStateFlow<WalletState>(WalletState.Loading)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()
    
    val recentTransactions: StateFlow<List<Transaction>> = observeTransactionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadWallet()
        loadRecentTransactions()
    }
    
    fun loadWallet() {
        viewModelScope.launch {
            _walletState.value = WalletState.Loading
            getWalletUseCase().fold(
                onSuccess = { _walletState.value = WalletState.Success(it) },
                onFailure = { _walletState.value = WalletState.Error(it.message ?: "Failed") }
            )
        }
    }
    
    private fun loadRecentTransactions() {
        viewModelScope.launch { getTransactionHistoryUseCase(1, 5) }
    }
    
    fun refresh() {
        loadWallet()
        loadRecentTransactions()
    }
}

sealed class WalletState {
    data object Loading : WalletState()
    data class Success(val wallet: Wallet) : WalletState()
    data class Error(val message: String) : WalletState()
}

// Payment ViewModel
class PaymentViewModel(
    private val quickPayUseCase: QuickPayUseCase,
    private val getDefaultCardUseCase: GetDefaultCardUseCase
) : ViewModel() {
    
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    private val _selectedCard = MutableStateFlow<PaymentCard?>(null)
    val selectedCard: StateFlow<PaymentCard?> = _selectedCard.asStateFlow()
    
    init { loadDefaultCard() }
    
    private fun loadDefaultCard() {
        viewModelScope.launch {
            getDefaultCardUseCase().fold(
                onSuccess = { _selectedCard.value = it },
                onFailure = { }
            )
        }
    }
    
    fun processPayment(amount: Double, merchantId: String, merchantName: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            quickPayUseCase(amount, merchantId, merchantName).fold(
                onSuccess = { result ->
                    _paymentState.value = if (result.success) {
                        PaymentState.Success(result)
                    } else {
                        PaymentState.Failed(result.errorMessage ?: "Payment failed")
                    }
                },
                onFailure = { _paymentState.value = PaymentState.Failed(it.message ?: "Payment failed") }
            )
        }
    }
    
    fun resetState() { _paymentState.value = PaymentState.Idle }
}

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Processing : PaymentState()
    data class Success(val result: PaymentResult) : PaymentState()
    data class Failed(val message: String) : PaymentState()
}

// Transaction History ViewModel
class TransactionHistoryViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val transactions: StateFlow<List<Transaction>> = observeTransactionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init { loadTransactions() }
    
    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            getTransactionHistoryUseCase(1, 20)
            _isLoading.value = false
        }
    }
    
    fun refresh() = loadTransactions()
}
