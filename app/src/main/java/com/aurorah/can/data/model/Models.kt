package com.aurorah.can.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId") val userId: Int = 1,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String = "Volunteer",
    @SerializedName("token") val token: String? = null
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: User? = null
)

data class ChangePasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class NewsItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("category") val category: String = "General"
)

data class CommunityNeed(
    @SerializedName("needId") val needId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("priority") val priority: String, // High, Medium, Low, Critical
    @SerializedName("category") val category: String, // Food, Clothing, Education, Healthcare
    @SerializedName("status") val status: String = "Open",
    @SerializedName("createdAt") val createdAt: String = ""
)

data class OfferAssistanceRequest(
    @SerializedName("needId") val needId: Int,
    @SerializedName("userEmail") val userEmail: String,
    @SerializedName("volunteerSkill") val volunteerSkill: String,
    @SerializedName("notes") val notes: String
)

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class DonationRequest(
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("userEmail") val userEmail: String
)

data class DonationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("transactionRef") val transactionRef: String,
    @SerializedName("message") val message: String
)
