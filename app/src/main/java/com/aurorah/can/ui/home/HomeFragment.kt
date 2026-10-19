package com.aurorah.can.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aurorah.can.R
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.ui.needs.OfferAssistanceBottomSheetFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val repository by lazy { ApiRepository(requireContext()) }
    private var feedState by mutableStateOf(HomeFeedState())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadHomeFeed()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AurorahHomeScreen(
                    feedState = feedState,
                    onRefresh = { loadHomeFeed() },
                    onRequestAssistance = {
                        findNavController().navigate(R.id.navigation_get_help)
                    },
                    onExploreNeeds = {
                        findNavController().navigate(R.id.navigation_volunteer)
                    },
                    onNeedClick = { need ->
                        val bottomSheet = OfferAssistanceBottomSheetFragment.newInstance(
                            needId = need.needId,
                            needTitle = need.title
                        )
                        bottomSheet.show(childFragmentManager, "OfferAssistanceBottomSheet")
                    },
                    onNewsClick = { newsItem ->
                        val detailBottomSheet = NewsDetailBottomSheetFragment.newInstance(newsItem)
                        detailBottomSheet.show(childFragmentManager, "NewsDetailBottomSheet")
                    }
                )
            }
        }
    }

    private fun loadHomeFeed() {
        feedState = feedState.copy(isLoading = true, newsError = null, needsError = null)

        viewLifecycleOwner.lifecycleScope.launch {
            val newsDeferred = async { repository.getNews() }
            val needsDeferred = async { repository.getNeeds() }

            val newsResult = newsDeferred.await()
            val needsResult = needsDeferred.await()

            feedState = HomeFeedState(
                stories = newsResult,
                needs = needsResult,
                isLoading = false,
                newsError = if (newsResult.isEmpty()) "Could not fetch news feed" else null,
                needsError = if (needsResult.isEmpty()) "Could not fetch needs feed" else null
            )
        }
    }
}
