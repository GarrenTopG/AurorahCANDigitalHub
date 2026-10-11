package com.aurorah.can.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AurorahCAN_Session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_ROLE = "user_role"
        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }

    fun saveSession(token: String, email: String, name: String, role: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            putString(KEY_ROLE, role)
            apply()
        }
    }

    fun fetchAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun fetchUserEmail(): String = prefs.getString(KEY_EMAIL, "guest@aurorahcan.org") ?: "guest@aurorahcan.org"

    fun fetchUserName(): String = prefs.getString(KEY_NAME, "Guest User") ?: "Guest User"

    fun fetchUserRole(): String = prefs.getString(KEY_ROLE, "Volunteer") ?: "Volunteer"

    fun isLoggedIn(): Boolean = !fetchAuthToken().isNullOrBlank()

    fun isNightModeEnabled(): Boolean = prefs.getBoolean(KEY_NIGHT_MODE, false)

    fun setNightModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NIGHT_MODE, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATIONS, true)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_EMAIL)
            remove(KEY_NAME)
            remove(KEY_ROLE)
            apply()
        }
    }
}
