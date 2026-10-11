package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.MainActivity
import com.aurorah.can.R
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.RegisterRequest
import com.aurorah.can.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private val repository = MockApiRepository()

    private val passwordPattern = Regex("^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
    private val roles = listOf("Volunteer", "Beneficiary", "Donor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRoleDropdown()
        setupInputValidation()
        setupListeners()
    }

    private fun setupRoleDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.actvRole.setAdapter(adapter)
        binding.actvRole.setText(roles[0], false)
    }

    private fun setupInputValidation() {
        binding.etFullName.doOnTextChanged { text, _, _, _ ->
            binding.tilFullName.error = null
        }

        binding.etRegEmail.doOnTextChanged { text, _, _, _ ->
            binding.tilRegEmail.error = null
        }

        binding.etRegPassword.doOnTextChanged { text, _, _, _ ->
            binding.tilRegPassword.error = null
        }

        binding.etConfirmPassword.doOnTextChanged { text, _, _, _ ->
            binding.tilConfirmPassword.error = null
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
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

            // RFC 5322 regex check
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilRegEmail.error = getString(R.string.error_invalid_email)
                isValid = false
            }

            // Password check: min 8 chars, numbers and special chars
            if (password.isEmpty() || !password.matches(passwordPattern)) {
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
                        token = response.token ?: "mock-jwt-token",
                        email = response.user.email,
                        name = response.user.fullName,
                        role = response.user.role
                    )
                    Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvLoginPrompt.setOnClickListener {
            finish()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
