package com.parkos.app.data.remote

import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.data.remote.dto.LoginResponse
import com.parkos.app.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("rest/v1/users")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String,
        @Query("select") select: String = "id,email,full_name,role,org_id"
    ): Response<List<UserProfileDto>>
}