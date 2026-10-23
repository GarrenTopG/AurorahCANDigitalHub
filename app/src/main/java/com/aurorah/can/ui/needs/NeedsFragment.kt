package com.aurorah.can.ui.needs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aurorah.can.ui.getinvolved.CommunityNeedsSubTab
import dagger.hilt.android.AndroidEntryPoint

@Deprecated(
    message = "NeedsFragment is deprecated in favor of Compose component CommunityNeedsSubTab.",
    replaceWith = ReplaceWith("CommunityNeedsSubTab", "com.aurorah.can.ui.getinvolved.CommunityNeedsSubTab")
)
@AndroidEntryPoint
class NeedsFragment : Fragment() {

    private val viewModel: NeedsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CommunityNeedsSubTab(viewModel = viewModel)
            }
        }
    }
}
