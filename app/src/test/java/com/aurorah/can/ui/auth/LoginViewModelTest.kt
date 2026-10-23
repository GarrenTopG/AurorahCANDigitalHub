package com.aurorah.can.ui.auth

import app.cash.turbine.test
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.AuthResponse
import com.aurorah.can.data.model.User
import com.aurorah.can.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private val sessionManager: SessionManager = mock()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(repository, sessionManager)
    }

    @Test
    fun `onEmailChanged updates email and clears errors`() = runTest {
        viewModel.onEmailChanged("user@aurorah.org")
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("user@aurorah.org", state.email)
            assertNull(state.emailError)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `onPasswordChanged updates password and clears errors`() = runTest {
        viewModel.onPasswordChanged("SecurePass123!")
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("SecurePass123!", state.password)
            assertNull(state.passwordError)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `hideError clears errorMessage`() = runTest {
        viewModel.hideError()
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `login with invalid email sets emailError`() = runTest {
        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("SecurePass123!")

        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Invalid Email Address", state.emailError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `login with insecure password sets passwordError`() = runTest {
        viewModel.onEmailChanged("test@aurorah.org")
        viewModel.onPasswordChanged("short")

        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.passwordError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `login success saves session and sets isSuccess`() = runTest {
        val user = User(userId = 1, fullName = "Test User", email = "test@aurorah.org", role = "Donor")
        val authResponse = AuthResponse(success = true, message = "Success", token = "jwt-123", user = user)

        whenever(repository.login(any())).thenReturn(NetworkResult.Success(authResponse))

        viewModel.onEmailChanged("test@aurorah.org")
        viewModel.onPasswordChanged("SecurePass123!")

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isSuccess)

            viewModel.login()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertTrue(successState.isSuccess)
            assertEquals("Test User", successState.userFullName)
            assertFalse(successState.isLoading)
        }

        verify(sessionManager).saveSession(
            token = "jwt-123",
            email = "test@aurorah.org",
            name = "Test User",
            role = "Donor"
        )
    }

    @Test
    fun `login error response sets errorMessage`() = runTest {
        whenever(repository.login(any())).thenReturn(NetworkResult.Error("Invalid credentials"))

        viewModel.onEmailChanged("test@aurorah.org")
        viewModel.onPasswordChanged("SecurePass123!")

        viewModel.uiState.test {
            awaitItem() // Initial
            viewModel.login()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertEquals("Invalid credentials", errorState.errorMessage)
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isSuccess)
        }
    }

    @Test
    fun `login failure response with message sets errorMessage`() = runTest {
        val authResponse = AuthResponse(success = false, message = "User suspended", token = null, user = null)
        whenever(repository.login(any())).thenReturn(NetworkResult.Success(authResponse))

        viewModel.onEmailChanged("test@aurorah.org")
        viewModel.onPasswordChanged("SecurePass123!")

        viewModel.uiState.test {
            awaitItem()
            viewModel.login()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val state = awaitItem()
            assertEquals("User suspended", state.errorMessage)
            assertFalse(state.isLoading)
        }
    }
}
