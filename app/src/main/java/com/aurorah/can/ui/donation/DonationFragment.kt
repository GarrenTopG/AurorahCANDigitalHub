package com.aurorah.can.ui.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.R
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.DonationRequest
import com.aurorah.can.databinding.FragmentDonationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class DonationFragment : Fragment() {

    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    private val repository = MockApiRepository()

    private val categories = listOf("Food Parcels", "Winter Relief", "General Fund")
    private var selectedAmount: Double = 100.0
    private var isCustomAmountSelected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupAmountChips()
        setupCustomAmountValidation()
        setupSubmitButton()
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setText(categories[0], false)
    }

    private fun setupAmountChips() {
        binding.cgAmount.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            when (checkedIds.first()) {
                binding.chipR50.id -> {
                    selectedAmount = 50.0
                    isCustomAmountSelected = false
                    binding.tilCustomAmount.visibility = View.GONE
                }
                binding.chipR100.id -> {
                    selectedAmount = 100.0
                    isCustomAmountSelected = false
                    binding.tilCustomAmount.visibility = View.GONE
                }
                binding.chipR250.id -> {
                    selectedAmount = 250.0
                    isCustomAmountSelected = false
                    binding.tilCustomAmount.visibility = View.GONE
                }
                binding.chipCustom.id -> {
                    isCustomAmountSelected = true
                    binding.tilCustomAmount.visibility = View.VISIBLE
                    validateCustomAmount()
                }
            }
        }
    }

    private fun setupCustomAmountValidation() {
        binding.etCustomAmount.doOnTextChanged { text, _, _, _ ->
            binding.tilCustomAmount.error = null
            if (isCustomAmountSelected) {
                val input = text.toString().toDoubleOrNull()
                if (input == null || input <= 0.0) {
                    binding.tilCustomAmount.error = getString(R.string.error_invalid_amount)
                } else {
                    selectedAmount = input
                }
            }
        }
    }

    private fun validateCustomAmount(): Boolean {
        if (!isCustomAmountSelected) return true

        val input = binding.etCustomAmount.text.toString().toDoubleOrNull()
        return if (input == null || input <= 0.0) {
            binding.tilCustomAmount.error = getString(R.string.error_invalid_amount)
            false
        } else {
            binding.tilCustomAmount.error = null
            selectedAmount = input
            true
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitDonation.setOnClickListener {
            val category = binding.actvCategory.text.toString().ifBlank { categories[0] }

            if (isCustomAmountSelected && !validateCustomAmount()) {
                Toast.makeText(requireContext(), getString(R.string.error_invalid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = if (binding.rbCard.isChecked) "Credit Card" else "Direct Bank Transfer"
            val sessionManager = SessionManager(requireContext())
            val userEmail = sessionManager.fetchUserEmail()

            // Disable submit button during active API network call to prevent double-charging
            binding.btnSubmitDonation.isEnabled = false
            binding.pbDonation.visibility = View.VISIBLE

            lifecycleScope.launch {
                val response = repository.submitDonation(
                    DonationRequest(
                        category = category,
                        amount = selectedAmount,
                        paymentMethod = paymentMethod,
                        userEmail = userEmail
                    )
                )

                binding.pbDonation.visibility = View.GONE
                binding.btnSubmitDonation.isEnabled = true

                if (response.success) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Donation Successful")
                        .setMessage("${response.message}\n\nTransaction Reference: ${response.transactionRef}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
