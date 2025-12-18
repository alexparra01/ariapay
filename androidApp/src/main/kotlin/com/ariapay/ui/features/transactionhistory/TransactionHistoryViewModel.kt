package com.ariapay.ui.features.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariapay.domain.model.Transaction
import com.ariapay.domain.usecases.GetTransactionHistoryUseCase
import com.ariapay.domain.usecases.ObserveTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
