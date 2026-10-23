package com.aurorah.can.ui.settings

import app.cash.turbine.test
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.ApiResponse
import com.aurorah.can.data.model.AuthResponse
import com.aurorah.can.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private val sessionManager: SessionManager = mock()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        whenever(sessionManager.fetchUserName()).thenReturn("John Doe")
        whenever(sessionManager.fetchUserEmail()).thenReturn("john@aurorah.org")
        whenever(sessionManager.fetchUserPhone()).thenReturn("0820000000")
        whenever(sessionManager.fetchUserRole()).thenReturn("Volunteer")
        whenever(sessionManager.isNightModeEnabled()).thenReturn(false)
        whenever(sessionManager.areNotificationsEnabled()).thenReturn(true)
        whenever(sessionManager.isDataSaverEnabled()).thenReturn(false)
        whenever(sessionManager.getSearchRadius()).thenReturn("10 km")
        whenever(sessionManager.getAppLanguage()).thenReturn("English")

        viewModel = SettingsViewModel(repository, sessionManager)
    }

    @Test
    fun `loadUserSettings loads settings from sessionManager`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.userName)
            assertEquals("john@aurorah.org", state.userEmail)
            assertEquals("0820000000", state.userPhone)
            assertEquals("Volunteer", state.userRole)
            assertFalse(state.isNightMode)
            assertTrue(state.areNotificationsEnabled)
            assertFalse(state.isDataSaverEnabled)
            assertEquals("10 km", state.searchRadius)
            assertEquals("English", state.appLanguage)
        }
    }

    @Test
    fun `updateProfile success updates profile and toastMessage`() = runTest {
        whenever(repository.updateProfile(any())).thenReturn(
            NetworkResult.Success(AuthResponse(success = true, message = "Profile updated"))
        )

        viewModel.updateProfile("John Smith", "0831112222")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Profile updated", state.toastMessage)
            assertFalse(state.isLoading)
        }

        verify(sessionManager).updateUserProfile("John Smith", "0831112222")
    }

    @Test
    fun `updateProfile error sets toastMessage`() = runTest {
        whenever(repository.updateProfile(any())).thenReturn(
            NetworkResult.Error("Failed to update profile")
        )

        viewModel.updateProfile("John Smith", "0831112222")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Failed to update profile", state.toastMessage)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `setNightMode updates sessionManager and state`() = runTest {
        viewModel.setNightMode(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isNightMode)
        }
        verify(sessionManager).setNightModeEnabled(true)
    }

    @Test
    fun `setNotificationsEnabled updates sessionManager and state`() = runTest {
        viewModel.setNotificationsEnabled(false)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.areNotificationsEnabled)
            assertEquals("Notifications disabled", state.toastMessage)
        }
        verify(sessionManager).setNotificationsEnabled(false)
    }

    @Test
    fun `changePassword success sets toastMessage`() = runTest {
        whenever(repository.changePassword(any())).thenReturn(
            NetworkResult.Success(ApiResponse(success = true, message = "Password changed successfully"))
        )

        viewModel.changePassword("OldPass123!", "NewPass123!")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Password changed successfully", state.toastMessage)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `signOut clears session and sets isLoggedOut`() = runTest {
        viewModel.signOut()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoggedOut)
        }
        verify(sessionManager).clearSession()
    }

    @Test
    fun `deleteAccount success clears session and sets isAccountDeleted`() = runTest {
        whenever(repository.deleteAccount()).thenReturn(
            NetworkResult.Success(ApiResponse(success = true, message = "Account deleted"))
        )

        viewModel.deleteAccount()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAccountDeleted)
            assertEquals("Account deleted successfully", state.toastMessage)
            assertFalse(state.isLoading)
        }
        verify(sessionManager).clearSession()
    }

    @Test
    fun `clearToastMessage clears toast`() = runTest {
        viewModel.setNotificationsEnabled(true)
        viewModel.clearToastMessage()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.toastMessage)
        }
    }
}
