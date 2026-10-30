package com.aurorah.can.data.api

import com.aurorah.can.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * Retrofit REST service declaration for the Aurorah CAN backend API endpoints.
 *
 * Defines asynchronous suspend endpoints for authentication, news feeds, community needs,
 * assistance requests, volunteer applications, and donation pledges.
 */
interface ApiService {
    /** Authenticates user with credentials. */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponseDto>

    /** Registers a new account. */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponseDto>

    /** Authenticates via Google Single Sign-On. */
    @POST("api/auth/google-sso")
    suspend fun googleSso(@Body request: GoogleSsoRequest): Response<AuthResponseDto>

    /** Updates the user's password. */
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse>

    /** Updates user profile information. */
    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<AuthResponseDto>

    /** Fetches currently authenticated user profile details. */
    @GET("api/auth/profile")
    suspend fun getProfile(): Response<AuthResponseDto>

    /** Invalidates the current user session on the backend. */
    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse>

    /** Permanently deletes the user account. */
    @DELETE("api/auth/account")
    suspend fun deleteAccount(): Response<ApiResponse>

    /** Fetches published news items and community updates. */
    @GET("api/news")
    suspend fun getNews(): Response<List<NewsItemDto>>

    /** Fetches verified community needs and projects. */
    @GET("api/needs")
    suspend fun getNeeds(): Response<List<CommunityNeedDto>>

    /** Submits an offer of assistance for a specific community need. */
    @POST("api/needs/offer-assistance")
    suspend fun offerAssistance(@Body request: OfferAssistanceRequest): Response<ApiResponse>

    /** Submits a request for community aid/assistance. */
    @POST("api/assistance-requests")
    suspend fun requestAssistance(@Body request: AssistanceRequest): Response<ApiResponse>

    /** Submits a volunteer registration form. */
    @POST("api/volunteers")
    suspend fun submitVolunteer(@Body request: VolunteerRequest): Response<ApiResponse>

    /** Submits a monetary or in-kind donation pledge. */
    @POST("api/donations")
    suspend fun submitDonation(@Body request: DonationRequest): Response<DonationResponseDto>
}
