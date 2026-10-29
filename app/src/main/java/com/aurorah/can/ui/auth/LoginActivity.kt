package com.aurorah.can.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aurorah.can.MainActivity
import com.aurorah.can.R
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email ?: ""
                val name = account.displayName ?: email.substringBefore("@")
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    Log.w("GoogleSignIn", "ID Token is null or blank for account: $email")
                } else {
                    Log.d("GoogleSignIn", "Retrieved Google ID Token: ${idToken.take(15)}...")
                }
                viewModel.loginWithGoogle(email = email, name = name, idToken = idToken)
            } else {
                showError("Google Sign-In failed to retrieve account details.")
            }
        } catch (e: ApiException) {
            // Code 12501 indicates the user cancelled the Google Sign-In picker
            if (e.statusCode != 12501) {
                showError("Google Sign-In failed (${e.statusCode}): ${e.localizedMessage}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        setupInputValidation()
        setupListeners()
        observeViewModel()
    }

    private fun setupGoogleSignIn() {
        val webClientId = getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupInputValidation() {
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChanged(text?.toString() ?: "")
            binding.tilEmail.error = null
        }

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text?.toString() ?: "")
            binding.tilPassword.error = null
        }
    }

    private fun setupListeners() {
        binding.btnCloseError.setOnClickListener {
            viewModel.hideError()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login()
        }

        binding.btnGoogleLogin.setOnClickListener {
            launchGoogleSignIn()
        }

        binding.tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun launchGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.pbLogin.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnLogin.isEnabled = !state.isLoading
                    binding.btnGoogleLogin.isEnabled = !state.isLoading

                    binding.tilEmail.error = state.emailError?.let {
                        if (it == "Invalid Email Address") getString(R.string.error_invalid_email) else it
                    }

                    binding.tilPassword.error = state.passwordError?.let {
                        if (it.contains("Password must be at least")) getString(R.string.error_invalid_password) else it
                    }

                    if (!state.errorMessage.isNullOrBlank()) {
                        showError(state.errorMessage)
                    } else {
                        hideError()
                    }

                    if (state.isSuccess) {
                        Toast.makeText(this@LoginActivity, "Welcome back, ${state.userFullName}!", Toast.LENGTH_SHORT).show()
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
