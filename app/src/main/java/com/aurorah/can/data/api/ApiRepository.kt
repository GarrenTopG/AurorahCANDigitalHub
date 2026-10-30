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

/**
 * Single source of truth repository managing network requests, response transformations,
 * exception handling, and error payload parsing across the Aurorah CAN platform.
 *
 * Employs generic [safeApiCall] execution to wrap Retrofit responses into strongly typed
 * [NetworkResult] instances, shielding domain logic and ViewModels from raw HTTP primitives.
 *
 * @property apiService Underlying Retrofit service abstraction for backend endpoint invocations.
 */
@Singleton
class ApiRepository @Inject constructor(private val apiService: ApiService) {

    /** Secondary constructor allowing legacy direct instantiation with Application [Context]. */
    constructor(context: Context) : this(RetrofitClient.getApiService(context))

    /** Secondary constructor allowing direct instantiation with [SessionManager]. */
    constructor(sessionManager: SessionManager) : this(RetrofitClient.getApiService(sessionManager))

    private val gson = Gson()

    /**
     * Authenticates a user using email and password credentials.
     *
     * @param request [LoginRequest] object containing credentials.
     * @return [NetworkResult] wrapping the domain [AuthResponse] model.
     */
    suspend fun login(request: LoginRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.login(request) },
            transform = { it.toDomain() }
        )
    }

    /**
     * Registers a new community account.
     *
     * @param request [RegisterRequest] payload with user registration details.
     * @return [NetworkResult] containing registration outcome and auth session.
     */
    suspend fun register(request: RegisterRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.register(request) },
            transform = { it.toDomain() }
        )
    }

    /**
     * Performs Google Single Sign-On authentication using Google OAuth credentials.
     *
     * @param email User's Google email address.
     * @param name User's display name from Google OAuth profile.
     * @param idToken Optional ID token retrieved from Google Credential Manager.
     * @return [NetworkResult] containing session tokens and profile.
     */
    suspend fun googleSso(email: String, name: String, idToken: String? = null): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.googleSso(GoogleSsoRequest(email = email, name = name, idToken = idToken)) },
            transform = { it.toDomain() }
        )
    }

    /** Updates the password for the currently authenticated user. */
    suspend fun changePassword(request: ChangePasswordRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.changePassword(request) },
            transform = { it }
        )
    }

    /** Fetches published community news articles and impact stories. */
    suspend fun getNews(): NetworkResult<List<NewsItem>> {
        return safeApiCall(
            apiCall = { apiService.getNews() },
            transform = { list -> list.map { it.toDomain() } }
        )
    }

    /** Fetches active community needs and outreach initiatives. */
    suspend fun getNeeds(): NetworkResult<List<CommunityNeed>> {
        return safeApiCall(
            apiCall = { apiService.getNeeds() },
            transform = { list -> list.map { it.toDomain() } }
        )
    }

    /** Submits an offer to assist with a specific community need. */
    suspend fun offerAssistance(request: OfferAssistanceRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.offerAssistance(request) },
            transform = { it }
        )
    }

    /** Submits a community assistance request. */
    suspend fun requestAssistance(request: AssistanceRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.requestAssistance(request) },
            transform = { it }
        )
    }

    /** Updates the authenticated user's profile information. */
    suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.updateProfile(request) },
            transform = { it.toDomain() }
        )
    }

    /** Fetches profile information for the authenticated user. */
    suspend fun getProfile(): NetworkResult<AuthResponse> {
        return safeApiCall(
            apiCall = { apiService.getProfile() },
            transform = { it.toDomain() }
        )
    }

    /** Logs out the user session on the backend. Always guarantees local logout success. */
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

    /** Requests permanent account deletion. */
    suspend fun deleteAccount(): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.deleteAccount() },
            transform = { it }
        )
    }

    /** Submits a volunteer application form. */
    suspend fun submitVolunteer(request: VolunteerRequest): NetworkResult<ApiResponse> {
        return safeApiCall(
            apiCall = { apiService.submitVolunteer(request) },
            transform = { it }
        )
    }

    /** Submits a monetary or in-kind donation pledge. */
    suspend fun submitDonation(request: DonationRequest): NetworkResult<DonationResponse> {
        return safeApiCall(
            apiCall = { apiService.submitDonation(request) },
            transform = { it.toDomain() }
        )
    }

    /**
     * Executes a Retrofit network call safely, converting success states into domain models
     * and mapping non-2xx HTTP error codes or network exceptions into structured [NetworkResult.Error].
     *
     * @param T Raw response type returned by Retrofit service endpoint.
     * @param R Target domain type after applying [transform].
     * @param apiCall Suspend lambda invoking the Retrofit endpoint.
     * @param transform Mapping function converting DTO/data models into domain entities.
     * @return [NetworkResult] wrapping domain model [R] or descriptive error string.
     */
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

    /**
     * Extracts readable human-friendly error messages from backend HTTP error response bodies.
     * Inspects common REST JSON fields including `detail`, `error`, `message`, `reason`, etc.
     */
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

    /**
     * Maps thrown network exceptions (e.g. timeouts, host lookup failures) into helpful
     * user-facing messages.
     */
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
