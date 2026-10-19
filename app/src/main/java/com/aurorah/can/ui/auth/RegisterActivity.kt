package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.MainActivity
import com.aurorah.can.R
import com.aurorah.can.util.InputValidator
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.RegisterRequest
import com.aurorah.can.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: ApiRepository

    private val roles = listOf("Volunteer", "Beneficiary", "Donor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        repository = ApiRepository(sessionManager)

        setupRoleDropdown()
        setupInputValidation()
        setupListeners()
    }

    private fun setupRoleDropdown() {
        binding.actvRole.setSimpleItems(roles.toTypedArray())
        binding.actvRole.setText(roles[0], false)
    }

    private fun setupInputValidation() {
        binding.etFullName.doOnTextChanged { _, _, _, _ ->
            binding.tilFullName.error = null
            hideError()
        }

        binding.etRegEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilRegEmail.error = null
            hideError()
        }

        binding.etRegPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilRegPassword.error = null
            hideError()
        }

        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilConfirmPassword.error = null
            hideError()
        }
    }

    private fun setupListeners() {
        binding.btnCloseError.setOnClickListener {
            hideError()
        }

        binding.btnRegister.setOnClickListener {
            hideError()
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val role = binding.actvRole.text.toString().ifBlank { roles[0] }

            var isValid = true

            if (fullName.isEmpty()) {
                binding.tilFullName.error = getString(R.string.error_empty_field)
                isValid = false
            }

            // Validation using InputValidator
            if (!InputValidator.isValidEmail(email)) {
                binding.tilRegEmail.error = getString(R.string.error_invalid_email)
                isValid = false
            }

            // Password complexity check
            if (!InputValidator.isSecurePassword(password)) {
                binding.tilRegPassword.error = getString(R.string.error_invalid_password)
                isValid = false
            }

            if (confirmPassword != password) {
                binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            binding.pbRegister.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false

            lifecycleScope.launch {
                val response = repository.register(
                    RegisterRequest(
                        fullName = fullName,
                        email = email,
                        password = password,
                        role = role
                    )
                )

                binding.pbRegister.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (response.success && response.user != null) {
                    sessionManager.saveSession(
                        token = response.token ?: "authenticated-jwt-token",
                        email = response.user.email,
                        name = response.user.fullName,
                        role = response.user.role
                    )
                    Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    showError(response.message)
                }
            }
        }

        binding.tvLoginPrompt.setOnClickListener {
            finish()
        }
    }

    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.cardErrorMessage.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.cardErrorMessage.visibility = View.GONE
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
