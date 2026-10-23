package com.aurorah.can.data.api

import android.content.Context
import com.aurorah.can.data.local.SessionManager
import com.aurorah.can.data.model.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepository @Inject constructor(private val apiService: ApiService) {

    constructor(context: Context) : this(RetrofitClient.getApiService(context))
    constructor(sessionManager: SessionManager) : this(RetrofitClient.getApiService(sessionManager))

    private val gson = Gson()

    suspend fun login(request: LoginRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.login(request) },
            transform = { it.toDomain() }
        )
    }

    suspend fun register(request: RegisterRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.register(request) },
            transform = { it.toDomain() }
        )
    }

    suspend fun googleSso(email: String, name: String): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.googleSso(GoogleSsoRequest(email = email, name = name)) },
            transform = { it.toDomain() }
        )
    }

    suspend fun changePassword(request: ChangePasswordRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.changePassword(request) },
            transform = { it }
        )
    }

    suspend fun getNews(): NetworkResult<List<NewsItem>> {
        return safeApiCall(
            apiCall = { apiService.getNews() },
            transform = { list -> list.map { it.toDomain() } }
        )
    }

    suspend fun getNeeds(): NetworkResult<List<CommunityNeed>> {
        return safeApiCall(
            apiCall = { apiService.getNeeds() },
            transform = { list -> list.map { it.toDomain() } }
        )
    }

    suspend fun offerAssistance(request: OfferAssistanceRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.offerAssistance(request) },
            transform = { it }
        )
    }

    suspend fun requestAssistance(request: AssistanceRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.requestAssistance(request) },
            transform = { it }
        )
    }

    suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.updateProfile(request) },
            transform = { it.toDomain() }
        )
    }

    suspend fun getProfile(): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.getProfile() },
            transform = { it.toDomain() }
        )
    }

    suspend fun logout(): NetworkResult<ApiResponse> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Success(ApiResponse(success = true, message = "Logged out successfully"))
            }
        } catch (_: Exception) {
            NetworkResult.Success(ApiResponse(success = true, message = "Logged out"))
        }
    }

    suspend fun deleteAccount(): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.deleteAccount() },
            transform = { it }
        )
    }

    suspend fun submitVolunteer(request: VolunteerRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.submitVolunteer(request) },
            transform = { it }
        )
    }

    suspend fun submitDonation(request: DonationRequest): NetworkResult<DonationResponse> {
        return safeApiCall(
            apiCall = { apiService.submitDonation(request) },
            transform = { it.toDomain() }
        )
    }

    private suspend fun <T, R> safeApiCall(
        apiCall: suspend () -> Response<T>,
        transform: (T) -> R
    ): NetworkResult<R> {
        return try {
            val response = apiCall()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(transform(response.body()!!))
            } else {
                val errorMessage = parseErrorMessage(response)
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(resolveNetworkErrorMessage(e), e)
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank()) {
                val extractedMessage = try {
                    val jsonElement = JsonParser.parseString(errorJson)
                    if (jsonElement.isJsonObject) {
                        val obj = jsonElement.asJsonObject
                        obj.get("detail")?.asString
                            ?: obj.get("error")?.asString
                            ?: obj.get("message")?.asString
                            ?: obj.get("reason")?.asString
                            ?: obj.get("error_message")?.asString
                            ?: obj.get("description")?.asString
                    } else null
                } catch (_: Exception) {
                    null
                }
                extractedMessage ?: "Operation failed (${response.code()})"
            } else {
                "Operation failed (${response.code()})"
            }
        } catch (_: Exception) {
            "Operation failed (${response.code()})"
        }
    }

    private fun resolveNetworkErrorMessage(e: Exception, defaultMessage: String = "Network connection error"): String {
        return when {
            e is SocketTimeoutException || e is InterruptedIOException || e.message?.contains("timeout", ignoreCase = true) == true -> {
                "Server is taking longer than expected to respond. Please try again in a moment."
            }
            e is UnknownHostException -> {
                "Unable to connect to server. Please check your internet connection."
            }
            !e.localizedMessage.isNullOrBlank() -> e.localizedMessage!!
            else -> defaultMessage
        }
    }
}
