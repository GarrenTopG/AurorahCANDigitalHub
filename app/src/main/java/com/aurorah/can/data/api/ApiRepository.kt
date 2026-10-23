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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
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

    suspend fun deleteAccount(): ApiResponse {
        return try {
            val response = apiService.deleteAccount()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                parseErrorResponse(response) ?: ApiResponse(
                    success = false,
                    message = "Account deletion failed (${response.code()})"
                )
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = resolveNetworkErrorMessage(e)
            )
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
                message = resolveNetworkErrorMessage(e)
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
                message = resolveNetworkErrorMessage(e)
            )
        }
    }

    private inline fun <reified T> parseErrorResponse(response: Response<*>): T? {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank()) {
                val parsed = gson.fromJson(errorJson, T::class.java)
                val extractedMessage = try {
                    val jsonElement = JsonParser.parseString(errorJson)
                    if (jsonElement.isJsonObject) {
                        val obj = jsonElement.asJsonObject
                        obj.get("detail")?.asString
                            ?: obj.get("error")?.asString
                            ?: obj.get("message")?.asString
                            ?: obj.get("reason")?.asString
                            ?: obj.get("error_message")?.asString
                    } else null
                } catch (_: Exception) {
                    null
                }

                val finalMessage = when {
                    parsed is AuthResponse && parsed.message.isNotBlank() -> parsed.message
                    parsed is ApiResponse && parsed.message.isNotBlank() -> parsed.message
                    !extractedMessage.isNullOrBlank() -> extractedMessage
                    else -> "Operation failed (${response.code()})"
                }

                @Suppress("UNCHECKED_CAST")
                when (T::class) {
                    AuthResponse::class -> AuthResponse(
                        success = false,
                        message = finalMessage,
                        token = (parsed as? AuthResponse)?.token,
                        user = (parsed as? AuthResponse)?.user
                    ) as T
                    ApiResponse::class -> ApiResponse(
                        success = false,
                        message = finalMessage
                    ) as T
                    else -> parsed
                }
            } else {
                null
            }
        } catch (_: Exception) {
            val fallbackMessage = "Operation failed (${response.code()})"
            @Suppress("UNCHECKED_CAST")
            when (T::class) {
                AuthResponse::class -> AuthResponse(success = false, message = fallbackMessage) as T
                ApiResponse::class -> ApiResponse(success = false, message = fallbackMessage) as T
                else -> null
            }
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
            !e.localizedMessage.isNullOrBlank() -> e.localizedMessage
            else -> defaultMessage
        }
    }
}
