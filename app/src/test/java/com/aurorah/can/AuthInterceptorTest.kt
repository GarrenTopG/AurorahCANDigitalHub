package com.aurorah.can

import com.aurorah.can.data.api.AuthInterceptor
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {

    private class FakeChain(private val request: Request) : Interceptor.Chain {
        var capturedRequest: Request? = null

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            capturedRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }

        override fun connection(): Connection? = null
        override fun call(): Call = throw UnsupportedOperationException()
        override fun connectTimeoutMillis(): Int = 0
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun readTimeoutMillis(): Int = 0
        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun writeTimeoutMillis(): Int = 0
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }

    @Test
    fun intercept_WithValidToken_AttachesBearerTokenHeader() {
        val testToken = "sample-jwt-token-xyz"
        val authInterceptor = AuthInterceptor { testToken }

        val originalRequest = Request.Builder()
            .url("http://10.0.2.2:8000/api/needs")
            .build()

        val chain = FakeChain(originalRequest)
        authInterceptor.intercept(chain)

        val capturedRequest = chain.capturedRequest
        assertEquals("Bearer $testToken", capturedRequest?.header("Authorization"))
    }

    @Test
    fun intercept_WithNullToken_DoesNotAttachAuthorizationHeader() {
        val authInterceptor = AuthInterceptor { null }

        val originalRequest = Request.Builder()
            .url("http://10.0.2.2:8000/api/news")
            .build()

        val chain = FakeChain(originalRequest)
        authInterceptor.intercept(chain)

        val capturedRequest = chain.capturedRequest
        assertEquals(null, capturedRequest?.header("Authorization"))
    }
}
