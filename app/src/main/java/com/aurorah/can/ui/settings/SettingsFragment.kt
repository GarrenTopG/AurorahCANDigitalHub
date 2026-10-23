package com.aurorah.can.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.R
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.ChangePasswordRequest
import com.aurorah.can.data.model.UpdateProfileRequest
import com.aurorah.can.databinding.DialogChangePasswordBinding
import com.aurorah.can.databinding.DialogEditProfileBinding
import com.aurorah.can.databinding.FragmentSettingsBinding
import com.aurorah.can.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private val repository by lazy { ApiRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupProfileHeader()
        setupEditProfile()
        setupToggles()
        setupChangePassword()
        setupPrivacyPolicy()
        setupSignOut()
        setupDeleteAccount()
    }

    private fun setupProfileHeader() {
        binding.tvUserName.text = sessionManager.fetchUserName()
        binding.tvUserEmail.text = sessionManager.fetchUserEmail()
        binding.tvUserPhone.text = sessionManager.fetchUserPhone()
        binding.tvUserRole.text = getString(R.string.user_role_format, sessionManager.fetchUserRole().uppercase())
    }

    private fun setupEditProfile() {
        binding.btnEditProfile.setOnClickListener {
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            dialogBinding.etEditFullName.setText(sessionManager.fetchUserName())
            dialogBinding.etEditPhone.setText(sessionManager.fetchUserPhone())

            dialogBinding.etEditFullName.doOnTextChanged { _, _, _, _ ->
                dialogBinding.tilEditFullName.error = null
            }
            dialogBinding.etEditPhone.doOnTextChanged { _, _, _, _ ->
                dialogBinding.tilEditPhone.error = null
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.edit_profile))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.save_changes)) { dialog, _ ->
                    val newName = dialogBinding.etEditFullName.text.toString().trim()
                    val newPhone = dialogBinding.etEditPhone.text.toString().trim()

                    var isValid = true
                    if (newName.isEmpty()) {
                        dialogBinding.tilEditFullName.error = getString(R.string.error_empty_field)
                        isValid = false
                    }
                    if (newPhone.isEmpty()) {
                        dialogBinding.tilEditPhone.error = getString(R.string.error_empty_field)
                        isValid = false
                    }

                    if (!isValid) return@setPositiveButton

                    val userEmail = sessionManager.fetchUserEmail()

                    viewLifecycleOwner.lifecycleScope.launch {
                        val response = repository.updateProfile(
                            UpdateProfileRequest(
                                fullName = newName,
                                phone = newPhone,
                                email = userEmail
                            )
                        )

                        if (isAdded) {
                            if (response.success) {
                                sessionManager.updateUserProfile(newName, newPhone)
                                setupProfileHeader()
                                Toast.makeText(requireContext(), response.message.ifBlank { "Profile updated successfully" }, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun setupToggles() {
        val isDark = sessionManager.isNightModeEnabled()
        binding.switchNightMode.isChecked = isDark

        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setNightModeEnabled(isChecked)
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        binding.switchNotifications.isChecked = sessionManager.areNotificationsEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setNotificationsEnabled(isChecked)
            val stateText = if (isChecked) "enabled" else "disabled"
            Toast.makeText(requireContext(), "Notifications $stateText", Toast.LENGTH_SHORT).show()
        }

        binding.switchDataSaver.isChecked = sessionManager.isDataSaverEnabled()
        binding.switchDataSaver.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setDataSaverEnabled(isChecked)
            val stateText = if (isChecked) "enabled" else "disabled"
            Toast.makeText(requireContext(), "Data Saver $stateText", Toast.LENGTH_SHORT).show()
        }

        // Assistance Radius Dropdown Setup
        val radiusOptions = arrayOf("5 km", "10 km", "25 km", "50 km")
        binding.actvRadius.setAdapter(
            NoFilterArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, radiusOptions)
        )
        binding.actvRadius.setText(sessionManager.getSearchRadius(), false)
        binding.actvRadius.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            sessionManager.setSearchRadius(selected)
            Toast.makeText(requireContext(), "Search radius set to $selected", Toast.LENGTH_SHORT).show()
        }

        // Language Selector Dropdown Setup
        val languageOptions = arrayOf("English", "isiZulu", "Afrikaans", "isiXhosa")
        binding.actvLanguage.setAdapter(
            NoFilterArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, languageOptions)
        )
        binding.actvLanguage.setText(sessionManager.getAppLanguage(), false)
        binding.actvLanguage.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            sessionManager.setAppLanguage(selected)
            val langCode = when (selected) {
                "isiZulu" -> "zu"
                "Afrikaans" -> "af"
                "isiXhosa" -> "xh"
                else -> "en"
            }
            val appLocales = LocaleListCompat.forLanguageTags(langCode)
            if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != langCode) {
                AppCompatDelegate.setApplicationLocales(appLocales)
            }
            Toast.makeText(requireContext(), "Language set to $selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChangePassword() {
        binding.btnChangePassword.setOnClickListener {
            val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)

            dialogBinding.etOldPassword.doOnTextChanged { _, _, _, _ ->
                dialogBinding.tilOldPassword.error = null
            }

            dialogBinding.etNewPassword.doOnTextChanged { _, _, _, _ ->
                dialogBinding.tilNewPassword.error = null
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.settings_change_password))
                .setView(dialogBinding.root)
                .setPositiveButton("Update") { dialog, _ ->
                    val oldPass = dialogBinding.etOldPassword.text.toString().trim()
                    val newPass = dialogBinding.etNewPassword.text.toString().trim()

                    var isValid = true
                    if (oldPass.isEmpty()) {
                        dialogBinding.tilOldPassword.error = getString(R.string.error_empty_field)
                        isValid = false
                    }

                    val passRegex = Regex("^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
                    if (newPass.isEmpty()) {
                        dialogBinding.tilNewPassword.error = getString(R.string.error_empty_field)
                        isValid = false
                    } else if (!newPass.matches(passRegex)) {
                        dialogBinding.tilNewPassword.error = getString(R.string.error_invalid_password)
                        isValid = false
                    }

                    if (!isValid) return@setPositiveButton

                    viewLifecycleOwner.lifecycleScope.launch {
                        val response = repository.changePassword(
                            ChangePasswordRequest(
                                email = sessionManager.fetchUserEmail(),
                                oldPassword = oldPass,
                                newPassword = newPass
                            )
                        )
                        if (isAdded) {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun setupSignOut() {
        binding.btnSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ ->
                    sessionManager.clearSession()
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupPrivacyPolicy() {
        binding.btnPrivacyPolicy.setOnClickListener {
            val url = getString(R.string.privacy_policy_url)
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDeleteAccount() {
        binding.btnDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_account_confirm_title))
                .setMessage(getString(R.string.delete_account_confirm_message))
                .setPositiveButton(getString(R.string.delete_account)) { dialog, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        val response = repository.deleteAccount()
                        if (isAdded) {
                            if (response.success) {
                                sessionManager.clearSession()
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.delete_account_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    response.message.ifBlank { "Failed to delete account" },
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class NoFilterArrayAdapter<T>(
    context: Context,
    resource: Int,
    private val items: Array<T>
) : ArrayAdapter<T>(context, resource, items) {

    private val noFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = items
                count = items.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = noFilter
}
