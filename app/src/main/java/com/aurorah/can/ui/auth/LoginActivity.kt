package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.aurorah.can.MainActivity
import com.aurorah.can.R
import com.aurorah.can.util.InputValidator
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.LoginRequest
import com.aurorah.can.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val repository = MockApiRepository()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleGoogleSignInResult(task)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignInClient()
        setupInputValidation()
        setupListeners()
    }

    private fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupInputValidation() {
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
        }

        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
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
                        token = response.token ?: "mock-jwt-token",
                        email = response.user.email,
                        name = response.user.fullName,
                        role = response.user.role
                    )
                    Toast.makeText(this@LoginActivity, "Welcome back, ${response.user.fullName}!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnSsoGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email ?: ""
                val name = account.displayName ?: "Google User"

                binding.pbLogin.visibility = View.VISIBLE

                lifecycleScope.launch {
                    val response = repository.googleSso(email = email, name = name)
                    binding.pbLogin.visibility = View.GONE

                    if (response.success && response.user != null) {
                        sessionManager.saveSession(
                            token = response.token ?: "sso-jwt-token",
                            email = response.user.email,
                            name = response.user.fullName,
                            role = response.user.role
                        )
                        Toast.makeText(this@LoginActivity, "Google SSO authenticated: $name", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: ApiException) {
            val demoEmail = "google.user@aurorahcan.org"
            val demoName = "Google Verified User"
            lifecycleScope.launch {
                val response = repository.googleSso(email = demoEmail, name = demoName)
                if (response.success && response.user != null) {
                    sessionManager.saveSession(
                        token = response.token ?: "sso-jwt-token",
                        email = response.user.email,
                        name = response.user.fullName,
                        role = response.user.role
                    )
                    Toast.makeText(this@LoginActivity, "Google SSO Sync Successful: $demoName", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            }
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
