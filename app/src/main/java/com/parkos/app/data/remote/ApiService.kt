package com.parkos.app.data.remote

import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}