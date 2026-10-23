package com.aurorah.can.ui.donation

import app.cash.turbine.test
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.DonationResponse
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DonationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ApiRepository = mock()
    private val sessionManager: SessionManager = mock()

    private lateinit var viewModel: DonationViewModel

    @Before
    fun setUp() {
        whenever(sessionManager.fetchUserEmail()).thenReturn("donor@aurorah.org")
        whenever(sessionManager.fetchUserName()).thenReturn("Donor Name")
        viewModel = DonationViewModel(repository, sessionManager)
    }

    @Test
    fun `init prefills donor email and name from session`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("donor@aurorah.org", state.donorEmail)
            assertEquals("Donor Name", state.donorName)
        }
    }

    @Test
    fun `onTabSelected switches monetary and in-kind tabs`() = runTest {
        viewModel.onTabSelected(false)
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isMonetaryTab)
        }

        viewModel.onTabSelected(true)
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isMonetaryTab)
        }
    }

    @Test
    fun `onAmountSelected updates selectedAmount`() = runTest {
        viewModel.onAmountSelected(500.0, isCustom = false)
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(500.0, state.selectedAmount, 0.01)
            assertFalse(state.isCustomAmountSelected)
        }
    }

    @Test
    fun `onCustomAmountChanged parses numeric input`() = runTest {
        viewModel.onCustomAmountChanged("750")
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("750", state.customAmountText)
            assertEquals(750.0, state.selectedAmount, 0.01)
        }
    }

    @Test
    fun `onAnonymousToggled sets anonymous name`() = runTest {
        viewModel.onAnonymousToggled(true)
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAnonymous)
            assertEquals("Anonymous Donor", state.donorName)
        }
    }

    @Test
    fun `submitDonation without popia consent sets errorMessage`() = runTest {
        viewModel.onPopiaConsentChanged(false)
        viewModel.submitDonation()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.errorMessage)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `submitDonation monetary success updates state`() = runTest {
        val donationResponse = DonationResponse(success = true, transactionRef = "TX123", message = "Thank you")
        whenever(repository.submitDonation(any())).thenReturn(NetworkResult.Success(donationResponse))

        viewModel.onPopiaConsentChanged(true)
        viewModel.submitDonation()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertEquals(donationResponse, state.donationResponse)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `submitDonation in-kind with empty description sets error`() = runTest {
        viewModel.onTabSelected(false)
        viewModel.onPopiaConsentChanged(true)
        viewModel.onItemDescriptionChanged("")
        viewModel.submitDonation()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.itemDescError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `submitDonation in-kind with pickup without address sets error`() = runTest {
        viewModel.onTabSelected(false)
        viewModel.onPopiaConsentChanged(true)
        viewModel.onItemDescriptionChanged("Warm coats")
        viewModel.onPickupToggled(true)
        viewModel.onPickupAddressChanged("")
        viewModel.submitDonation()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.pickupAddressError)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `resetForm clears inputs and success state`() = runTest {
        viewModel.resetForm()
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSuccess)
            assertNull(state.donationResponse)
            assertNull(state.errorMessage)
        }
    }
}
