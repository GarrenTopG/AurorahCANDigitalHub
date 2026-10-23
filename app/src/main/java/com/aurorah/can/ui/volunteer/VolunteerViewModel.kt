package com.aurorah.can.ui.volunteer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.VolunteerRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VolunteerUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val countryCode: String = "+27",
    val phone: String = "",
    val location: String = "",
    val skills: String = "",
    val selectedDays: List<String> = emptyList(),
    val popiaConsent: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneError: String? = null,
    val locationError: String? = null,
    val skillsError: String? = null,
    val daysError: String? = null,
    val popiaError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val serverError: String? = null
)

internal object VolunteerFormValidator {
    fun validate(state: VolunteerUiState): VolunteerUiState {
        val phoneDigits = state.phone.filter { it.isDigit() }
        return state.copy(
            firstNameError = if (state.firstName.isBlank()) "First name is required" else null,
            lastNameError = if (state.lastName.isBlank()) "Last name is required" else null,
            phoneError = when {
                state.phone.isBlank() -> "Phone number is required"
                phoneDigits.length < 7 -> "Please enter a valid phone number"
                else -> null
            },
            locationError = if (state.location.isBlank()) "Location/Sub-area is required" else null,
            skillsError = if (state.skills.isBlank()) "Please enter at least one skill" else null,
            daysError = if (state.selectedDays.isEmpty()) "Please select at least one day" else null,
            popiaError = if (!state.popiaConsent) "You must agree to the POPIA consent to submit" else null
        )
    }

    fun hasErrors(state: VolunteerUiState): Boolean =
        state.firstNameError != null || state.lastNameError != null ||
            state.phoneError != null || state.locationError != null ||
            state.skillsError != null || state.daysError != null ||
            state.popiaError != null
}

class VolunteerViewModel(
    application: Application,
    private val repository: ApiRepository = ApiRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VolunteerUiState())
    val uiState: StateFlow<VolunteerUiState> = _uiState.asStateFlow()

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
    fun onSkillsChange(value: String) = _uiState.update { it.copy(skills = value, skillsError = null) }
    fun onPopiaConsentChange(value: Boolean) = _uiState.update { it.copy(popiaConsent = value, popiaError = null) }

    fun toggleDay(day: String) {
        _uiState.update { state ->
            val updated = state.selectedDays.toMutableList()
            if (updated.contains(day)) updated.remove(day) else updated.add(day)
            state.copy(selectedDays = updated, daysError = null)
        }
    }

    fun submitApplication() {
        val state = _uiState.value
        val validatedState = VolunteerFormValidator.validate(state)
        if (VolunteerFormValidator.hasErrors(validatedState)) {
            _uiState.update { VolunteerFormValidator.validate(it) }
            return
        }

        _uiState.update { it.copy(isLoading = true, serverError = null) }

        viewModelScope.launch {
            val request = VolunteerRequest(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                email = state.email.trim(),
                countryCode = state.countryCode,
                phone = state.phone.trim(),
                location = state.location.trim(),
                skills = state.skills.trim(),
                availability = state.selectedDays,
                popiaConsent = state.popiaConsent,
                userEmail = state.email.trim()
            )

            val apiResponse = repository.submitVolunteer(request)
            if (apiResponse.success) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        serverError = apiResponse.message.ifBlank { "Failed to submit application. Please check your connection and try again." }
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update {
            VolunteerUiState(
                email = it.email,
                firstName = "",
                lastName = "",
                phone = "",
                location = "",
                skills = "",
                selectedDays = emptyList(),
                popiaConsent = false
            )
        }
    }
}
