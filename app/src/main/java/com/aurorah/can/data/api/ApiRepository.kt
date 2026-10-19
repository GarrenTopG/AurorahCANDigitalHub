package com.aurorah.can.data.api

import android.content.Context
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.*
import com.google.gson.Gson
import retrofit2.Response

class ApiRepository(private val apiService: ApiService) {

    constructor(context: Context) : this(RetrofitClient.getApiService(context))
    constructor(sessionManager: SessionManager) : this(RetrofitClient.getApiService(sessionManager))

    private val gson = Gson()

    suspend fun login(request: LoginRequest): AuthResponse {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: AuthResponse(
                    success = false,
                    message = "Login failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: AuthResponse(
                    success = false,
                    message = "Registration failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun googleSso(email: String, name: String): AuthResponse {
        return try {
            val response = apiService.googleSso(GoogleSsoRequest(email = email, name = name))
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: AuthResponse(
                    success = false,
                    message = "Google SSO authentication failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): ApiResponse {
        return try {
            val response = apiService.changePassword(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = false,
                    message = "Password update failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun getNews(): List<NewsItem> {
        return try {
            val response = apiService.getNews()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getNeeds(): List<CommunityNeed> {
        return try {
            val response = apiService.getNeeds()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun offerAssistance(request: OfferAssistanceRequest): ApiResponse {
        return try {
            val response = apiService.offerAssistance(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = false,
                    message = "Submitting offer failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun requestAssistance(request: AssistanceRequest): ApiResponse {
        return try {
            val response = apiService.requestAssistance(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = false,
                    message = "Submitting assistance request failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): AuthResponse {
        return try {
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: AuthResponse(
                    success = false,
                    message = "Updating profile failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun getProfile(): AuthResponse {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: AuthResponse(
                    success = false,
                    message = "Fetching profile failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun logout(): ApiResponse {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = true,
                    message = "Logged out successfully"
                )
            }
        } catch (_: Exception) {
            ApiResponse(success = true, message = "Logged out")
        }
    }

    suspend fun submitVolunteer(request: VolunteerRequest): ApiResponse {
        return try {
            val response = apiService.submitVolunteer(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = false,
                    message = "Volunteer submission failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    suspend fun submitDonation(request: DonationRequest): DonationResponse {
        return try {
            val response = apiService.submitDonation(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: DonationResponse(
                    success = false,
                    transactionRef = "",
                    message = "Donation processing failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            DonationResponse(
                success = false,
                transactionRef = "",
                message = e.localizedMessage ?: "Network connection error"
            )
        }
    }

    private inline fun <reified T> parseErrorResponse(response: Response<*>): T? {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank()) {
                gson.fromJson(errorJson, T::class.java)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
