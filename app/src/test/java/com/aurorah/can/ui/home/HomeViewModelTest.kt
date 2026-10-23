package com.aurorah.can.ui.home

import app.cash.turbine.test
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `loadHomeFeed success updates stories and needs`() = runTest {
        val newsList = listOf(NewsItem(id = 1, title = "News 1"))
        val needsList = listOf(CommunityNeed(needId = 1, title = "Need 1"))

        whenever(repository.getNews()).thenReturn(NetworkResult.Success(newsList))
        whenever(repository.getNeeds()).thenReturn(NetworkResult.Success(needsList))

        viewModel.feedState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            viewModel.loadHomeFeed()

            val loadedState = awaitItem()
            assertEquals(newsList, loadedState.stories)
            assertEquals(needsList, loadedState.needs)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.newsError)
            assertNull(loadedState.needsError)
        }
    }

    @Test
    fun `loadHomeFeed error sets error messages`() = runTest {
        whenever(repository.getNews()).thenReturn(NetworkResult.Error("News failed"))
        whenever(repository.getNeeds()).thenReturn(NetworkResult.Error("Needs failed"))

        viewModel.feedState.test {
            awaitItem() // Initial state

            viewModel.loadHomeFeed()

            val errorState = awaitItem()
            assertEquals("News failed", errorState.newsError)
            assertEquals("Needs failed", errorState.needsError)
            assertFalse(errorState.isLoading)
        }
    }

    @Test
    fun `loadHomeFeed caches data and does not reload without forceRefresh`() = runTest {
        whenever(repository.getNews()).thenReturn(NetworkResult.Success(emptyList()))
        whenever(repository.getNeeds()).thenReturn(NetworkResult.Success(emptyList()))

        viewModel.loadHomeFeed()
        viewModel.loadHomeFeed()

        verify(repository, times(1)).getNews()
        verify(repository, times(1)).getNeeds()
    }

    @Test
    fun `loadHomeFeed with forceRefresh reloads data`() = runTest {
        whenever(repository.getNews()).thenReturn(NetworkResult.Success(emptyList()))
        whenever(repository.getNeeds()).thenReturn(NetworkResult.Success(emptyList()))

        viewModel.loadHomeFeed()
        viewModel.loadHomeFeed(forceRefresh = true)

        verify(repository, times(2)).getNews()
        verify(repository, times(2)).getNeeds()
    }
}
