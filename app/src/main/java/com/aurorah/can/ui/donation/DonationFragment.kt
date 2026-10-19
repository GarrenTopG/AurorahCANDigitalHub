package com.aurorah.can.ui.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val monetaryCauses = listOf(
        "General fund — where most needed",
        "Food relief",
        "Youth and learning",
        "Community library",
        "Community development"
    )

    private val inKindCategories = listOf(
        "Non-Perishable Food / Canned Goods",
        "Clothing & Footwear",
        "Blankets & Bedding",
        "School Supplies & Books",
        "Other Physical Goods"
    )

    private val countryCodes = listOf("🇿🇦 +27", "🇳🇦 +264", "🇧🇼 +267")

    private var selectedAmount: Double = 200.0
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
        setupTaxCertificateToggle()
        setupFulfillmentToggle()
        setupAnonymousToggle()
        setupValidation()
        setupSubmitButton()
        prefillUserInfo()
    }

    private fun prefillUserInfo() {
        val sessionManager = SessionManager(requireContext())
        val savedEmail = sessionManager.fetchUserEmail()
        val savedName = sessionManager.fetchUserName()

        if (savedEmail.isNotBlank()) {
            binding.etDonorEmail.setText(savedEmail)
        }
        if (savedName.isNotBlank() && savedName != "User") {
            binding.etDonorName.setText(savedName)
        }
    }

    private fun setupTabs() {
        binding.tlDonationType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    isMonetaryTab = true
                    binding.layoutMonetary.visibility = View.VISIBLE
                    binding.layoutInKind.visibility = View.GONE
                    binding.btnSubmitDonation.text = "Submit Donation Pledge"
                } else {
                    isMonetaryTab = false
                    binding.layoutMonetary.visibility = View.GONE
                    binding.layoutInKind.visibility = View.VISIBLE
                    binding.btnSubmitDonation.text = "Submit In-Kind Offer"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSpinners() {
        // Monetary causes dropdown
        binding.actvCategory.setSimpleItems(monetaryCauses.toTypedArray())
        binding.actvCategory.setText(monetaryCauses[0], false)

        // In-kind item categories dropdown
        binding.actvInKindCategory.setSimpleItems(inKindCategories.toTypedArray())
        binding.actvInKindCategory.setText(inKindCategories[0], false)

        // Country code dropdown
        binding.actvCountryCode.setSimpleItems(countryCodes.toTypedArray())
        binding.actvCountryCode.setText(countryCodes[0], false)
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
                binding.chipR200.id -> {
                    selectedAmount = 200.0
                    isCustomAmountSelected = false
                    binding.tilCustomAmount.visibility = View.GONE
                }
                binding.chipR500.id -> {
                    selectedAmount = 500.0
                    isCustomAmountSelected = false
                    binding.tilCustomAmount.visibility = View.GONE
                }
                binding.chipR1000.id -> {
                    selectedAmount = 1000.0
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

    private fun setupTaxCertificateToggle() {
        binding.cbRequestTaxCertificate.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutTaxReceiptFields.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                // Tax certificates require non-anonymous details
                if (binding.cbAnonymous.isChecked) {
                    binding.cbAnonymous.isChecked = false
                }
            }
        }
    }

    private fun setupFulfillmentToggle() {
        binding.rgFulfillmentMethod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbDropoff.id) {
                binding.cardDropoffInfo.visibility = View.VISIBLE
                binding.tilPickupAddress.visibility = View.GONE
            } else {
                binding.cardDropoffInfo.visibility = View.GONE
                binding.tilPickupAddress.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAnonymousToggle() {
        binding.cbAnonymous.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etDonorName.setText("Anonymous Donor")
                binding.etDonorName.isEnabled = false
                if (binding.cbRequestTaxCertificate.isChecked) {
                    binding.cbRequestTaxCertificate.isChecked = false
                }
            } else {
                if (binding.etDonorName.text?.toString() == "Anonymous Donor") {
                    binding.etDonorName.setText("")
                }
                binding.etDonorName.isEnabled = true
            }
        }
    }

    private fun setupValidation() {
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
        binding.etTaxIdNumber.doOnTextChanged { _, _, _, _ ->
            binding.tilTaxIdNumber.error = null
        }
        binding.etTaxAddress.doOnTextChanged { _, _, _, _ ->
            binding.tilTaxAddress.error = null
        }
        binding.etPickupAddress.doOnTextChanged { _, _, _, _ ->
            binding.tilPickupAddress.error = null
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
            if (!binding.cbPopiaConsent.isChecked) {
                Toast.makeText(requireContext(), "Please accept the privacy information to proceed.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isTaxCertRequested = binding.cbRequestTaxCertificate.isChecked
            val isAnonymous = binding.cbAnonymous.isChecked

            if (isTaxCertRequested) {
                val taxId = binding.etTaxIdNumber.text.toString().trim()
                val taxAddress = binding.etTaxAddress.text.toString().trim()
                val donorName = binding.etDonorName.text.toString().trim()
                val donorEmail = binding.etDonorEmail.text.toString().trim()

                if (taxId.isEmpty()) {
                    binding.tilTaxIdNumber.error = "Tax reference / ID required for Section 18A certificate"
                    return@setOnClickListener
                }
                if (taxAddress.isEmpty()) {
                    binding.tilTaxAddress.error = "Tax address required for Section 18A certificate"
                    return@setOnClickListener
                }
                if (donorName.isEmpty() || isAnonymous) {
                    binding.tilDonorName.error = "Your name is required for Section 18A certificate"
                    return@setOnClickListener
                }
                if (donorEmail.isEmpty()) {
                    binding.tilDonorEmail.error = "Email address is required for Section 18A certificate"
                    return@setOnClickListener
                }
            }

            val request: DonationRequest
            val donorName = binding.etDonorName.text.toString().trim().ifBlank { if (isAnonymous) "Anonymous Donor" else "Friend of Aurorah" }
            val donorEmail = binding.etDonorEmail.text.toString().trim()
            val phoneNum = binding.etPhone.text.toString().trim()
            val countryCode = binding.actvCountryCode.text.toString().trim().takeLast(3).ifBlank { "+27" }
            val message = binding.etMessage.text.toString().trim()

            if (isMonetaryTab) {
                val cause = binding.actvCategory.text.toString().ifBlank { monetaryCauses[0] }

                if (isCustomAmountSelected && !validateCustomAmount()) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_amount), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                request = DonationRequest(
                    donationType = "Monetary",
                    category = cause,
                    amount = selectedAmount,
                    donorName = donorName,
                    userEmail = donorEmail,
                    phone = phoneNum,
                    countryCode = countryCode,
                    message = message,
                    isAnonymous = isAnonymous,
                    requestTaxCertificate = isTaxCertRequested,
                    taxIdNumber = if (isTaxCertRequested) binding.etTaxIdNumber.text.toString().trim() else null,
                    taxAddress = if (isTaxCertRequested) binding.etTaxAddress.text.toString().trim() else null,
                    popiaConsent = binding.cbPopiaConsent.isChecked
                )
            } else {
                val category = binding.actvInKindCategory.text.toString().ifBlank { inKindCategories[0] }
                val itemDesc = binding.etItemDescription.text.toString().trim()
                val isPickup = binding.rbPickup.isChecked
                val preference = if (isPickup) "Request Collection" else "Drop-off at Hub"
                val pickupAddress = if (isPickup) binding.etPickupAddress.text.toString().trim() else null

                if (itemDesc.isEmpty()) {
                    binding.tilItemDescription.error = getString(R.string.error_empty_field)
                    return@setOnClickListener
                }

                if (isPickup && pickupAddress.isNullOrBlank()) {
                    binding.tilPickupAddress.error = "Collection address required"
                    return@setOnClickListener
                }

                request = DonationRequest(
                    donationType = "In-Kind",
                    category = category,
                    itemDescription = itemDesc,
                    preference = preference,
                    pickupAddress = pickupAddress,
                    donorName = donorName,
                    userEmail = donorEmail,
                    phone = phoneNum,
                    countryCode = countryCode,
                    message = message,
                    isAnonymous = isAnonymous,
                    popiaConsent = binding.cbPopiaConsent.isChecked
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
                            "\n\nPledge Reference: ${response.transactionRef}"
                        } else ""
                        val taxCertNote = if (isTaxCertRequested) {
                            "\n\nSection 18A Tax Certificate Request logged. A certificate will be issued to $donorEmail upon review."
                        } else ""

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Pledge Recorded")
                            .setMessage("Thank you! Your pledge has been recorded.\n\nNote: Payments are not processed online. Aurorah CAN team will follow up on your offer.$taxCertNote$refMessage")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                resetForm()
                            }
                            .show()
                    } else {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun resetForm() {
        binding.etItemDescription.setText("")
        binding.etTaxIdNumber.setText("")
        binding.etTaxAddress.setText("")
        binding.etPickupAddress.setText("")
        binding.etMessage.setText("")
        binding.cbRequestTaxCertificate.isChecked = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
