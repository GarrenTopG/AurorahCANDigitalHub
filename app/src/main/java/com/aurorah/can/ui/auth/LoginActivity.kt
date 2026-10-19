package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
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
import com.aurorah.can.data.model.LoginRequest
import com.aurorah.can.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: ApiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        repository = ApiRepository(sessionManager)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputValidation()
        setupListeners()
    }

    private fun setupInputValidation() {
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
            hideError()
        }

        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
            hideError()
        }
    }

    private fun setupListeners() {
        binding.btnCloseError.setOnClickListener {
            hideError()
        }

        binding.btnLogin.setOnClickListener {
            hideError()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            // Input validation using InputValidator
            if (!InputValidator.isValidEmail(email)) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                isValid = false
            }

            // Password complexity check
            if (!InputValidator.isSecurePassword(password)) {
                binding.tilPassword.error = getString(R.string.error_invalid_password)
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            binding.pbLogin.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            lifecycleScope.launch {
                val response = repository.login(LoginRequest(email, password))
                binding.pbLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.success && response.user != null) {
                    sessionManager.saveSession(
                        token = response.token ?: "authenticated-jwt-token",
                        email = response.user.email,
                        name = response.user.fullName,
                        role = response.user.role
                    )
                    Toast.makeText(this@LoginActivity, "Welcome back, ${response.user.fullName}!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    showError(response.message)
                }
            }
        }

        binding.tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
