package com.ariapay.ui.models

sealed class BiometricState {
    data object Success : BiometricState()
    data class Error(val message: String= "") : BiometricState()
    data object Idle : BiometricState()
}