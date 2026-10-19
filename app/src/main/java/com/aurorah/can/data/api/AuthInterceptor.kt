package com.aurorah.can.data.api

import com.aurorah.can.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that extracts the auth token from SessionManager and attaches
 * "Authorization: Bearer <token>" to outgoing HTTP requests.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    constructor(sessionManager: SessionManager) : this({ sessionManager.fetchAuthToken() })

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
