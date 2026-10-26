package com.aurorah.can.ui.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aurorah.can.ui.getinvolved.DonateSubTab
import com.aurorah.can.ui.theme.AurorahTheme
import dagger.hilt.android.AndroidEntryPoint

@Deprecated(
    message = "DonationFragment is deprecated in favor of Compose component DonateSubTab.",
    replaceWith = ReplaceWith("DonateSubTab", "com.aurorah.can.ui.getinvolved.DonateSubTab")
)
@AndroidEntryPoint
class DonationFragment : Fragment() {

    private val viewModel: DonationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AurorahTheme {
                    DonateSubTab(viewModel = viewModel)
                }
            }
        }
    }
}
