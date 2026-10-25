package com.aurorah.can.ui.donation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DonationInputValidatorTest {

    @Test
    fun isValidCustomAmount_returnsTrueForTenOrGreater() {
        assertTrue(DonationInputValidator.isValidCustomAmount("10"))
        assertTrue(DonationInputValidator.isValidCustomAmount("10.0"))
        assertTrue(DonationInputValidator.isValidCustomAmount("250"))
    }

    @Test
    fun isValidCustomAmount_returnsFalseForLessThanTenOrInvalid() {
        assertFalse(DonationInputValidator.isValidCustomAmount("9.99"))
        assertFalse(DonationInputValidator.isValidCustomAmount("0"))
        assertFalse(DonationInputValidator.isValidCustomAmount("-50"))
        assertFalse(DonationInputValidator.isValidCustomAmount("abc"))
        assertFalse(DonationInputValidator.isValidCustomAmount(""))
    }

    @Test
    fun isValidItemDescription_returnsFalseWhenBlank() {
        assertTrue(DonationInputValidator.isValidItemDescription("5 boxes of canned goods"))
        assertFalse(DonationInputValidator.isValidItemDescription("   "))
    }

    @Test
    fun isValidPickupAddress_validatesPickupAddressRequirement() {
        assertTrue(DonationInputValidator.isValidPickupAddress(isPickup = false, address = null))
        assertTrue(DonationInputValidator.isValidPickupAddress(isPickup = true, address = "123 Main Rd"))
        assertFalse(DonationInputValidator.isValidPickupAddress(isPickup = true, address = ""))
        assertFalse(DonationInputValidator.isValidPickupAddress(isPickup = true, address = null))
    }
}
