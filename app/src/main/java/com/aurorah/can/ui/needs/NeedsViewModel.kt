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

    init {
        loadNeeds()
    }

    fun loadNeeds() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.getNeeds()
            val list = when (result) {
                is NetworkResult.Success -> result.data
                else -> emptyList()
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
