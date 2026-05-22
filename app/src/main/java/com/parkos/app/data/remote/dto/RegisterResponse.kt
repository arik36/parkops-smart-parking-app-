package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    // Formato posible 1: usuario dentro de "user"
    val user: RegisteredUserDto?,

    // Formato posible 2: usuario directamente en la raíz
    val id: String?,
    val email: String?,

    // Formato posible: access_token en la raíz
    @SerializedName("access_token")
    val accessToken: String?,

    // Formato posible: sesión anidada
    val session: RegisterSessionDto?
)

data class RegisteredUserDto(
    val id: String,
    val email: String?
)

data class RegisterSessionDto(
    @SerializedName("access_token")
    val accessToken: String?
)