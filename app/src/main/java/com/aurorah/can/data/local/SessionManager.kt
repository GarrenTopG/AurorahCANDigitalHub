package com.aurorah.can.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
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

    fun updateUserProfile(name: String, phone: String) {
        try {
            prefs.edit().apply {
                putString(KEY_NAME, name)
                putString(KEY_PHONE, phone)
                commit()
            }
        } catch (_: Exception) { }
    }

    fun fetchAuthToken(): String? {
        return try {
            prefs.getString(KEY_TOKEN, null)
        } catch (_: Exception) {
            null
        }
    }

    fun fetchUserEmail(): String {
        return try {
            prefs.getString(KEY_EMAIL, "guest@aurorahcan.org") ?: "guest@aurorahcan.org"
        } catch (_: Exception) {
            "guest@aurorahcan.org"
        }
    }

    fun fetchUserName(): String {
        return try {
            prefs.getString(KEY_NAME, "Guest User") ?: "Guest User"
        } catch (_: Exception) {
            "Guest User"
        }
    }

    fun fetchUserPhone(): String {
        return try {
            prefs.getString(KEY_PHONE, "+27 82 000 0000") ?: "+27 82 000 0000"
        } catch (_: Exception) {
            "+27 82 000 0000"
        }
    }

    fun fetchUserRole(): String {
        return try {
            prefs.getString(KEY_ROLE, "Volunteer") ?: "Volunteer"
        } catch (_: Exception) {
            "Volunteer"
        }
    }

    fun isLoggedIn(): Boolean = !fetchAuthToken().isNullOrBlank()

    fun isNightModeEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_NIGHT_MODE, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setNightModeEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_NIGHT_MODE, enabled).commit()
        } catch (_: Exception) { }
    }

    fun areNotificationsEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true)
        } catch (_: Exception) {
            true
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).commit()
        } catch (_: Exception) { }
    }

    fun getSearchRadius(): String {
        return try {
            settingsPrefs.getString(KEY_SEARCH_RADIUS, "10 km") ?: "10 km"
        } catch (_: Exception) {
            "10 km"
        }
    }

    fun setSearchRadius(radius: String) {
        try {
            settingsPrefs.edit().putString(KEY_SEARCH_RADIUS, radius).commit()
        } catch (_: Exception) { }
    }

    fun getAppLanguage(): String {
        return try {
            settingsPrefs.getString(KEY_APP_LANGUAGE, "English") ?: "English"
        } catch (_: Exception) {
            "English"
        }
    }

    fun setAppLanguage(language: String) {
        try {
            settingsPrefs.edit().putString(KEY_APP_LANGUAGE, language).commit()
        } catch (_: Exception) { }
    }

    fun isDataSaverEnabled(): Boolean {
        return try {
            settingsPrefs.getBoolean(KEY_DATA_SAVER, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        try {
            settingsPrefs.edit().putBoolean(KEY_DATA_SAVER, enabled).commit()
        } catch (_: Exception) { }
    }

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
