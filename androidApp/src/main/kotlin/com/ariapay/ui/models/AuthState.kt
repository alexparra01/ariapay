package com.ariapay.ui.models

import com.ariapay.domain.model.User

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()

    data class Error(val message: String) : AuthState()
    data object BiometricSuccess: AuthState()
}