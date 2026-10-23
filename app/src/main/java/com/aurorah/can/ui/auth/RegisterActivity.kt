package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aurorah.can.MainActivity
import com.aurorah.can.R
import com.aurorah.can.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val roles = listOf("Volunteer", "Beneficiary", "Donor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleDropdown()
        setupInputValidation()
        setupListeners()
        observeViewModel()

        viewModel.preWarmServer()
    }

    private fun setupRoleDropdown() {
        binding.actvRole.setSimpleItems(roles.toTypedArray())
        binding.actvRole.setText(roles[0], false)
        binding.actvRole.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            viewModel.onRoleChanged(selected)
        }
    }

    private fun setupInputValidation() {
        binding.etFullName.doOnTextChanged { text, _, _, _ ->
            viewModel.onFullNameChanged(text?.toString() ?: "")
            binding.tilFullName.error = null
        }

        binding.etRegEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChanged(text?.toString() ?: "")
            binding.tilRegEmail.error = null
        }

        binding.etRegPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text?.toString() ?: "")
            binding.tilRegPassword.error = null
        }

        binding.etConfirmPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onConfirmPasswordChanged(text?.toString() ?: "")
            binding.tilConfirmPassword.error = null
        }
    }

    private fun setupListeners() {
        binding.btnCloseError.setOnClickListener {
            viewModel.hideError()
        }

        binding.btnRegister.setOnClickListener {
            viewModel.register()
        }

        binding.tvLoginPrompt.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.pbRegister.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnRegister.isEnabled = !state.isLoading

                    binding.tilFullName.error = state.fullNameError?.let {
                        if (it == "Field cannot be empty") getString(R.string.error_empty_field) else it
                    }

                    binding.tilRegEmail.error = state.emailError?.let {
                        if (it == "Invalid Email Address") getString(R.string.error_invalid_email) else it
                    }

                    binding.tilRegPassword.error = state.passwordError?.let {
                        if (it.contains("Password must be at least")) getString(R.string.error_invalid_password) else it
                    }

                    binding.tilConfirmPassword.error = state.confirmPasswordError?.let {
                        if (it == "Passwords do not match") getString(R.string.error_password_mismatch) else it
                    }

                    if (!state.errorMessage.isNullOrBlank()) {
                        showError(state.errorMessage)
                    } else {
                        hideError()
                    }

                    if (state.isSuccess) {
                        Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                }
            }
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
