package com.aurorah.can

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aurorah.can.data.local.SessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sessionManager = SessionManager(context)
        sessionManager.clearSession()
    }

    @Test
    fun saveSession_PersistsAuthTokenAndUserAttributes() {
        sessionManager.saveSession(
            token = "firebase-sample-id-token-12345",
            email = "test.user@aurorahcan.org",
            name = "Test Volunteer",
            role = "Volunteer"
        )

        assertTrue(sessionManager.isLoggedIn())
        assertEquals("firebase-sample-id-token-12345", sessionManager.fetchAuthToken())
        assertEquals("test.user@aurorahcan.org", sessionManager.fetchUserEmail())
        assertEquals("Test Volunteer", sessionManager.fetchUserName())
        assertEquals("Volunteer", sessionManager.fetchUserRole())
    }

    @Test
    fun clearSession_ClearsAllSessionData() {
        sessionManager.saveSession(
            token = "token-to-be-cleared",
            email = "user@aurorahcan.org",
            name = "Clear User",
            role = "Admin"
        )

        sessionManager.clearSession()

        assertEquals(null, sessionManager.fetchAuthToken())
        assertEquals("guest@aurorahcan.org", sessionManager.fetchUserEmail())
        assertEquals("Guest User", sessionManager.fetchUserName())
        assertEquals("Volunteer", sessionManager.fetchUserRole())
    }
}
