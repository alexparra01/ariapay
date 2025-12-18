package com.ariapay.ui.features.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariapay.domain.model.PaymentCard
import com.ariapay.domain.model.PaymentResult
import com.ariapay.domain.usecases.GetDefaultCardUseCase
import com.ariapay.domain.usecases.QuickPayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                onFailure = {  }
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
