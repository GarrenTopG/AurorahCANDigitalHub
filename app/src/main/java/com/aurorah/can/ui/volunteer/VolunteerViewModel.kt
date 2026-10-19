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
        var hasError = false

        val firstNameErr = if (state.firstName.isBlank()) "First name is required" else null
        val lastNameErr = if (state.lastName.isBlank()) "Last name is required" else null
        val phoneDigits = state.phone.filter { it.isDigit() }
        val phoneErr = when {
            state.phone.isBlank() -> "Phone number is required"
            phoneDigits.length < 7 -> "Please enter a valid phone number"
            else -> null
        }
        val locationErr = if (state.location.isBlank()) "Location/Sub-area is required" else null
        val skillsErr = if (state.skills.isBlank()) "Please enter at least one skill" else null
        val daysErr = if (state.selectedDays.isEmpty()) "Please select at least one day" else null
        val popiaErr = if (!state.popiaConsent) "You must agree to the POPIA consent to submit" else null

        if (firstNameErr != null || lastNameErr != null || phoneErr != null ||
            locationErr != null || skillsErr != null || daysErr != null || popiaErr != null) {
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    firstNameError = firstNameErr,
                    lastNameError = lastNameErr,
                    phoneError = phoneErr,
                    locationError = locationErr,
                    skillsError = skillsErr,
                    daysError = daysErr,
                    popiaError = popiaErr
                )
            }
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
