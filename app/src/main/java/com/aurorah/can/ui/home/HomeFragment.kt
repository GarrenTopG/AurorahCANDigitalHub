package com.aurorah.can.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aurorah.can.R
import com.aurorah.can.ui.needs.OfferAssistanceBottomSheetFragment

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.loadHomeFeed()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val feedState by viewModel.feedState.collectAsState()

                AurorahHomeScreen(
                    feedState = feedState,
                    onRefresh = { viewModel.loadHomeFeed(forceRefresh = true) },
                    onRequestAssistance = {
                        findNavController().navigate(R.id.action_home_to_get_help)
                    },
                    onExploreNeeds = {
                        findNavController().navigate(R.id.action_home_to_volunteer)
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
}
