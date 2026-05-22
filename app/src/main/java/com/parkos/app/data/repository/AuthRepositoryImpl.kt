package com.parkos.app.data.repository

import com.parkos.app.core.TokenManager
import com.parkos.app.data.local.dao.UserDao
import com.parkos.app.data.local.entities.UserEntity
import com.parkos.app.data.remote.ApiService
import com.parkos.app.data.remote.dto.LoginRequest
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
            val loginResponse = apiService.login(
                LoginRequest(email, password)
            )

            if (!loginResponse.isSuccessful) {
                return Result.failure(
                    Exception("Login failed: ${loginResponse.errorBody()?.string()}")
                )
            }

            val loginBody = loginResponse.body()
                ?: return Result.failure(Exception("Empty login response"))

            val authUser = loginBody.user
                ?: return Result.failure(Exception("User data missing from login response"))

            val profileResponse = apiService.getUserProfile(
                authorization = "Bearer ${loginBody.accessToken}",
                idFilter = "eq.${authUser.id}"
            )

            if (!profileResponse.isSuccessful) {
                return Result.failure(
                    Exception("Profile failed: ${profileResponse.errorBody()?.string()}")
                )
            }

            val profile = profileResponse.body()
                ?.firstOrNull()
                ?: return Result.failure(Exception("No profile found in public.users"))

            val role = when (profile.role) {
                "admin", "collaborator", "consumer" -> profile.role
                else -> return Result.failure(Exception("Invalid user role: ${profile.role}"))
            }

            tokenManager.saveSession(
                token = loginBody.accessToken,
                userId = profile.id,
                userType = role,
                orgId = profile.orgId
            )

            val userEntity = UserEntity(
                id = profile.id,
                name = profile.fullName,
                email = profile.email,
                userType = role,
                orgId = profile.orgId
            )

            userDao.insert(userEntity)

            val user = User(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email,
                userType = userEntity.userType,
                orgId = userEntity.orgId
            )

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearSession()
        userDao.clearAll()
    }
}