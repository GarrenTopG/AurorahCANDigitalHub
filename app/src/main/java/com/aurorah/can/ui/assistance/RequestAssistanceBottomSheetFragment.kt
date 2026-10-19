package com.aurorah.can.ui.assistance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.R
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.AssistanceRequest
import com.aurorah.can.databinding.FragmentRequestAssistanceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RequestAssistanceBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRequestAssistanceBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { ApiRepository(requireContext()) }

    private val categories = listOf("Food", "Clothing", "Education", "Healthcare")
    private val urgencies = listOf("Critical", "High", "Medium", "Low")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestAssistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupValidation()
        setupSubmitButton()
    }

    private fun setupSpinners() {
        binding.actvCategory.setSimpleItems(categories.toTypedArray())
        binding.actvCategory.setText(categories[0], false)

        binding.actvUrgency.setSimpleItems(urgencies.toTypedArray())
        binding.actvUrgency.setText(urgencies[2], false) // Default to Medium
    }

    private fun setupValidation() {
        binding.etAreaLocation.doOnTextChanged { _, _, _, _ -> binding.tilAreaLocation.error = null }
        binding.etContactPhone.doOnTextChanged { _, _, _, _ -> binding.tilContactPhone.error = null }
        binding.etDescription.doOnTextChanged { _, _, _, _ -> binding.tilDescription.error = null }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitAssistance.setOnClickListener {
            val category = binding.actvCategory.text.toString().ifBlank { categories[0] }
            val urgency = binding.actvUrgency.text.toString().ifBlank { urgencies[2] }
            val areaLocation = binding.etAreaLocation.text.toString().trim()
            val contactPhone = binding.etContactPhone.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val isConsentChecked = binding.cbPrivacyConsent.isChecked

            var isValid = true

            if (areaLocation.isEmpty()) {
                binding.tilAreaLocation.error = getString(R.string.error_empty_field)
                isValid = false
            }

            if (contactPhone.isEmpty()) {
                binding.tilContactPhone.error = getString(R.string.error_empty_field)
                isValid = false
            }

            if (description.isEmpty()) {
                binding.tilDescription.error = getString(R.string.error_empty_field)
                isValid = false
            }

            if (!isConsentChecked) {
                Toast.makeText(requireContext(), getString(R.string.error_privacy_consent), Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            val sessionManager = SessionManager(requireContext())
            val userEmail = sessionManager.fetchUserEmail()

            binding.pbAssistance.visibility = View.VISIBLE
            binding.btnSubmitAssistance.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                val response = repository.requestAssistance(
                    AssistanceRequest(
                        category = category,
                        urgency = urgency,
                        areaLocation = areaLocation,
                        contactPhone = contactPhone,
                        description = description,
                        privacyConsent = isConsentChecked,
                        userEmail = userEmail
                    )
                )

                _binding?.let { b ->
                    b.pbAssistance.visibility = View.GONE
                    b.btnSubmitAssistance.isEnabled = true
                }

                if (isAdded) {
                    if (response.success) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Request Submitted")
                            .setMessage(response.message.ifBlank { "Your assistance request has been received and prioritized." })
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                dismiss()
                            }
                            .show()
                    } else {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
