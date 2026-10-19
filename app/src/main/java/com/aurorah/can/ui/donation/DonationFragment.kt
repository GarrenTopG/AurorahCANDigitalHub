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
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.DonationRequest
import com.aurorah.can.databinding.FragmentDonationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class DonationFragment : Fragment() {

    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { ApiRepository(requireContext()) }

    private val monetaryCategories = listOf("Food Parcels", "Winter Relief", "General Fund")
    private val inKindCategories = listOf("Food Parcels", "Clothing", "Household Goods", "School Supplies")
    private val preferences = listOf("Drop-off at Hub", "Request Collection")

    private var selectedAmount: Double = 100.0
    private var isCustomAmountSelected: Boolean = false
    private var isMonetaryTab: Boolean = true

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

        setupTabs()
        setupSpinners()
        setupAmountChips()
        setupCustomAmountValidation()
        setupSubmitButton()
    }

    private fun setupTabs() {
        binding.tlDonationType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    isMonetaryTab = true
                    binding.layoutMonetary.visibility = View.VISIBLE
                    binding.layoutInKind.visibility = View.GONE
                } else {
                    isMonetaryTab = false
                    binding.layoutMonetary.visibility = View.GONE
                    binding.layoutInKind.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSpinners() {
        // Monetary category spinner
        val monetaryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, monetaryCategories)
        binding.actvCategory.setAdapter(monetaryAdapter)
        binding.actvCategory.setText(monetaryCategories[0], false)

        // In-kind item category spinner
        val inKindAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, inKindCategories)
        binding.actvInKindCategory.setAdapter(inKindAdapter)
        binding.actvInKindCategory.setText(inKindCategories[0], false)

        // Preference spinner
        val preferenceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, preferences)
        binding.actvInKindPreference.setAdapter(preferenceAdapter)
        binding.actvInKindPreference.setText(preferences[0], false)
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
            val input = text?.toString()?.toDoubleOrNull()
            if (input != null && input > 0.0) {
                selectedAmount = input
            }
        }
        binding.etItemDescription.doOnTextChanged { _, _, _, _ ->
            binding.tilItemDescription.error = null
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
            val sessionManager = SessionManager(requireContext())
            val userEmail = sessionManager.fetchUserEmail()

            val request: DonationRequest

            if (isMonetaryTab) {
                val category = binding.actvCategory.text.toString().ifBlank { monetaryCategories[0] }

                if (isCustomAmountSelected && !validateCustomAmount()) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_amount), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val paymentMethod = if (binding.rbCard.isChecked) "Credit Card" else "Direct Bank Transfer"

                request = DonationRequest(
                    donationType = "Monetary",
                    category = category,
                    amount = selectedAmount,
                    paymentMethod = paymentMethod,
                    userEmail = userEmail
                )
            } else {
                val category = binding.actvInKindCategory.text.toString().ifBlank { inKindCategories[0] }
                val itemDesc = binding.etItemDescription.text.toString().trim()
                val pref = binding.actvInKindPreference.text.toString().ifBlank { preferences[0] }

                if (itemDesc.isEmpty()) {
                    binding.tilItemDescription.error = getString(R.string.error_empty_field)
                    return@setOnClickListener
                }

                request = DonationRequest(
                    donationType = "In-Kind",
                    category = category,
                    itemDescription = itemDesc,
                    preference = pref,
                    userEmail = userEmail
                )
            }

            binding.btnSubmitDonation.isEnabled = false
            binding.pbDonation.visibility = View.VISIBLE

            viewLifecycleOwner.lifecycleScope.launch {
                val response = repository.submitDonation(request)

                _binding?.let { b ->
                    b.pbDonation.visibility = View.GONE
                    b.btnSubmitDonation.isEnabled = true
                }

                if (isAdded) {
                    if (response.success) {
                        val refMessage = if (response.transactionRef.isNotBlank()) {
                            "\n\nTransaction Reference: ${response.transactionRef}"
                        } else ""
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(if (isMonetaryTab) "Donation Successful" else "Pledge Received")
                            .setMessage("${response.message}$refMessage")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
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
