package com.aurorah.can.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.R
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.ChangePasswordRequest
import com.aurorah.can.databinding.DialogChangePasswordBinding
import com.aurorah.can.databinding.FragmentSettingsBinding
import com.aurorah.can.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private val repository = MockApiRepository()

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
        setupToggles()
        setupChangePassword()
        setupSignOut()
    }

    private fun setupProfileHeader() {
        binding.tvUserName.text = sessionManager.fetchUserName()
        binding.tvUserEmail.text = sessionManager.fetchUserEmail()
        binding.tvUserRole.text = getString(R.string.user_role_format, sessionManager.fetchUserRole().uppercase())
    }

    private fun setupToggles() {
        // Night mode switch
        val isDark = sessionManager.isNightModeEnabled()
        binding.switchNightMode.isChecked = isDark

        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setNightModeEnabled(isChecked)
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Notification preferences switch
        binding.switchNotifications.isChecked = sessionManager.areNotificationsEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setNotificationsEnabled(isChecked)
            val stateText = if (isChecked) "enabled" else "disabled"
            Toast.makeText(requireContext(), "Notifications $stateText", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChangePassword() {
        binding.btnChangePassword.setOnClickListener {
            val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.settings_change_password))
                .setView(dialogBinding.root)
                .setPositiveButton("Update") { dialog, _ ->
                    val oldPass = dialogBinding.etOldPassword.text.toString().trim()
                    val newPass = dialogBinding.etNewPassword.text.toString().trim()

                    if (oldPass.isEmpty() || newPass.isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.error_empty_field), Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Password complexity validation: min 8 chars, numbers, special chars
                    val passRegex = Regex("^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
                    if (!newPass.matches(passRegex)) {
                        Toast.makeText(requireContext(), getString(R.string.error_invalid_password), Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }

                    lifecycleScope.launch {
                        val response = repository.changePassword(
                            ChangePasswordRequest(
                                email = sessionManager.fetchUserEmail(),
                                oldPassword = oldPass,
                                newPassword = newPass
                            )
                        )
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
