package com.aurorah.can.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.CommunityNeed
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
    val selectedFundCategory: String = "General fund — where most needed",
    val communityNeeds: List<CommunityNeed> = emptyList(),
    val selectedCommunityNeed: CommunityNeed? = null,
    val isLoadingNeeds: Boolean = false,
    val isMonetaryTab: Boolean = true,
    val selectedCategory: String = "General fund — where most needed",
    val selectedAmount: Double = 200.0,
    val isCustomAmountSelected: Boolean = false,
    val customAmountText: String = "",
    val requestTaxCertificate: Boolean = false,
    val taxIdNumber: String = "",
    val taxAddress: String = "",
    val isAnonymous: Boolean = false,
    val donorName: String = "",
    val donorEmail: String = "",
    val phone: String = "",
    val countryCode: String = "+27",
    val message: String = "",
    val inKindCategory: String = "Non-Perishable Food / Canned Goods",
    val itemDescription: String = "",
    val isPickup: Boolean = false,
    val pickupAddress: String = "",
    val popiaConsent: Boolean = true,
    val customAmountError: String? = null,
    val taxError: String? = null,
    val itemDescError: String? = null,
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
        loadCommunityNeeds()
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

    private val defaultNeeds = listOf(
        CommunityNeed(
            needId = 1,
            title = "Warm tracksuits and shoes for young athletes.",
            category = "Clothing",
            location = "Kuils River",
            priority = "High",
            description = "Empowering Kuils River's Youth: How Donating Tracksuits and Shoes Transforms Local Athletes.",
            articleExcerpt = "In every community, sport serves as a powerful catalyst for growth, discipline, and unity. Many of our local youth athletes lack suitable sportswear and durable running shoes.",
            article = "Empowering Kuils River's Youth: How Donating Tracksuits and Shoes Transforms Local Athletes.\n\nIn every community, sport serves as a powerful catalyst for growth, discipline, and unity. Many of our local youth athletes lack suitable sportswear and durable running shoes for training and competitions. By donating warm tracksuits and proper athletic shoes, you enable these young leaders to train with dignity, stay warm during cold morning practices, and perform at their best.",
            targetAmount = 5000.0,
            currentAmount = 3200.0,
            imageUrl = "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?auto=format&fit=crop&w=800&q=80",
            imageAlt = "Tracksuits and sports shoes for youth athletes",
            deadline = "2025-10-31"
        ),
        CommunityNeed(
            needId = 2,
            title = "Medical supplies and health kits for community clinic.",
            category = "Healthcare",
            location = "Kuils River",
            priority = "High",
            description = "Mobilising Support for Community Health: Essential Medical Supplies for Kuils River Clinic.",
            articleExcerpt = "Access to reliable healthcare resources remains a foundational pillar for community well-being. Our volunteer health outreach team needs basic medical supplies.",
            article = "Mobilising Support for Community Health: Essential Medical Supplies for Kuils River Clinic.\n\nAccess to reliable healthcare resources remains a foundational pillar for community well-being. Our volunteer health outreach team needs basic medical supplies including first-aid kits, blood pressure monitors, bandages, and antiseptic solutions to support home visits for elderly and vulnerable residents.",
            targetAmount = 8500.0,
            currentAmount = 8500.0,
            imageUrl = "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=800&q=80",
            imageAlt = "Medical supplies and first aid kits",
            deadline = "2025-11-15"
        ),
        CommunityNeed(
            needId = 3,
            title = "Science textbooks and lab goggles for high schoolers.",
            category = "Education",
            location = "Kuils River",
            priority = "Medium",
            description = "Empowering Future Scientists: Science Textbooks and Lab Goggles for Kuils River High Schoolers.",
            articleExcerpt = "Quality STEM education relies heavily on access to real-world learning tools. We are gathering physical science textbooks and protective lab gear.",
            article = "Empowering Future Scientists: Science Textbooks and Lab Goggles for Kuils River High Schoolers.\n\nQuality STEM education relies heavily on access to real-world learning tools. We are gathering physical science textbooks and protective lab gear to equip high school students preparing for matriculation exams.",
            targetAmount = 12000.0,
            currentAmount = 4500.0,
            imageUrl = "https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&w=800&q=80",
            imageAlt = "Science textbooks and laboratory equipment",
            deadline = "2025-12-01"
        )
    )

    fun loadCommunityNeeds() {
        _uiState.update { it.copy(isLoadingNeeds = true) }
        viewModelScope.launch {
            val result = repository.getNeeds()
            val list = when (result) {
                is NetworkResult.Success -> if (result.data.isNotEmpty()) result.data else defaultNeeds
                else -> defaultNeeds
            }
            _uiState.update { state ->
                state.copy(
                    communityNeeds = list,
                    isLoadingNeeds = false
                )
            }
        }
    }

    fun onFundCategorySelected(category: String) {
        val needMatch = _uiState.value.communityNeeds.find { it.title.equals(category, ignoreCase = true) }
        _uiState.update {
            it.copy(
                selectedFundCategory = category,
                selectedCategory = category,
                selectedCommunityNeed = needMatch,
                errorMessage = null
            )
        }
    }

    fun selectNeedById(needId: Int) {
        val match = _uiState.value.communityNeeds.find { it.needId == needId }
        if (match != null) {
            onFundCategorySelected(match.title)
        }
    }

    fun selectCommunityNeed(need: CommunityNeed?) {
        if (need == null) {
            onFundCategorySelected("General fund — where most needed")
        } else {
            onFundCategorySelected(need.title)
        }
    }

    fun onTabSelected(isMonetary: Boolean) {
        _uiState.update {
            val taxReq = if (!isMonetary) false else it.requestTaxCertificate
            it.copy(
                isMonetaryTab = isMonetary,
                requestTaxCertificate = taxReq,
                errorMessage = null
            )
        }
    }

    fun onRequestTaxCertificateToggled(checked: Boolean) {
        _uiState.update { state ->
            val isAnon = if (checked) false else state.isAnonymous
            state.copy(
                requestTaxCertificate = checked,
                isAnonymous = isAnon,
                taxError = null,
                donorName = if (isAnon) "Anonymous Donor" else if (state.donorName == "Anonymous Donor") "" else state.donorName
            )
        }
    }

    fun onTaxIdNumberChanged(taxId: String) {
        _uiState.update { it.copy(taxIdNumber = taxId, taxError = null) }
    }

    fun onTaxAddressChanged(address: String) {
        _uiState.update { it.copy(taxAddress = address, taxError = null) }
    }

    fun onAnonymousToggled(isAnon: Boolean) {
        _uiState.update { state ->
            val name = if (isAnon) "Anonymous Donor" else if (state.donorName == "Anonymous Donor") "" else state.donorName
            val taxReq = if (isAnon) false else state.requestTaxCertificate
            state.copy(
                isAnonymous = isAnon,
                requestTaxCertificate = taxReq,
                donorName = name
            )
        }
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
        val error = if (text.isNotBlank() && (input == null || input < 10.0)) {
            "Minimum custom donation amount is R10"
        } else {
            null
        }
        _uiState.update {
            it.copy(
                customAmountText = text,
                selectedAmount = if (input != null && input >= 10.0) input else 0.0,
                customAmountError = error
            )
        }
    }

    fun onDonorNameChanged(name: String) {
        _uiState.update { it.copy(donorName = name) }
    }

    fun onDonorEmailChanged(email: String) {
        _uiState.update { it.copy(donorEmail = email) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onCountryCodeChanged(code: String) {
        _uiState.update { it.copy(countryCode = code) }
    }

    fun onMessageChanged(msg: String) {
        _uiState.update { it.copy(message = msg) }
    }

    fun onItemDescriptionChanged(desc: String) {
        _uiState.update { it.copy(itemDescription = desc, itemDescError = null) }
    }

    fun onPickupToggled(isPickup: Boolean) {
        _uiState.update { it.copy(isPickup = isPickup, pickupAddressError = null) }
    }

    fun onPickupAddressChanged(address: String) {
        _uiState.update { it.copy(pickupAddress = address, pickupAddressError = null) }
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

        val request: DonationRequest
        if (state.isMonetaryTab) {
            if (state.isCustomAmountSelected) {
                val parsed = state.customAmountText.toDoubleOrNull()
                if (parsed == null || parsed < 10.0) {
                    _uiState.update { it.copy(customAmountError = "Minimum custom donation amount is R10") }
                    return
                }
            }

            if (state.requestTaxCertificate) {
                if (state.taxIdNumber.isBlank() || state.taxAddress.isBlank()) {
                    _uiState.update { it.copy(taxError = "Tax reference ID and tax address are required for Section 18A receipt") }
                    return
                }
                if (state.donorEmail.isBlank() || state.donorName.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Name and Email are required to issue a Section 18A tax certificate.") }
                    return
                }
            }

            request = DonationRequest(
                donationType = "Monetary",
                needId = state.selectedCommunityNeed?.needId,
                category = state.selectedFundCategory,
                amount = state.selectedAmount,
                donorName = state.donorName.ifBlank { "Friend of Aurorah" },
                userEmail = state.donorEmail.trim(),
                phone = state.phone.trim(),
                countryCode = state.countryCode.takeLast(3).ifBlank { "+27" },
                message = state.message.trim(),
                isAnonymous = state.isAnonymous,
                requestTaxCertificate = state.requestTaxCertificate,
                taxIdNumber = if (state.requestTaxCertificate) state.taxIdNumber.trim() else null,
                taxAddress = if (state.requestTaxCertificate) state.taxAddress.trim() else null,
                popiaConsent = state.popiaConsent
            )
        } else {
            if (state.itemDescription.isBlank()) {
                _uiState.update { it.copy(itemDescError = "Item description and quantity required") }
                return
            }

            if (state.isPickup && state.pickupAddress.isBlank()) {
                _uiState.update { it.copy(pickupAddressError = "Collection address is required for pickup") }
                return
            }

            val preference = if (state.isPickup) "Request Collection" else "Drop-off at Hub"
            request = DonationRequest(
                donationType = "In-Kind",
                needId = state.selectedCommunityNeed?.needId,
                category = state.inKindCategory,
                itemDescription = state.itemDescription.trim(),
                preference = preference,
                pickupAddress = if (state.isPickup) state.pickupAddress.trim() else null,
                donorName = state.donorName.ifBlank { "Friend of Aurorah" },
                userEmail = state.donorEmail.trim(),
                phone = state.phone.trim(),
                countryCode = state.countryCode.takeLast(3).ifBlank { "+27" },
                message = state.message.trim(),
                isAnonymous = state.isAnonymous,
                popiaConsent = state.popiaConsent
            )
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, taxError = null) }

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
                pickupAddress = "",
                customAmountText = "",
                message = "",
                taxIdNumber = "",
                taxAddress = "",
                requestTaxCertificate = false,
                customAmountError = null,
                taxError = null,
                itemDescError = null,
                pickupAddressError = null,
                isSuccess = false,
                donationResponse = null,
                errorMessage = null
            )
        }
    }
}
