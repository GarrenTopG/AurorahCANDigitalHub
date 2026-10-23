package com.aurorah.can.ui.needs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.OfferAssistanceRequest
import com.aurorah.can.databinding.BottomSheetOfferAssistanceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OfferAssistanceBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOfferAssistanceBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: ApiRepository

    @Inject
    lateinit var sessionManager: SessionManager

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
        setupInputValidation()

        binding.btnSubmitOffer.setOnClickListener {
            val skill = binding.etSkill.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (skill.isEmpty()) {
                binding.tilSkill.error = "Please enter your skill or specialty"
                return@setOnClickListener
            } else {
                binding.tilSkill.error = null
            }

            val userEmail = sessionManager.fetchUserEmail()

            binding.pbOffer.visibility = View.VISIBLE
            binding.btnSubmitOffer.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                val result = repository.offerAssistance(
                    OfferAssistanceRequest(
                        needId = needId,
                        userEmail = userEmail,
                        volunteerSkill = skill,
                        notes = notes
                    )
                )
                _binding?.let { b ->
                    b.pbOffer.visibility = View.GONE
                    b.btnSubmitOffer.isEnabled = true
                }

                if (isAdded) {
                    val msg = when (result) {
                        is NetworkResult.Success -> result.data.message
                        is NetworkResult.Error -> result.message
                        is NetworkResult.Loading -> ""
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
        }
    }

    private fun setupInputValidation() {
        binding.etSkill.doOnTextChanged { _, _, _, _ ->
            binding.tilSkill.error = null
        }
        binding.etNotes.doOnTextChanged { _, _, _, _ ->
            binding.tilNotes.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
