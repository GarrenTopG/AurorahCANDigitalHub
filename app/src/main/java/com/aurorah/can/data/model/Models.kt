package com.aurorah.can.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName(value = "userId", alternate = ["user_id", "id"]) val userId: Int = 1,
    @SerializedName(value = "fullName", alternate = ["full_name", "name"]) val fullName: String = "User",
    @SerializedName(value = "email") val email: String = "",
    @SerializedName(value = "role", alternate = ["user_role"]) val role: String = "Volunteer",
    @SerializedName(value = "token", alternate = ["access_token", "jwt"]) val token: String? = null
)

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

data class AuthResponse(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "message") val message: String = "",
    @SerializedName(value = "token", alternate = ["access_token", "jwt"]) val token: String? = null,
    @SerializedName(value = "user") val user: User? = null
)

data class ChangePasswordRequest(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "oldPassword", alternate = ["old_password"]) val oldPassword: String,
    @SerializedName(value = "newPassword", alternate = ["new_password"]) val newPassword: String
)

data class NewsItem(
    @SerializedName(value = "id", alternate = ["news_id"]) val id: Int = 0,
    @SerializedName(value = "title") val title: String = "",
    @SerializedName(value = "summary", alternate = ["description"]) val summary: String = "",
    @SerializedName(value = "body", alternate = ["content", "full_text", "text"]) val body: String = "",
    @SerializedName(value = "author", alternate = ["by", "writer"]) val author: String = "Aurorah CAN Team",
    @SerializedName(value = "timestamp", alternate = ["created_at", "date", "published_at"]) val timestamp: String = "",
    @SerializedName(value = "category") val category: String = "General"
)

data class CommunityNeed(
    @SerializedName(value = "needId", alternate = ["need_id", "id"]) val needId: Int = 0,
    @SerializedName(value = "title") val title: String = "",
    @SerializedName(value = "description") val description: String = "",
    @SerializedName(value = "location") val location: String = "",
    @SerializedName(value = "priority") val priority: String = "Low", // High, Medium, Low, Critical
    @SerializedName(value = "category") val category: String = "General", // Food, Clothing, Education, Healthcare
    @SerializedName(value = "status") val status: String = "Open",
    @SerializedName(value = "createdAt", alternate = ["created_at", "date"]) val createdAt: String = ""
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

data class ApiResponse(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "message") val message: String = ""
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

data class DonationResponse(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "transactionRef", alternate = ["transaction_ref", "transaction_id", "ref"]) val transactionRef: String = "",
    @SerializedName(value = "message") val message: String = ""
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
