package com.aurorah.can.data.api

import com.aurorah.can.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/google-sso")
    suspend fun googleSso(@Body request: GoogleSsoRequest): Response<AuthResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse>

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<AuthResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse>

    @DELETE("api/auth/account")
    suspend fun deleteAccount(): Response<ApiResponse>

    @GET("api/news")
    suspend fun getNews(): Response<List<NewsItem>>

    @GET("api/needs")
    suspend fun getNeeds(): Response<List<CommunityNeed>>

    @POST("api/needs/offer-assistance")
    suspend fun offerAssistance(@Body request: OfferAssistanceRequest): Response<ApiResponse>

    @POST("api/assistance-requests")
    suspend fun requestAssistance(@Body request: AssistanceRequest): Response<ApiResponse>

    @POST("api/volunteers")
    suspend fun submitVolunteer(@Body request: VolunteerRequest): Response<ApiResponse>

    @POST("api/donations")
    suspend fun submitDonation(@Body request: DonationRequest): Response<DonationResponse>
}
