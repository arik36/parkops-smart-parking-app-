package com.parkos.app.di

import com.parkos.app.core.TokenManager
import com.parkos.app.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://uwsrxtcutgxkirilypnn.supabase.co/"

    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV3c3J4dGN1dGd4a2lyaWx5cG5uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzgyODAyNzQsImV4cCI6MjA5Mzg1NjI3NH0.NSUErWUHJD2BXaEC557o-_KB9TOD2Z-mEAnkQch20Hg"

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->

                val token = runBlocking { tokenManager.getTokenFlow().first() }

                val requestBuilder = chain.request().newBuilder()
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV3c3J4dGN1dGd4a2lyaWx5cG5uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzgyODAyNzQsImV4cCI6MjA5Mzg1NjI3NH0.NSUErWUHJD2BXaEC557o-_KB9TOD2Z-mEAnkQch20Hg") // obligatorio, no mover
                    .addHeader("Content-Type", "application/json")

                // 🔐 Solo agrega Authorization si ya hay token
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService {

        return retrofit.create(ApiService::class.java)
    }
}