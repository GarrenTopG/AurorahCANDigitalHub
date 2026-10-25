package com.aurorah.can.ui.needs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.OfferAssistanceRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NeedsUiState(
    val needsList: List<CommunityNeed> = emptyList(),
    val filteredNeeds: List<CommunityNeed> = emptyList(),
    val selectedFilter: String = "All",
    val isLoading: Boolean = true,
    val selectedNeedForOffer: CommunityNeed? = null,
    val selectedNeedForDetail: CommunityNeed? = null,
    val isSubmittingOffer: Boolean = false,
    val offerSuccessMessage: String? = null,
    val offerErrorMessage: String? = null
)

@HiltViewModel
class NeedsViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NeedsUiState())
    val uiState: StateFlow<NeedsUiState> = _uiState.asStateFlow()

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

    init {
        loadNeeds()
    }

    fun loadNeeds() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.getNeeds()
            val list = when (result) {
                is NetworkResult.Success -> if (result.data.isNotEmpty()) result.data else defaultNeeds
                else -> defaultNeeds
            }
            _uiState.update { state ->
                val filtered = applyFilter(list, state.selectedFilter)
                state.copy(
                    needsList = list,
                    filteredNeeds = filtered,
                    isLoading = false
                )
            }
        }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { state ->
            val filtered = applyFilter(state.needsList, filter)
            state.copy(selectedFilter = filter, filteredNeeds = filtered)
        }
    }

    private fun applyFilter(list: List<CommunityNeed>, filter: String): List<CommunityNeed> {
        return if (filter.equals("All", ignoreCase = true)) {
            list
        } else {
            list.filter { it.category.equals(filter, ignoreCase = true) }
        }
    }

    fun selectNeedForDetail(need: CommunityNeed?) {
        _uiState.update { it.copy(selectedNeedForDetail = need) }
    }

    fun selectNeedForOffer(need: CommunityNeed?) {
        _uiState.update { it.copy(selectedNeedForOffer = need, offerErrorMessage = null) }
    }

    fun submitOffer(needId: Int, skill: String, notes: String) {
        val userEmail = sessionManager.fetchUserEmail()
        _uiState.update { it.copy(isSubmittingOffer = true, offerErrorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.offerAssistance(
                OfferAssistanceRequest(
                    needId = needId,
                    userEmail = userEmail,
                    volunteerSkill = skill,
                    notes = notes
                )
            )) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _uiState.update {
                        it.copy(
                            isSubmittingOffer = false,
                            selectedNeedForOffer = null,
                            offerSuccessMessage = response.message.ifBlank { "Offer submitted successfully!" }
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmittingOffer = false,
                            offerErrorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(offerSuccessMessage = null, offerErrorMessage = null) }
    }
}
