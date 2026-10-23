package com.aurorah.can.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.ChangePasswordRequest
import com.aurorah.can.data.model.UpdateProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userRole: String = "",
    val isNightMode: Boolean = false,
    val areNotificationsEnabled: Boolean = true,
    val isDataSaverEnabled: Boolean = false,
    val searchRadius: String = "10 km",
    val appLanguage: String = "English",
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
    val isLoggedOut: Boolean = false,
    val isAccountDeleted: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserSettings()
    }

    fun loadUserSettings() {
        _uiState.update {
            it.copy(
                userName = sessionManager.fetchUserName(),
                userEmail = sessionManager.fetchUserEmail(),
                userPhone = sessionManager.fetchUserPhone(),
                userRole = sessionManager.fetchUserRole(),
                isNightMode = sessionManager.isNightModeEnabled(),
                areNotificationsEnabled = sessionManager.areNotificationsEnabled(),
                isDataSaverEnabled = sessionManager.isDataSaverEnabled(),
                searchRadius = sessionManager.getSearchRadius(),
                appLanguage = sessionManager.getAppLanguage()
            )
        }
    }

    fun updateProfile(newName: String, newPhone: String) {
        val email = sessionManager.fetchUserEmail()
        _uiState.update { it.copy(isLoading = true, toastMessage = null) }

        viewModelScope.launch {
            when (val result = repository.updateProfile(UpdateProfileRequest(fullName = newName, phone = newPhone, email = email))) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success) {
                        sessionManager.updateUserProfile(newName, newPhone)
                        loadUserSettings()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                toastMessage = response.message.ifBlank { "Profile updated successfully" }
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                toastMessage = response.message
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun setNightMode(enabled: Boolean) {
        sessionManager.setNightModeEnabled(enabled)
        _uiState.update { it.copy(isNightMode = enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sessionManager.setNotificationsEnabled(enabled)
        val stateText = if (enabled) "enabled" else "disabled"
        _uiState.update { it.copy(areNotificationsEnabled = enabled, toastMessage = "Notifications $stateText") }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        sessionManager.setDataSaverEnabled(enabled)
        val stateText = if (enabled) "enabled" else "disabled"
        _uiState.update { it.copy(isDataSaverEnabled = enabled, toastMessage = "Data Saver $stateText") }
    }

    fun setSearchRadius(radius: String) {
        sessionManager.setSearchRadius(radius)
        _uiState.update { it.copy(searchRadius = radius, toastMessage = "Search radius set to $radius") }
    }

    fun setAppLanguage(language: String) {
        sessionManager.setAppLanguage(language)
        _uiState.update { it.copy(appLanguage = language, toastMessage = "Language set to $language") }
    }

    fun changePassword(oldPass: String, newPass: String) {
        val email = sessionManager.fetchUserEmail()
        _uiState.update { it.copy(isLoading = true, toastMessage = null) }

        viewModelScope.launch {
            when (val result = repository.changePassword(ChangePasswordRequest(email = email, oldPassword = oldPass, newPassword = newPass))) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = result.data.message
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun signOut() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    fun deleteAccount() {
        _uiState.update { it.copy(isLoading = true, toastMessage = null) }

        viewModelScope.launch {
            when (val result = repository.deleteAccount()) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success) {
                        sessionManager.clearSession()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isAccountDeleted = true,
                                toastMessage = "Account deleted successfully"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                toastMessage = response.message.ifBlank { "Failed to delete account" }
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            toastMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
