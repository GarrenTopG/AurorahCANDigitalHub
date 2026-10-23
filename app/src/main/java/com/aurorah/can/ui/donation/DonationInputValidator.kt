package com.aurorah.can.ui.donation

internal object DonationInputValidator {
    fun isValidCustomAmount(amount: String): Boolean =
        amount.trim().toDoubleOrNull()?.let { it > 0.0 } == true

    fun isValidItemDescription(description: String): Boolean = description.isNotBlank()

    fun isValidPickupAddress(isPickup: Boolean, address: String?): Boolean =
        !isPickup || !address.isNullOrBlank()
}
