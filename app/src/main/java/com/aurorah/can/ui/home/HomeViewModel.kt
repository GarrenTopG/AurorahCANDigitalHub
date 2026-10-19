package com.aurorah.can.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurorah.can.data.api.ApiRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(application)

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
            _feedState.value = HomeFeedState(
                stories = newsResult,
                needs = needsResult,
                isLoading = false,
                newsError = null,
                needsError = null
            )
        }
    }
}
