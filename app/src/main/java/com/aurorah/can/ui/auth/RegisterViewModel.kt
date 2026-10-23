package com.aurorah.can.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.RegisterRequest
import com.aurorah.can.util.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val role: String = "Volunteer",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(fullName: String) {
        _uiState.update { it.copy(fullName = fullName, fullNameError = null, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
    }

    fun onRoleChanged(role: String) {
        _uiState.update { it.copy(role = role) }
    }

    fun hideError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun preWarmServer() {
        viewModelScope.launch {
            try {
                repository.getNews()
            } catch (_: Exception) {
                // Ignore pre-warm errors
            }
        }
    }

    fun register() {
        val state = _uiState.value
        val fullName = state.fullName.trim()
        val email = state.email.trim()
        val password = state.password.trim()
        val confirmPassword = state.confirmPassword.trim()
        val role = state.role.ifBlank { "Volunteer" }

        var isValid = true
        var nameErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null
        var confirmPassErr: String? = null

        if (fullName.isEmpty()) {
            nameErr = "Field cannot be empty"
            isValid = false
        }

        if (!InputValidator.isValidEmail(email)) {
            emailErr = "Invalid Email Address"
            isValid = false
        }

        if (!InputValidator.isSecurePassword(password)) {
            passErr = "Password must be at least 8 chars with 1 number and 1 special char"
            isValid = false
        }

        if (confirmPassword != password) {
            confirmPassErr = "Passwords do not match"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(
                    fullNameError = nameErr,
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmPassErr
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val request = RegisterRequest(
                fullName = fullName,
                email = email,
                password = password,
                role = role
            )

            when (val result = repository.register(request)) {
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
                                isSuccess = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.message.ifBlank { "Registration failed. Please try again." }
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
