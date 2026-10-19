package com.aurorah.can.ui.gethelp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.local.SessionManager

class GetHelpFragment : Fragment() {

    private val repository by lazy { ApiRepository(requireContext()) }
    private val sessionManager by lazy { SessionManager(requireContext()) }

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
                    repository = repository,
                    userEmail = userEmail,
                    userName = userName,
                    userPhone = userPhone
                )
            }
        }
    }
}
