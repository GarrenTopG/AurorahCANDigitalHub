package com.aurorah.can.data.api

import com.aurorah.can.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/google-sso")
    suspend fun googleSso(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse>

    @GET("api/news")
    suspend fun getNews(): Response<List<NewsItem>>

    @GET("api/needs")
    suspend fun getNeeds(): Response<List<CommunityNeed>>

    @POST("api/needs/offer-assistance")
    suspend fun offerAssistance(@Body request: OfferAssistanceRequest): Response<ApiResponse>

    @POST("api/donations")
    suspend fun submitDonation(@Body request: DonationRequest): Response<DonationResponse>
}
