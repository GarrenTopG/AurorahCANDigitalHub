package com.aurorah.can.ui.gethelp

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
class GetHelpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private lateinit var viewModel: GetHelpViewModel

    @Before
    fun setUp() {
        viewModel = GetHelpViewModel(repository)
    }

    @Test
    fun `initUserInfo populates name email and phone`() = runTest {
        viewModel.initUserInfo("john@doe.com", "John Doe", "0821234567")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John", state.firstName)
            assertEquals("Doe", state.lastName)
            assertEquals("john@doe.com", state.email)
            assertEquals("0821234567", state.phone)
        }
    }

    @Test
    fun `field change handlers update state`() = runTest {
        viewModel.onFirstNameChange("Jane")
        viewModel.onLastNameChange("Smith")
        viewModel.onEmailChange("jane@smith.com")
        viewModel.onCountryCodeChange("+27")
        viewModel.onPhoneChange("0839876543")
        viewModel.onLocationChange("Sunbird Park")
        viewModel.onCategoryChange("Food Parcel")
        viewModel.onDescriptionChange("Emergency food needed")
        viewModel.onUrgencyChange("Urgent")
        viewModel.onPopiaConsentChange(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Jane", state.firstName)
            assertEquals("Smith", state.lastName)
            assertEquals("jane@smith.com", state.email)
            assertEquals("+27", state.countryCode)
            assertEquals("0839876543", state.phone)
            assertEquals("Sunbird Park", state.location)
            assertEquals("Food Parcel", state.selectedCategory)
            assertEquals("Emergency food needed", state.description)
            assertEquals("Urgent", state.selectedUrgency)
            assertTrue(state.popiaConsent)
        }
    }

    @Test
    fun `submitRequest with blank fields sets errors`() = runTest {
        viewModel.submitRequest()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.firstNameError)
            assertNotNull(state.lastNameError)
            assertNotNull(state.phoneError)
            assertNotNull(state.locationError)
            assertNotNull(state.categoryError)
            assertNotNull(state.descriptionError)
            assertNotNull(state.popiaError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `submitRequest success updates state to isSuccess`() = runTest {
        whenever(repository.requestAssistance(any())).thenReturn(
            NetworkResult.Success(ApiResponse(success = true, message = "Submitted"))
        )

        viewModel.onFirstNameChange("Jane")
        viewModel.onLastNameChange("Smith")
        viewModel.onPhoneChange("0839876543")
        viewModel.onLocationChange("Sunbird Park")
        viewModel.onCategoryChange("Food Parcel")
        viewModel.onDescriptionChange("Emergency food needed")
        viewModel.onPopiaConsentChange(true)

        viewModel.uiState.test {
            awaitItem() // Initial
            viewModel.submitRequest()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertTrue(successState.isSuccess)
            assertEquals("Jane Smith", successState.submittedFullName)
            assertFalse(successState.isLoading)
        }
    }

    @Test
    fun `submitRequest error sets serverError`() = runTest {
        whenever(repository.requestAssistance(any())).thenReturn(
            NetworkResult.Error("Network error")
        )

        viewModel.onFirstNameChange("Jane")
        viewModel.onLastNameChange("Smith")
        viewModel.onPhoneChange("0839876543")
        viewModel.onLocationChange("Sunbird Park")
        viewModel.onCategoryChange("Food Parcel")
        viewModel.onDescriptionChange("Emergency food needed")
        viewModel.onPopiaConsentChange(true)

        viewModel.uiState.test {
            awaitItem()
            viewModel.submitRequest()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertEquals("Network error", errorState.serverError)
            assertFalse(errorState.isLoading)
        }
    }

    @Test
    fun `resetSuccess resets fields`() = runTest {
        viewModel.onEmailChange("jane@smith.com")
        viewModel.onFirstNameChange("Jane")
        viewModel.resetSuccess()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.firstName)
            assertEquals("jane@smith.com", state.email)
            assertFalse(state.isSuccess)
        }
    }
}
