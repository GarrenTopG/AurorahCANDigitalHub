package com.aurorah.can.data.api

import android.content.Context
import com.aurorah.can.BuildConfig
import com.aurorah.can.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton factory providing thread-safe instantiation of Retrofit [ApiService] instances.
 *
 * Configures the underlying [OkHttpClient] with [AuthInterceptor] for dynamic JWT bearer token
 * injection, [HttpLoggingInterceptor] for debug logging, and connection timeouts optimized
 * for mobile network connectivity.
 */
object RetrofitClient {

    private val BASE_URL = BuildConfig.BASE_URL

    @Volatile
    private var apiServiceInstance: ApiService? = null

    /** Retrieves or initializes the singleton [ApiService] using an application [Context]. */
    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: createApiService(SessionManager(context.applicationContext)).also {
                apiServiceInstance = it
            }
        }
    }

    /** Retrieves or initializes the singleton [ApiService] using a [SessionManager]. */
    fun getApiService(sessionManager: SessionManager): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: createApiService(sessionManager).also {
                apiServiceInstance = it
            }
        }
    }

    /** Constructs the underlying Retrofit client instance. */
    private fun createApiService(sessionManager: SessionManager): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val authInterceptor = AuthInterceptor(sessionManager)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
