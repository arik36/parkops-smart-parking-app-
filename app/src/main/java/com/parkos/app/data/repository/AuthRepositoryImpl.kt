package com.parkos.app.data.repository

import com.parkos.app.core.TokenManager
import com.parkos.app.data.local.dao.UserDao
import com.parkos.app.data.local.entities.UserEntity
import com.parkos.app.data.remote.ApiService
import com.parkos.app.data.remote.dto.CreateUserProfileRequest
import com.parkos.app.data.remote.dto.LoginRequest
import com.parkos.app.data.remote.dto.RegisterRequest
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
            val cleanEmail = email.trim()
            val cleanPassword = password.trim()

            if (cleanEmail.isBlank()) {
                return Result.failure(Exception("El email es obligatorio"))
            }

            if (cleanPassword.isBlank()) {
                return Result.failure(Exception("La contraseña es obligatoria"))
            }

            val loginResponse = apiService.login(
                LoginRequest(
                    email = cleanEmail,
                    password = cleanPassword
                )
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

            val role = validateRole(profile.role)

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

            Result.success(userEntity.toDomainUser())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        orgId: String?
    ): Result<User> {
        return try {
            val cleanName = fullName.trim()
            val cleanEmail = email.trim()
            val cleanPassword = password.trim()
            val cleanOrgId = orgId?.trim()?.takeIf { it.isNotBlank() }

            if (cleanName.isBlank()) {
                return Result.failure(Exception("El nombre es obligatorio"))
            }

            if (cleanEmail.isBlank()) {
                return Result.failure(Exception("El email es obligatorio"))
            }

            if (cleanPassword.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            if (cleanOrgId != null) {
                val organizationsResponse = apiService.getOrganizations()

                if (!organizationsResponse.isSuccessful) {
                    return Result.failure(
                        Exception("No se pudieron cargar las organizaciones: ${organizationsResponse.errorBody()?.string()}")
                    )
                }

                val orgExists = organizationsResponse.body()
                    ?.any { it.id == cleanOrgId }
                    ?: false

                if (!orgExists) {
                    return Result.failure(Exception("ID de organización inválido"))
                }
            }

            val registerResponse = apiService.register(
                RegisterRequest(
                    email = cleanEmail,
                    password = cleanPassword
                )
            )

            if (!registerResponse.isSuccessful) {
                return Result.failure(
                    Exception("Register failed: ${registerResponse.errorBody()?.string()}")
                )
            }

            val registerBody = registerResponse.body()
                ?: return Result.failure(Exception("Empty register response"))

            val registeredUserId = registerBody.user?.id
                ?: registerBody.id
                ?: return Result.failure(Exception("User id missing from register response"))

            val registeredUserEmail = registerBody.user?.email
                ?: registerBody.email
                ?: cleanEmail

            val accessTokenFromRegister = registerBody.accessToken
                ?: registerBody.session?.accessToken

            val accessToken = accessTokenFromRegister ?: run {
                val loginAfterRegisterResponse = apiService.login(
                    LoginRequest(
                        email = cleanEmail,
                        password = cleanPassword
                    )
                )

                if (!loginAfterRegisterResponse.isSuccessful) {
                    return Result.failure(
                        Exception(
                            "Usuario creado en Authentication, pero no se pudo iniciar sesión automáticamente: ${
                                loginAfterRegisterResponse.errorBody()?.string()
                            }"
                        )
                    )
                }

                loginAfterRegisterResponse.body()?.accessToken
                    ?: return Result.failure(
                        Exception("Usuario creado, pero Supabase no devolvió access token")
                    )
            }

            val role = if (cleanOrgId == null) {
                "consumer"
            } else {
                "collaborator"
            }

            val createProfileResponse = apiService.createUserProfile(
                authorization = "Bearer $accessToken",
                request = CreateUserProfileRequest(
                    id = registeredUserId,
                    email = registeredUserEmail,
                    fullName = cleanName,
                    role = role,
                    orgId = cleanOrgId
                )
            )

            if (!createProfileResponse.isSuccessful) {
                return Result.failure(
                    Exception("Profile creation failed: ${createProfileResponse.errorBody()?.string()}")
                )
            }

            val profile = createProfileResponse.body()
                ?.firstOrNull()
                ?: return Result.failure(Exception("Profile was created but no data was returned"))

            val safeRole = validateRole(profile.role)

            tokenManager.saveSession(
                token = accessToken,
                userId = profile.id,
                userType = safeRole,
                orgId = profile.orgId
            )

            val userEntity = UserEntity(
                id = profile.id,
                name = profile.fullName,
                email = profile.email,
                userType = safeRole,
                orgId = profile.orgId
            )

            userDao.insert(userEntity)

            Result.success(userEntity.toDomainUser())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrganizationIds(): Result<List<String>> {
        return try {
            val response = apiService.getOrganizations()

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudieron cargar las organizaciones: ${response.errorBody()?.string()}")
                )
            }

            val ids = response.body()
                ?.map { it.id }
                ?: emptyList()

            Result.success(ids)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearSession()
        userDao.clearAll()
    }

    private fun validateRole(role: String): String {
        return when (role) {
            "admin", "collaborator", "consumer" -> role
            else -> throw IllegalArgumentException("Invalid user role: $role")
        }
    }

    private fun UserEntity.toDomainUser(): User {
        return User(
            id = id,
            name = name,
            email = email,
            userType = userType,
            orgId = orgId
        )
    }
}