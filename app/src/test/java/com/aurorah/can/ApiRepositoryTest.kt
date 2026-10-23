package com.aurorah.can

import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.api.ApiService
import com.aurorah.can.data.api.NetworkResult
import com.aurorah.can.data.model.RegisterRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiRepositoryTest {

    private fun createMockApiService(handler: (request: Request) -> Response): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                handler(chain.request())
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Test
    fun testRegisterSuccess() = runBlocking {
        val jsonResponse = """
            {
                "success": true,
                "message": "User registered successfully",
                "token": "test-jwt-token",
                "user": {
                    "userId": 1,
                    "fullName": "Aubrey Graham",
                    "email": "drake@aurorah.co.za",
                    "role": "Donor"
                }
            }
        """.trimIndent()

        val apiService = createMockApiService { req ->
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                .build()
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Success)
        val response = (result as NetworkResult.Success).data
        assertTrue(response.success)
        assertEquals("User registered successfully", response.message)
        assertEquals("test-jwt-token", response.token)
        assertEquals("Aubrey Graham", response.user?.fullName)
    }

    @Test
    fun testRegisterError400_StandardJson() = runBlocking {
        val errorJson = """
            {
                "success": false,
                "message": "Email already registered"
            }
        """.trimIndent()

        val apiService = createMockApiService { req ->
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(400)
                .message("Bad Request")
                .body(errorJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Error)
        assertEquals("Email already registered", (result as NetworkResult.Error).message)
    }

    @Test
    fun testRegisterError500_HtmlBody() = runBlocking {
        val htmlError = "<html><body>500 Internal Server Error</body></html>"

        val apiService = createMockApiService { req ->
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Server Error")
                .body(htmlError.toResponseBody("text/html".toMediaType()))
                .build()
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message.contains("500"))
    }

    @Test
    fun testRegisterError_DetailKeyInJson() = runBlocking {
        val detailJson = """{"detail": "User already exists"}"""

        val apiService = createMockApiService { req ->
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(400)
                .message("Bad Request")
                .body(detailJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Error)
        assertEquals("User already exists", (result as NetworkResult.Error).message)
    }

    @Test
    fun testRegisterSocketTimeoutException() = runBlocking {
        val apiService = createMockApiService { _ ->
            throw SocketTimeoutException("timeout")
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message.contains("longer than expected", ignoreCase = true))
    }

    @Test
    fun testRegisterUnknownHostException() = runBlocking {
        val apiService = createMockApiService { _ ->
            throw UnknownHostException("Unable to resolve host")
        }

        val repository = ApiRepository(apiService)
        val result = repository.register(
            RegisterRequest(
                fullName = "Aubrey Graham",
                email = "drake@aurorah.co.za",
                password = "Password123!",
                role = "Donor"
            )
        )

        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message.contains("Unable to connect", ignoreCase = true))
    }
}
