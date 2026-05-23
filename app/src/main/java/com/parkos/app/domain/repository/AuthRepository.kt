package com.parkos.app.domain.repository

import com.parkos.app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        orgId: String?
    ): Result<User>

    suspend fun getOrganizationIds(): Result<List<String>>

    suspend fun logout()
}