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
            description = "Supporting youth sports development in Kuils River.",
            articleExcerpt = "Supporting local youth athletes with sportswear and running shoes.",
            article = "Supporting local youth athletes with sportswear and running shoes for training and competitions."
        ),
        CommunityNeed(
            needId = 2,
            title = "Medical supplies and health kits for community clinic.",
            category = "Healthcare",
            location = "Kuils River",
            priority = "High",
            description = "Basic medical supplies for home visits and clinic outreach.",
            articleExcerpt = "Essential medical supplies for community clinic outreach.",
            article = "Basic medical supplies to support home visits and clinic care for elderly and vulnerable residents."
        ),
        CommunityNeed(
            needId = 3,
            title = "Science textbooks and lab goggles for high schoolers.",
            category = "Education",
            location = "Kuils River",
            priority = "Medium",
            description = "Learning tools for high school physical science students.",
            articleExcerpt = "Physical science textbooks and lab safety gear for high schoolers.",
            article = "Physical science textbooks and protective lab gear to equip high school students preparing for exams."
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
                is NetworkResult.Success -> result.data ?: emptyList()
                else -> defaultNeeds
            }
            _uiState.update { state ->
                val safeList = list
                val filtered = applyFilter(safeList, state.selectedFilter)
                state.copy(
                    needsList = safeList,
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
