package com.ariapay.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariapay.domain.model.Transaction
import com.ariapay.domain.model.Wallet
import com.ariapay.domain.usecases.GetTransactionHistoryUseCase
import com.ariapay.domain.usecases.GetWalletUseCase
import com.ariapay.domain.usecases.ObserveTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
