package com.parkos.app.data.remote

import com.parkos.app.data.remote.dto.CreateUserProfileRequest
import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.data.remote.dto.LoginResponse
import com.parkos.app.data.remote.dto.OrganizationDto
import com.parkos.app.data.remote.dto.RegisterRequest
import com.parkos.app.data.remote.dto.RegisterResponse
import com.parkos.app.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("rest/v1/users")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String,
        @Query("select") select: String = "id,email,full_name,role,org_id"
    ): Response<List<UserProfileDto>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/users")
    suspend fun createUserProfile(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserProfileRequest
    ): Response<List<UserProfileDto>>

    @GET("rest/v1/organizations")
    suspend fun getOrganizations(
        @Query("select") select: String = "id"
    ): Response<List<OrganizationDto>>
}