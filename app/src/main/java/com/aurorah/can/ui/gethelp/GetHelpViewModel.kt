package com.aurorah.can.ui.gethelp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.AssistanceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GetHelpUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val countryCode: String = "+27",
    val phone: String = "",
    val location: String = "",
    val selectedCategory: String = "",
    val description: String = "",
    val selectedUrgency: String = "Standard",
    val popiaConsent: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneError: String? = null,
    val locationError: String? = null,
    val categoryError: String? = null,
    val descriptionError: String? = null,
    val popiaError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val submittedFullName: String = "",
    val serverError: String? = null
)

internal object GetHelpFormValidator {
    fun validate(state: GetHelpUiState): GetHelpUiState {
        val phoneDigits = state.phone.filter { it.isDigit() }
        return state.copy(
            firstNameError = if (state.firstName.isBlank()) "First name is required" else null,
            lastNameError = if (state.lastName.isBlank()) "Last name is required" else null,
            phoneError = when {
                state.phone.isBlank() -> "Phone number is required"
                phoneDigits.length < 7 -> "Please enter a valid phone number"
                else -> null
            },
            locationError = if (state.location.isBlank()) "Suburb or area is required" else null,
            categoryError = if (state.selectedCategory.isBlank()) "Please select a type of support" else null,
            descriptionError = if (state.description.isBlank()) "Please describe what assistance you need" else null,
            popiaError = if (!state.popiaConsent) "Consent is required to submit request" else null
        )
    }

    fun hasErrors(state: GetHelpUiState): Boolean =
        state.firstNameError != null || state.lastNameError != null ||
            state.phoneError != null || state.locationError != null ||
            state.categoryError != null || state.descriptionError != null ||
            state.popiaError != null
}

class GetHelpViewModel(
    application: Application,
    private val repository: ApiRepository = ApiRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GetHelpUiState())
    val uiState: StateFlow<GetHelpUiState> = _uiState.asStateFlow()

    fun initUserInfo(userEmail: String, userName: String, userPhone: String) {
        val split = userName.trim().split(" ")
        val first = split.firstOrNull() ?: ""
        val last = if (split.size > 1) split.drop(1).joinToString(" ") else ""
        val email = if (userEmail != "guest@aurorahcan.org") userEmail else ""

        _uiState.update {
            it.copy(
                firstName = it.firstName.ifBlank { first },
                lastName = it.lastName.ifBlank { last },
                email = it.email.ifBlank { email },
                phone = it.phone.ifBlank { userPhone }
            )
        }
    }

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value, firstNameError = null) }
    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value, lastNameError = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onCountryCodeChange(value: String) = _uiState.update { it.copy(countryCode = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, phoneError = null) }
    fun onLocationChange(value: String) = _uiState.update { it.copy(location = value, locationError = null) }
    fun onCategoryChange(value: String) = _uiState.update { it.copy(selectedCategory = value, categoryError = null) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value, descriptionError = null) }
    fun onUrgencyChange(value: String) = _uiState.update { it.copy(selectedUrgency = value) }
    fun onPopiaConsentChange(value: Boolean) = _uiState.update { it.copy(popiaConsent = value, popiaError = null) }

    fun submitRequest() {
        val state = _uiState.value
        val validatedState = GetHelpFormValidator.validate(state)
        if (GetHelpFormValidator.hasErrors(validatedState)) {
            _uiState.update { GetHelpFormValidator.validate(it) }
            return
        }

        _uiState.update { it.copy(isLoading = true, serverError = null) }

        viewModelScope.launch {
            val fullName = "${state.firstName.trim()} ${state.lastName.trim()}".trim()
            val fullPhone = "${state.countryCode}${state.phone.trim()}"

            val request = AssistanceRequest(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                countryCode = state.countryCode,
                category = state.selectedCategory,
                urgency = state.selectedUrgency,
                areaLocation = state.location.trim(),
                contactPhone = fullPhone,
                description = state.description.trim(),
                privacyConsent = state.popiaConsent,
                userEmail = state.email.trim()
            )

            val apiResponse = repository.requestAssistance(request)
            if (apiResponse.success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        submittedFullName = fullName
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        serverError = apiResponse.message.ifBlank { "Failed to submit request. Please check connection and try again." }
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update {
            GetHelpUiState(
                email = it.email,
                firstName = "",
                lastName = "",
                phone = "",
                location = "",
                selectedCategory = "",
                description = "",
                selectedUrgency = "Standard",
                popiaConsent = false
            )
        }
    }
}
