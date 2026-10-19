package com.aurorah.can.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val appContext = context.applicationContext

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            appContext,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: Exception) {
        try {
            val fallbackPrefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            fallbackPrefs.getString(KEY_TOKEN, null)
            fallbackPrefs
        } catch (_: Exception) {
            appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).also {
                it.edit().clear().apply()
            }
        }
    }

    companion object {
        private const val PREF_NAME = "AurorahCAN_Session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_ROLE = "user_role"
        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
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
                apply()
            }
        } catch (_: Exception) { }
    }

    fun updateUserProfile(name: String, phone: String) {
        try {
            prefs.edit().apply {
                putString(KEY_NAME, name)
                putString(KEY_PHONE, phone)
                apply()
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
            prefs.getBoolean(KEY_NIGHT_MODE, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setNightModeEnabled(enabled: Boolean) {
        try {
            prefs.edit().putBoolean(KEY_NIGHT_MODE, enabled).apply()
        } catch (_: Exception) { }
    }

    fun areNotificationsEnabled(): Boolean {
        return try {
            prefs.getBoolean(KEY_NOTIFICATIONS, true)
        } catch (_: Exception) {
            true
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        try {
            prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        } catch (_: Exception) { }
    }

    fun clearSession() {
        try {
            prefs.edit().apply {
                remove(KEY_TOKEN)
                remove(KEY_EMAIL)
                remove(KEY_NAME)
                remove(KEY_ROLE)
                apply()
            }
        } catch (_: Exception) { }
    }
}
