package com.aurorah.can.ui.needs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.OfferAssistanceRequest
import com.aurorah.can.databinding.BottomSheetOfferAssistanceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class OfferAssistanceBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOfferAssistanceBinding? = null
    private val binding get() = _binding!!

    private val repository = MockApiRepository()

    private var needId: Int = 0
    private var needTitle: String = ""

    companion object {
        private const val ARG_NEED_ID = "need_id"
        private const val ARG_NEED_TITLE = "need_title"

        fun newInstance(needId: Int, needTitle: String): OfferAssistanceBottomSheetFragment {
            val fragment = OfferAssistanceBottomSheetFragment()
            val args = Bundle().apply {
                putInt(ARG_NEED_ID, needId)
                putString(ARG_NEED_TITLE, needTitle)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            needId = it.getInt(ARG_NEED_ID)
            needTitle = it.getString(ARG_NEED_TITLE, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOfferAssistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNeedTitleInSheet.text = needTitle

        binding.btnSubmitOffer.setOnClickListener {
            val skill = binding.etSkill.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (skill.isEmpty()) {
                binding.tilSkill.error = "Please enter your skill or specialty"
                return@setOnClickListener
            } else {
                binding.tilSkill.error = null
            }

            val sessionManager = SessionManager(requireContext())
            val userEmail = sessionManager.fetchUserEmail()

            binding.pbOffer.visibility = View.VISIBLE
            binding.btnSubmitOffer.isEnabled = false

            lifecycleScope.launch {
                val response = repository.offerAssistance(
                    OfferAssistanceRequest(
                        needId = needId,
                        userEmail = userEmail,
                        volunteerSkill = skill,
                        notes = notes
                    )
                )
                binding.pbOffer.visibility = View.GONE
                binding.btnSubmitOffer.isEnabled = true

                Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
