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

/**
 * ViewModel managing the primary Home Screen feed state.
 *
 * Parallelizes async data fetching for news stories and active community needs using [async]
 * coroutine scope deferrals, reducing initial load latency.
 *
 * @property repository Single source of truth repository for platform API interactions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow(HomeFeedState())

    /** Observable reactive StateFlow exposed to the home screen UI layer. */
    val feedState: StateFlow<HomeFeedState> = _feedState.asStateFlow()

    private var isLoaded = false

    /**
     * Triggers asynchronous fetching of home screen content (stories & needs) in parallel.
     *
     * @param forceRefresh When `true`, bypasses in-memory caching and re-queries backend endpoints.
     */
    fun loadHomeFeed(forceRefresh: Boolean = false) {
        if (isLoaded && !forceRefresh) return

        _feedState.value = _feedState.value.copy(isLoading = !isLoaded)

        viewModelScope.launch {
            // Fetch news stories and community needs concurrently
            val newsDeferred = async { repository.getNews() }
            val needsDeferred = async { repository.getNeeds() }

            val newsResult = newsDeferred.await()
            val needsResult = needsDeferred.await()

            isLoaded = true

            val storiesList = when (newsResult) {
                is NetworkResult.Success -> newsResult.data ?: emptyList()
                else -> emptyList()
            }

            val needsList = when (needsResult) {
                is NetworkResult.Success -> needsResult.data ?: emptyList()
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
