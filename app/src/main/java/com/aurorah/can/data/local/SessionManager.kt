package com.aurorah.can.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages local session persistence and user preference storage.
 *
 * Employs [EncryptedSharedPreferences] with AES256 encryption to securely store sensitive
 * auth tokens, user roles, and profile information. Maintains a separate unencrypted
 * [SharedPreferences] store for non-sensitive app settings (dark mode, notifications, language)
 * to ensure rapid UI synchronization on app startup.
 *
 * @param context Application context injected via Hilt.
 */
@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val appContext = context.applicationContext

    // Separate unencrypted preferences for UI settings to guarantee immediate persistence across restarts
    private val settingsPrefs: SharedPreferences =
        appContext.getSharedPreferences(SETTINGS_PREF_NAME, Context.MODE_PRIVATE)

    // Secure encrypted preferences for session tokens & profile info
    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            appContext,
            SESSION_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: Exception) {
        appContext.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val SETTINGS_PREF_NAME = "AurorahCAN_AppSettings"
        private const val SESSION_PREF_NAME = "AurorahCAN_EncryptedSession"
        private const val FALLBACK_PREF_NAME = "AurorahCAN_FallbackSession"

        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_ROLE = "user_role"

        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_SEARCH_RADIUS = "search_radius"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_DATA_SAVER = "data_saver"
    }

    /** Saves authenticated user session tokens and profile parameters to encrypted storage. */
    fun saveSession(token: String, email: String, name: String, role: String, phone: String? = null) {
        try {
            prefs.edit().apply {
                putString(KEY_TOKEN, token)
                putString(KEY_EMAIL, email)
                putString(KEY_NAME, name)
                putString(KEY_ROLE, role)
                if (phone != null) {
                    putString(KEY_PHONE, phone)
                }
                commit()
            }
        } catch (_: Exception) { }
    }

    /** Updates local profile details for the currently logged in user. */
    fun updateUserProfile(name: String, phone: String) {
        try {
            prefs.edit().apply {
                putString(KEY_NAME, name)
                putString(KEY_PHONE, phone)
                commit()
            }
        } catch (_: Exception) { }
    }

    /** Fetches stored JWT authentication token, or `null` if unauthenticated. */
    fun fetchAuthToken(): String? {
        return try {
            prefs.getString(KEY_TOKEN, null)
        } catch (_: Exception) {
            null
        }
    }

    /** Fetches stored user email address, defaulting to a guest fallback. */
    fun fetchUserEmail(): String {
        return try {
            prefs.getString(KEY_EMAIL, "guest@aurorahcan.org") ?: "guest@aurorahcan.org"
        } catch (_: Exception) {
            "guest@aurorahcan.org"
        }
    }

    /** Fetches stored user full name, defaulting to "Guest User". */
    fun fetchUserName(): String {
        return try {
            prefs.getString(KEY_NAME, "Guest User") ?: "Guest User"
        } catch (_: Exception) {
            "Guest User"
        }
    }

    /** Fetches stored user phone number. */
    fun fetchUserPhone(): String {
        return try {
            prefs.getString(KEY_PHONE, "+27 82 000 0000") ?: "+27 82 000 0000"
        } catch (_: Exception) {
            "+27 82 000 0000"
        }
    }

    /** Fetches stored user security role (e.g., Volunteer, Admin). */
    fun fetchUserRole(): String {
        return try {
            prefs.getString(KEY_ROLE, "Volunteer") ?: "Volunteer"
        } catch (_: Exception) {
            "Volunteer"
        }
    }

    /** Returns `true` if a valid non-empty auth token is present in encrypted storage. */
    fun isLoggedIn(): Boolean = !fetchAuthToken().isNullOrBlank()

    /** Checks whether night/dark theme preference is enabled. */
    fun isNightModeEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_NIGHT_MODE, false)
        } catch (_: Exception) {
            false
        }
    }

    /** Updates night/dark theme preference. */
    fun setNightModeEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_NIGHT_MODE, enabled).commit()
        } catch (_: Exception) { }
    }

    /** Checks whether push notification preference is enabled. */
    fun areNotificationsEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true)
        } catch (_: Exception) {
            true
        }
    }

    /** Updates push notification preference. */
    fun setNotificationsEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).commit()
        } catch (_: Exception) { }
    }

    /** Fetches preferred search filter radius string. */
    fun getSearchRadius(): String {
        return try {
            settingsPrefs.getString(KEY_SEARCH_RADIUS, "10 km") ?: "10 km"
        } catch (_: Exception) {
            "10 km"
        }
    }

    /** Updates preferred search filter radius. */
    fun setSearchRadius(radius: String) {
        try {
            settingsPrefs.edit().putString(KEY_SEARCH_RADIUS, radius).commit()
        } catch (_: Exception) { }
    }

    /** Fetches preferred app UI display language. */
    fun getAppLanguage(): String {
        return try {
            settingsPrefs.getString(KEY_APP_LANGUAGE, "English") ?: "English"
        } catch (_: Exception) {
            "English"
        }
    }

    /** Updates preferred app UI display language. */
    fun setAppLanguage(language: String) {
        try {
            settingsPrefs.edit().putString(KEY_APP_LANGUAGE, language).commit()
        } catch (_: Exception) { }
    }

    /** Checks whether data saver mode is enabled. */
    fun isDataSaverEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_DATA_SAVER, false)
        } catch (_: Exception) {
            false
        }
    }

    /** Updates data saver mode preference. */
    fun setDataSaverEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_DATA_SAVER, enabled).commit()
        } catch (_: Exception) { }
    }

    /** Clears active user tokens and profile data from encrypted preferences upon logout. */
    fun clearSession() {
        try {
            prefs.edit().apply {
                remove(KEY_TOKEN)
                remove(KEY_EMAIL)
                remove(KEY_NAME)
                remove(KEY_ROLE)
                commit()
            }
        } catch (_: Exception) { }
    }
}
