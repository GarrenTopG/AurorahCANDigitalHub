package com.aurorah.can.data.api

import android.content.Context
import com.aurorah.can.BuildConfig
import com.aurorah.can.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://aurorah-web-hub.onrender.com/"

    @Volatile
    private var apiServiceInstance: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: createApiService(SessionManager(context.applicationContext)).also {
                apiServiceInstance = it
            }
        }
    }

    fun getApiService(sessionManager: SessionManager): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: createApiService(sessionManager).also {
                apiServiceInstance = it
            }
        }
    }

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
