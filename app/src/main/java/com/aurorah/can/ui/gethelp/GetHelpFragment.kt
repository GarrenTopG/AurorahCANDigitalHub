package com.aurorah.can.ui.gethelp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aurorah.can.data.local.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GetHelpFragment : Fragment() {

    private val viewModel: GetHelpViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userEmail = sessionManager.fetchUserEmail()
        val userName = sessionManager.fetchUserName()
        val userPhone = sessionManager.fetchUserPhone()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GetHelpScreen(
                    viewModel = viewModel,
                    userEmail = userEmail,
                    userName = userName,
                    userPhone = userPhone,
                    onBackClick = { findNavController().popBackStack() }
                )
            }
        }
    }
}
