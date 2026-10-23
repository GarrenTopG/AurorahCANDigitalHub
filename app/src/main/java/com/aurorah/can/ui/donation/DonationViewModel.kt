package com.aurorah.can.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.DonationRequest
import com.aurorah.can.data.model.DonationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationUiState(
    val isMonetaryTab: Boolean = true,
    val selectedCategory: String = "General fund — where most needed",
    val selectedAmount: Double = 200.0,
    val isCustomAmountSelected: Boolean = false,
    val customAmountText: String = "",
    val donorName: String = "",
    val donorEmail: String = "",
    val phone: String = "",
    val countryCode: String = "+27",
    val message: String = "",
    val isAnonymous: Boolean = false,
    val isTaxCertRequested: Boolean = false,
    val taxIdNumber: String = "",
    val taxAddress: String = "",
    val inKindCategory: String = "Non-Perishable Food / Canned Goods",
    val itemDescription: String = "",
    val isPickup: Boolean = false,
    val pickupAddress: String = "",
    val popiaConsent: Boolean = true,
    val customAmountError: String? = null,
    val itemDescError: String? = null,
    val taxIdError: String? = null,
    val taxAddressError: String? = null,
    val donorNameError: String? = null,
    val donorEmailError: String? = null,
    val pickupAddressError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val donationResponse: DonationResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationUiState())
    val uiState: StateFlow<DonationUiState> = _uiState.asStateFlow()

    init {
        prefillUserInfo()
    }

    private fun prefillUserInfo() {
        val savedEmail = sessionManager.fetchUserEmail()
        val savedName = sessionManager.fetchUserName()

        _uiState.update { state ->
            state.copy(
                donorEmail = savedEmail.ifBlank { state.donorEmail },
                donorName = if (savedName.isNotBlank() && savedName != "User") savedName else state.donorName
            )
        }
    }

    fun onTabSelected(isMonetary: Boolean) {
        _uiState.update { it.copy(isMonetaryTab = isMonetary, errorMessage = null) }
    }

    fun onMonetaryCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onInKindCategorySelected(category: String) {
        _uiState.update { it.copy(inKindCategory = category) }
    }

    fun onAmountSelected(amount: Double, isCustom: Boolean) {
        _uiState.update {
            it.copy(
                selectedAmount = amount,
                isCustomAmountSelected = isCustom,
                customAmountError = null
            )
        }
    }

    fun onCustomAmountChanged(text: String) {
        val input = text.toDoubleOrNull()
        _uiState.update {
            it.copy(
                customAmountText = text,
                selectedAmount = if (input != null && input > 0.0) input else it.selectedAmount,
                customAmountError = null
            )
        }
    }

    fun onDonorNameChanged(name: String) {
        _uiState.update { it.copy(donorName = name, donorNameError = null) }
    }

    fun onDonorEmailChanged(email: String) {
        _uiState.update { it.copy(donorEmail = email, donorEmailError = null) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onCountryCodeChanged(code: String) {
        _uiState.update { it.copy(countryCode = code) }
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun onItemDescriptionChanged(desc: String) {
        _uiState.update { it.copy(itemDescription = desc, itemDescError = null) }
    }

    fun onTaxCertToggled(isChecked: Boolean) {
        _uiState.update {
            it.copy(
                isTaxCertRequested = isChecked,
                isAnonymous = if (isChecked) false else it.isAnonymous,
                taxIdError = null,
                taxAddressError = null,
                donorNameError = null,
                donorEmailError = null
            )
        }
    }

    fun onTaxIdChanged(taxId: String) {
        _uiState.update { it.copy(taxIdNumber = taxId, taxIdError = null) }
    }

    fun onTaxAddressChanged(address: String) {
        _uiState.update { it.copy(taxAddress = address, taxAddressError = null) }
    }

    fun onPickupToggled(isPickup: Boolean) {
        _uiState.update { it.copy(isPickup = isPickup, pickupAddressError = null) }
    }

    fun onPickupAddressChanged(address: String) {
        _uiState.update { it.copy(pickupAddress = address, pickupAddressError = null) }
    }

    fun onAnonymousToggled(isChecked: Boolean) {
        _uiState.update {
            it.copy(
                isAnonymous = isChecked,
                donorName = if (isChecked) "Anonymous Donor" else "",
                isTaxCertRequested = if (isChecked) false else it.isTaxCertRequested
            )
        }
    }

    fun onPopiaConsentChanged(isChecked: Boolean) {
        _uiState.update { it.copy(popiaConsent = isChecked) }
    }

    fun submitDonation() {
        val state = _uiState.value

        if (!state.popiaConsent) {
            _uiState.update { it.copy(errorMessage = "Please accept the privacy information to proceed.") }
            return
        }

        if (state.isTaxCertRequested) {
            var hasTaxErr = false
            var taxIdErr: String? = null
            var taxAddrErr: String? = null
            var nameErr: String? = null
            var emailErr: String? = null

            if (state.taxIdNumber.isBlank()) {
                taxIdErr = "Tax reference / ID required for Section 18A certificate"
                hasTaxErr = true
            }

            if (state.taxAddress.isBlank()) {
                taxAddrErr = "Tax address required for Section 18A certificate"
                hasTaxErr = true
            }

            if (state.donorName.isBlank() || state.isAnonymous) {
                nameErr = "Your name is required for Section 18A certificate"
                hasTaxErr = true
            }

            if (state.donorEmail.isBlank()) {
                emailErr = "Email address is required for Section 18A certificate"
                hasTaxErr = true
            }

            if (hasTaxErr) {
                _uiState.update {
                    it.copy(
                        taxIdError = taxIdErr,
                        taxAddressError = taxAddrErr,
                        donorNameError = nameErr,
                        donorEmailError = emailErr
                    )
                }
                return
            }
        }

        val nameToUse = state.donorName.trim().ifBlank {
            if (state.isAnonymous) "Anonymous Donor" else "Friend of Aurorah"
        }

        val request: DonationRequest
        if (state.isMonetaryTab) {
            if (state.isCustomAmountSelected) {
                val parsed = state.customAmountText.toDoubleOrNull()
                if (parsed == null || parsed <= 0.0) {
                    _uiState.update { it.copy(customAmountError = "Please enter a valid donation amount") }
                    return
                }
            }

            request = DonationRequest(
                donationType = "Monetary",
                category = state.selectedCategory,
                amount = state.selectedAmount,
                donorName = nameToUse,
                userEmail = state.donorEmail.trim(),
                phone = state.phone.trim(),
                countryCode = state.countryCode.takeLast(3).ifBlank { "+27" },
                message = state.message.trim(),
                isAnonymous = state.isAnonymous,
                requestTaxCertificate = state.isTaxCertRequested,
                taxIdNumber = if (state.isTaxCertRequested) state.taxIdNumber.trim() else null,
                taxAddress = if (state.isTaxCertRequested) state.taxAddress.trim() else null,
                popiaConsent = state.popiaConsent
            )
        } else {
            if (state.itemDescription.isBlank()) {
                _uiState.update { it.copy(itemDescError = "Field cannot be empty") }
                return
            }

            if (state.isPickup && state.pickupAddress.isBlank()) {
                _uiState.update { it.copy(pickupAddressError = "Collection address required") }
                return
            }

            val preference = if (state.isPickup) "Request Collection" else "Drop-off at Hub"
            request = DonationRequest(
                donationType = "In-Kind",
                category = state.inKindCategory,
                itemDescription = state.itemDescription.trim(),
                preference = preference,
                pickupAddress = if (state.isPickup) state.pickupAddress.trim() else null,
                donorName = nameToUse,
                userEmail = state.donorEmail.trim(),
                phone = state.phone.trim(),
                countryCode = state.countryCode.takeLast(3).ifBlank { "+27" },
                message = state.message.trim(),
                isAnonymous = state.isAnonymous,
                popiaConsent = state.popiaConsent
            )
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.submitDonation(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                donationResponse = response
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.message.ifBlank { "Donation processing failed." }
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

    fun resetForm() {
        _uiState.update {
            it.copy(
                itemDescription = "",
                taxIdNumber = "",
                taxAddress = "",
                pickupAddress = "",
                message = "",
                isTaxCertRequested = false,
                isSuccess = false,
                donationResponse = null,
                errorMessage = null
            )
        }
    }
}
