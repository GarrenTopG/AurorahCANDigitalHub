package com.aurorah.can.data.api

import com.aurorah.can.data.model.*
import kotlinx.coroutines.delay
import java.util.UUID

class MockApiRepository {

    suspend fun login(request: LoginRequest): AuthResponse {
        delay(600) // Simulate network delay
        return if (request.email.contains("@") && request.password.length >= 8) {
            AuthResponse(
                success = true,
                message = "Login successful",
                token = "jwt-mock-token-" + UUID.randomUUID().toString().take(8),
                user = User(
                    userId = 1,
                    fullName = if (request.email.startsWith("admin")) "Sarah Jenkins" else "Elena Rostova",
                    email = request.email,
                    role = if (request.email.startsWith("admin")) "Admin" else "Volunteer"
                )
            )
        } else {
            AuthResponse(
                success = false,
                message = "Invalid email or password credential"
            )
        }
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        delay(600)
        return AuthResponse(
            success = true,
            message = "Registration successful",
            token = "jwt-mock-token-" + UUID.randomUUID().toString().take(8),
            user = User(
                userId = (2..100).random(),
                fullName = request.fullName,
                email = request.email,
                role = request.role
            )
        )
    }

    suspend fun googleSso(email: String, name: String): AuthResponse {
        delay(500)
        return AuthResponse(
            success = true,
            message = "Google SSO sync successful",
            token = "jwt-sso-token-" + UUID.randomUUID().toString().take(8),
            user = User(
                userId = 10,
                fullName = name.ifBlank { "Google User" },
                email = email.ifBlank { "user.google@gmail.com" },
                role = "Volunteer"
            )
        )
    }

    suspend fun changePassword(request: ChangePasswordRequest): ApiResponse {
        delay(500)
        return ApiResponse(
            success = true,
            message = "Password updated successfully"
        )
    }

    suspend fun getNews(): List<NewsItem> {
        delay(400)
        return listOf(
            NewsItem(
                id = 1,
                title = "Winter Support Campaign Launch",
                summary = "Collecting thermal blankets, warm coats, and winter boots for displaced families in local sectors.",
                timestamp = "2 hours ago",
                category = "Campaign"
            ),
            NewsItem(
                id = 2,
                title = "Food Parcel Drive This Weekend",
                summary = "Volunteers needed at Kibera & Mathare distribution hubs to pack dry rations and nutritional supplies.",
                timestamp = "5 hours ago",
                category = "Event"
            ),
            NewsItem(
                id = 3,
                title = "Mobile Medical Clinic Expansion",
                summary = "Our medical team has provided free health checkups and basic prescriptions to over 350 community members.",
                timestamp = "1 day ago",
                category = "Healthcare"
            ),
            NewsItem(
                id = 4,
                title = "Youth Digital Literacy Lab Opening",
                summary = "10 refurbished computer workstations deployed at Kangemi Tech Center for coding and digital skill workshops.",
                timestamp = "2 days ago",
                category = "Education"
            ),
            NewsItem(
                id = 5,
                title = "Clean Water Filtration Installation Complete",
                summary = "Two commercial-grade filtration tanks installed in Korogocho, serving safe drinking water daily.",
                timestamp = "3 days ago",
                category = "Infrastructure"
            )
        )
    }

    suspend fun getNeeds(): List<CommunityNeed> {
        delay(500)
        return listOf(
            CommunityNeed(
                needId = 1,
                title = "Emergency Food Distribution Drive",
                description = "Providing essential dry food rations, flour, oil, and clean water packets to 200 vulnerable households.",
                location = "Kibera Community Center",
                priority = "High",
                category = "Food",
                status = "Open",
                createdAt = "2025-02-10"
            ),
            CommunityNeed(
                needId = 2,
                title = "Mobile Medical & First Aid Clinic",
                description = "Setting up a temporary mobile health unit with volunteer nurses to provide free checkups.",
                location = "Mathare Health Hub",
                priority = "Critical",
                category = "Healthcare",
                status = "In Progress",
                createdAt = "2025-02-11"
            ),
            CommunityNeed(
                needId = 3,
                title = "After-School STEM & Literacy Tutoring",
                description = "Weekly educational support and learning materials for primary school students needing homework assistance.",
                location = "Mukuru Resource Center",
                priority = "Medium",
                category = "Education",
                status = "Open",
                createdAt = "2025-02-12"
            ),
            CommunityNeed(
                needId = 4,
                title = "Clean Drinking Water Filtration Station",
                description = "Installation of two water filtration tanks to supply clean, safe drinking water.",
                location = "Korogocho Community School",
                priority = "High",
                category = "Healthcare",
                status = "Open",
                createdAt = "2025-02-13"
            ),
            CommunityNeed(
                needId = 5,
                title = "Winter Blankets & Warm Clothing Relief",
                description = "Collecting and distributing heavy coats, thermal blankets, and shoes for children and seniors.",
                location = "Kawangware Youth Hall",
                priority = "Medium",
                category = "Clothing",
                status = "Open",
                createdAt = "2025-02-14"
            ),
            CommunityNeed(
                needId = 6,
                title = "Maternal and Child Health Supplies",
                description = "Emergency delivery kits, infant formula, prenatal vitamins, and hygiene supplies.",
                location = "Dagoretti Care Center",
                priority = "Critical",
                category = "Healthcare",
                status = "Open",
                createdAt = "2025-02-15"
            ),
            CommunityNeed(
                needId = 7,
                title = "Community Shelter Roof Repair",
                description = "Urgent structural repairs and waterproofing for the primary community emergency shelter.",
                location = "Dandora Social Hall",
                priority = "High",
                category = "Clothing",
                status = "Open",
                createdAt = "2025-02-16"
            ),
            CommunityNeed(
                needId = 8,
                title = "Elderly Companion & Meal Delivery",
                description = "Daily hot meal delivery and basic wellness visits for homebound elderly community members.",
                location = "Baba Dogo Health Post",
                priority = "Low",
                category = "Food",
                status = "Open",
                createdAt = "2025-02-17"
            )
        )
    }

    suspend fun offerAssistance(request: OfferAssistanceRequest): ApiResponse {
        delay(600)
        return ApiResponse(
            success = true,
            message = "Your offer of assistance with skill '${request.volunteerSkill}' has been registered! A coordinator will contact you soon."
        )
    }

    suspend fun submitDonation(request: DonationRequest): DonationResponse {
        delay(800)
        val ref = "TXN-2025-" + (10000..99999).random()
        return DonationResponse(
            success = true,
            transactionRef = ref,
            message = "Thank you! Your donation of R${"%.2f".format(request.amount)} to ${request.category} via ${request.paymentMethod} was processed successfully."
        )
    }
}
