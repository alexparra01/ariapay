package com.ariapay.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariapay.domain.usecases.CheckAuthStatusUseCase
import com.ariapay.domain.usecases.GetCurrentUserUseCase
import com.ariapay.domain.usecases.LoginUseCase
import com.ariapay.domain.usecases.LogoutUseCase
import com.ariapay.ui.models.AuthState
import com.ariapay.ui.models.BiometricState
import com.ariapay.ui.models.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _biometricEnabled = MutableStateFlow<BiometricState>(BiometricState.Idle)
    val biometricEnabled: StateFlow<BiometricState> = _biometricEnabled.asStateFlow()



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
