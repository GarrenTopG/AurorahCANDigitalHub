package com.aurorah.can

import com.aurorah.can.ui.donation.DonationInputValidator
import com.aurorah.can.ui.gethelp.GetHelpFormValidator
import com.aurorah.can.ui.gethelp.GetHelpUiState
import com.aurorah.can.ui.volunteer.VolunteerFormValidator
import com.aurorah.can.ui.volunteer.VolunteerUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyFormSubmissionTest {

    @Test
    fun emptyVolunteerForm_ShowsRequiredFieldErrorsAndDoesNotSubmit() {
        val state = VolunteerFormValidator.validate(VolunteerUiState())
        assertEquals("First name is required", state.firstNameError)
        assertEquals("Last name is required", state.lastNameError)
        assertEquals("Phone number is required", state.phoneError)
        assertEquals("Location/Sub-area is required", state.locationError)
        assertEquals("Please enter at least one skill", state.skillsError)
        assertEquals("Please select at least one day", state.daysError)
        assertEquals("You must agree to the POPIA consent to submit", state.popiaError)
        assertTrue(VolunteerFormValidator.hasErrors(state))
    }

    @Test
    fun emptyAssistanceForm_ShowsRequiredFieldErrorsAndDoesNotSubmit() {
        val state = GetHelpFormValidator.validate(GetHelpUiState())
        assertEquals("First name is required", state.firstNameError)
        assertEquals("Last name is required", state.lastNameError)
        assertEquals("Phone number is required", state.phoneError)
        assertEquals("Suburb or area is required", state.locationError)
        assertEquals("Please select a type of support", state.categoryError)
        assertEquals("Please describe what assistance you need", state.descriptionError)
        assertEquals("Consent is required to submit request", state.popiaError)
        assertTrue(GetHelpFormValidator.hasErrors(state))
    }

    @Test
    fun emptyDonationDetails_AreRejected() {
        assertFalse(DonationInputValidator.isValidItemDescription(""))
        assertFalse(DonationInputValidator.isValidItemDescription("   "))
        assertFalse(DonationInputValidator.isValidCustomAmount(""))
        assertFalse(DonationInputValidator.isValidCustomAmount("   "))
        assertFalse(DonationInputValidator.isValidCustomAmount("0"))
        assertFalse(DonationInputValidator.isValidPickupAddress(isPickup = true, address = ""))
    }
}
