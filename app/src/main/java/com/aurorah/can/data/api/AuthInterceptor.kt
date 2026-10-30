package com.aurorah.can.data.api

import com.aurorah.can.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp [Interceptor] that inspects active authentication state via [tokenProvider] and attaches
 * an HTTP `Authorization: Bearer <token>` header to outgoing network requests when available.
 *
 * @property tokenProvider Lambda returning the current JWT auth token or `null` if unauthenticated.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    /** Secondary constructor accepting a [SessionManager] instance. */
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
