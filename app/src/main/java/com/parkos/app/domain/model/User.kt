package com.parkos.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val userType: String,
    val orgId: String?
)