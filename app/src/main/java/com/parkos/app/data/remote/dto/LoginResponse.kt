package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    val user: UserDto?
)

data class UserDto(
    val id: String,
    val email: String,

    @SerializedName("user_metadata")
    val metadata: UserMetadata?
)

data class UserMetadata(
    val name: String?,
    @SerializedName("user_type")
    val userType: String?,

    @SerializedName("org_id")
    val orgId: String?

)