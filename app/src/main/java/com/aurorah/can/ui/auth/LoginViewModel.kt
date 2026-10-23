package com.aurorah.can.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.LoginRequest
import com.aurorah.can.util.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userFullName: String = ""
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun hideError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password.trim()

        var isValid = true
        var emailErr: String? = null
        var passwordErr: String? = null

        if (!InputValidator.isValidEmail(email)) {
            emailErr = "Invalid Email Address"
            isValid = false
        }

        if (!InputValidator.isSecurePassword(password)) {
            passwordErr = "Password must be at least 8 chars with 1 number and 1 special char"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.login(LoginRequest(email, password))) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success && response.user != null) {
                        sessionManager.saveSession(
                            token = response.token ?: "authenticated-jwt-token",
                            email = response.user.email,
                            name = response.user.fullName,
                            role = response.user.role
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                userFullName = response.user.fullName
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.message.ifBlank { "Login failed" }
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
