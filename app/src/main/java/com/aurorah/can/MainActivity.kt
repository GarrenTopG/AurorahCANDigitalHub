package com.aurorah.can

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.databinding.ActivityMainBinding
import com.aurorah.can.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(this)

        // Check night mode preference before super.onCreate
        val isDark = sessionManager.isNightModeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Apply preferred app language if configured
        val savedLanguage = sessionManager.getAppLanguage()
        val langCode = when (savedLanguage) {
            "isiZulu" -> "zu"
            "Afrikaans" -> "af"
            "isiXhosa" -> "xh"
            else -> "en"
        }
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.isEmpty || currentLocales.toLanguageTags() != langCode) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(langCode)
            )
        }

        super.onCreate(savedInstanceState)

        // Check authentication session
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.nav_active_indicator)
        binding.bottomNavigation.setupWithNavController(navController)

        // Custom transition animations and clean backstack handling for bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == navController.currentDestination?.id) {
                return@setOnItemSelectedListener true
            }

            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)

            if (item.itemId == R.id.navigation_home) {
                // Return cleanly to Home root
                builder.setPopUpTo(R.id.navigation_home, false)
            } else {
                builder.setPopUpTo(navController.graph.startDestinationId, false, true)
            }

            try {
                navController.navigate(item.itemId, null, builder.build())
                true
            } catch (_: Exception) {
                false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, false)
            } else {
                navController.popBackStack(item.itemId, false)
            }
        }
    }
}
