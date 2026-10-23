package com.aurorah.can.data.model

import com.google.gson.annotations.SerializedName

// Clean Domain Models (Decoupled from Network DTOs)

data class User(
    val userId: Int = 1,
    val fullName: String = "User",
    val email: String = "",
    val role: String = "Volunteer",
    val token: String? = null
)

data class AuthResponse(
    val success: Boolean = true,
    val message: String = "",
    val token: String? = null,
    val user: User? = null
)

data class NewsItem(
    val id: Int = 0,
    val title: String = "",
    val summary: String = "",
    val body: String = "",
    val author: String = "Aurorah CAN Team",
    val timestamp: String = "",
    val category: String = "General",
    val imageUrl: String? = null
)

data class CommunityNeed(
    val needId: Int = 0,
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val priority: String = "Low",
    val category: String = "General",
    val status: String = "Open",
    val createdAt: String = ""
)

data class DonationResponse(
    val success: Boolean = true,
    val transactionRef: String = "",
    val message: String = ""
)

data class ApiResponse(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "message", alternate = ["error", "detail", "reason", "error_message", "description"]) val message: String = ""
)

// Request DTOs (used for Network Request payloads)

data class LoginRequest(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "password") val password: String
)

data class GoogleSsoRequest(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "name", alternate = ["full_name"]) val name: String
)

data class RegisterRequest(
    @SerializedName(value = "fullName", alternate = ["full_name"]) val fullName: String,
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "password") val password: String,
    @SerializedName(value = "role") val role: String
)

data class ChangePasswordRequest(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "oldPassword", alternate = ["old_password"]) val oldPassword: String,
    @SerializedName(value = "newPassword", alternate = ["new_password"]) val newPassword: String
)

data class OfferAssistanceRequest(
    @SerializedName(value = "needId", alternate = ["need_id"]) val needId: Int,
    @SerializedName(value = "userEmail", alternate = ["user_email", "email"]) val userEmail: String,
    @SerializedName(value = "volunteerSkill", alternate = ["volunteer_skill", "skill"]) val volunteerSkill: String,
    @SerializedName(value = "notes") val notes: String
)

data class AssistanceRequest(
    @SerializedName(value = "firstName", alternate = ["first_name"]) val firstName: String = "",
    @SerializedName(value = "lastName", alternate = ["last_name"]) val lastName: String = "",
    @SerializedName(value = "countryCode", alternate = ["country_code"]) val countryCode: String = "+27",
    @SerializedName(value = "category") val category: String,
    @SerializedName(value = "urgency") val urgency: String,
    @SerializedName(value = "areaLocation", alternate = ["area_location", "location", "area"]) val areaLocation: String,
    @SerializedName(value = "contactPhone", alternate = ["contact_phone", "phone"]) val contactPhone: String,
    @SerializedName(value = "description") val description: String,
    @SerializedName(value = "privacyConsent", alternate = ["privacy_consent", "popia"]) val privacyConsent: Boolean,
    @SerializedName(value = "userEmail", alternate = ["user_email", "email"]) val userEmail: String = ""
)

data class UpdateProfileRequest(
    @SerializedName(value = "fullName", alternate = ["full_name", "name"]) val fullName: String,
    @SerializedName(value = "phone", alternate = ["phone_number", "contact_phone"]) val phone: String,
    @SerializedName(value = "email") val email: String
)

data class DonationRequest(
    @SerializedName(value = "donationType", alternate = ["donation_type", "type"]) val donationType: String = "Monetary",
    @SerializedName(value = "category") val category: String,
    @SerializedName(value = "amount") val amount: Double? = null,
    @SerializedName(value = "paymentMethod", alternate = ["payment_method"]) val paymentMethod: String? = null,
    @SerializedName(value = "itemDescription", alternate = ["item_description", "description"]) val itemDescription: String? = null,
    @SerializedName(value = "preference", alternate = ["delivery_preference", "collection_preference", "logistics_type"]) val preference: String? = null,
    @SerializedName(value = "userEmail", alternate = ["user_email", "email"]) val userEmail: String? = null,
    @SerializedName(value = "donorName", alternate = ["donor_name", "full_name"]) val donorName: String? = null,
    @SerializedName(value = "phone", alternate = ["phone_number", "contact_phone"]) val phone: String? = null,
    @SerializedName(value = "countryCode", alternate = ["country_code"]) val countryCode: String? = "+27",
    @SerializedName(value = "message") val message: String? = null,
    @SerializedName(value = "isAnonymous", alternate = ["is_anonymous", "anonymous"]) val isAnonymous: Boolean = false,
    @SerializedName(value = "requestTaxCertificate", alternate = ["request_tax_certificate", "tax_certificate"]) val requestTaxCertificate: Boolean = false,
    @SerializedName(value = "taxIdNumber", alternate = ["tax_id_number", "tax_id"]) val taxIdNumber: String? = null,
    @SerializedName(value = "taxAddress", alternate = ["tax_address"]) val taxAddress: String? = null,
    @SerializedName(value = "pickupAddress", alternate = ["pickup_address"]) val pickupAddress: String? = null,
    @SerializedName(value = "popiaConsent", alternate = ["popia", "popia_consent"]) val popiaConsent: Boolean = true
)

data class VolunteerRequest(
    @SerializedName(value = "firstName", alternate = ["first_name"]) val firstName: String,
    @SerializedName(value = "lastName", alternate = ["last_name"]) val lastName: String,
    @SerializedName(value = "email") val email: String? = null,
    @SerializedName(value = "countryCode", alternate = ["country_code"]) val countryCode: String = "+27",
    @SerializedName(value = "phone") val phone: String,
    @SerializedName(value = "location") val location: String,
    @SerializedName(value = "skills") val skills: String,
    @SerializedName(value = "availability") val availability: List<String> = emptyList(),
    @SerializedName(value = "popiaConsent", alternate = ["popia", "popia_consent"]) val popiaConsent: Boolean = true,
    @SerializedName(value = "userEmail", alternate = ["user_email"]) val userEmail: String? = null
)
