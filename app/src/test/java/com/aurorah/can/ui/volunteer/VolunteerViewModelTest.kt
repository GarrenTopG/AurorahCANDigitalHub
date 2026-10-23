package com.aurorah.can.ui.volunteer

import app.cash.turbine.test
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.model.ApiResponse
import com.aurorah.can.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class VolunteerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private lateinit var viewModel: VolunteerViewModel

    @Before
    fun setUp() {
        viewModel = VolunteerViewModel(repository)
    }

    @Test
    fun `initUserInfo populates name email and phone`() = runTest {
        viewModel.initUserInfo("vol@aurorah.org", "Volunteer User", "0830000000")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Volunteer", state.firstName)
            assertEquals("User", state.lastName)
            assertEquals("vol@aurorah.org", state.email)
            assertEquals("0830000000", state.phone)
        }
    }

    @Test
    fun `toggleDay adds and removes day`() = runTest {
        viewModel.toggleDay("Monday")
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.selectedDays.contains("Monday"))
        }

        viewModel.toggleDay("Monday")
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.selectedDays.contains("Monday"))
        }
    }

    @Test
    fun `submitApplication with empty fields sets errors`() = runTest {
        viewModel.submitApplication()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.firstNameError)
            assertNotNull(state.lastNameError)
            assertNotNull(state.phoneError)
            assertNotNull(state.locationError)
            assertNotNull(state.skillsError)
            assertNotNull(state.daysError)
            assertNotNull(state.popiaError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `submitApplication success updates isSuccess`() = runTest {
        whenever(repository.submitVolunteer(any())).thenReturn(
            NetworkResult.Success(ApiResponse(success = true, message = "Application received"))
        )

        viewModel.onFirstNameChange("Sam")
        viewModel.onLastNameChange("Taylor")
        viewModel.onEmailChange("sam@aurorah.org")
        viewModel.onPhoneChange("0821112222")
        viewModel.onLocationChange("Kuils River")
        viewModel.onSkillsChange("Teaching, Logistics")
        viewModel.toggleDay("Saturdays")
        viewModel.onPopiaConsentChange(true)

        viewModel.uiState.test {
            awaitItem() // Initial
            viewModel.submitApplication()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertTrue(successState.isSuccess)
            assertFalse(successState.isLoading)
        }
    }

    @Test
    fun `submitApplication error sets serverError`() = runTest {
        whenever(repository.submitVolunteer(any())).thenReturn(
            NetworkResult.Error("Submission error")
        )

        viewModel.onFirstNameChange("Sam")
        viewModel.onLastNameChange("Taylor")
        viewModel.onPhoneChange("0821112222")
        viewModel.onLocationChange("Kuils River")
        viewModel.onSkillsChange("Teaching")
        viewModel.toggleDay("Saturdays")
        viewModel.onPopiaConsentChange(true)

        viewModel.uiState.test {
            awaitItem()
            viewModel.submitApplication()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertEquals("Submission error", errorState.serverError)
            assertFalse(errorState.isLoading)
        }
    }

    @Test
    fun `resetSuccess clears form`() = runTest {
        viewModel.onEmailChange("sam@aurorah.org")
        viewModel.onFirstNameChange("Sam")
        viewModel.resetSuccess()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.firstName)
            assertEquals("sam@aurorah.org", state.email)
            assertFalse(state.isSuccess)
        }
    }
}
