package com.parkos.app.data.repository

import com.parkos.app.data.local.dao.UserDao
import com.parkos.app.data.local.entities.UserEntity
import com.parkos.app.data.remote.ApiService
import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.core.TokenManager
import com.parkos.app.domain.model.User
import com.parkos.app.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {

            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful) {

                val loginResponse =
                    response.body()
                        ?: return Result.failure(Exception("Empty body"))

                val userDto =
                    loginResponse.user
                        ?: return Result.failure(Exception("User data missing from response"))

                val metadata = userDto.metadata

                // FIX IMPORTANTE: roles válidos
                val safeUserType = metadata?.userType ?: "consumer"

                // Guardar sesión
                tokenManager.saveSession(
                    token = loginResponse.accessToken,
                    userId = userDto.id,
                    userType = metadata?.userType ?: "user",
                    orgId = metadata?.orgId
                )

                // Guardar en Room
                val userEntity = UserEntity(
                    id = userDto.id,
                    name = metadata?.name ?: "",
                    email = userDto.email,
                    userType = safeUserType,
                    orgId = metadata?.orgId
                )

                userDao.insert(userEntity)

                // Domain model
                val user = User(
                    id = userEntity.id,
                    email = userEntity.email,
                    name = userEntity.name,
                    userType = userEntity.userType,
                    orgId = userEntity.orgId
                )

                Result.success(user)

            } else {

                Result.failure(
                    Exception("Login failed: ${response.errorBody()?.string()}")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearSession()
        userDao.clearAll()
    }
}