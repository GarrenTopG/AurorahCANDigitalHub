package com.aurorah.can.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow(HomeFeedState())
    val feedState: StateFlow<HomeFeedState> = _feedState.asStateFlow()

    private var isLoaded = false

    fun loadHomeFeed(forceRefresh: Boolean = false) {
        if (isLoaded && !forceRefresh) return

        _feedState.value = _feedState.value.copy(isLoading = !isLoaded)

        viewModelScope.launch {
            val newsDeferred = async { repository.getNews() }
            val needsDeferred = async { repository.getNeeds() }

            val newsResult = newsDeferred.await()
            val needsResult = needsDeferred.await()

            isLoaded = true

            val storiesList = when (newsResult) {
                is NetworkResult.Success -> newsResult.data
                else -> emptyList()
            }

            val needsList = when (needsResult) {
                is NetworkResult.Success -> needsResult.data
                else -> emptyList()
            }

            val newsErrorMsg = (newsResult as? NetworkResult.Error)?.message
            val needsErrorMsg = (needsResult as? NetworkResult.Error)?.message

            _feedState.value = HomeFeedState(
                stories = storiesList,
                needs = needsList,
                isLoading = false,
                newsError = newsErrorMsg,
                needsError = needsErrorMsg
            )
        }
    }
}
