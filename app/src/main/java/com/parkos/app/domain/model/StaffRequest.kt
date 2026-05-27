package com.parkos.app.domain.model

data class StaffRequest(
    val userId: String,
    val email: String,
    val fullName: String,
    val role: String,
    val orgId: String?,
    val staffStatus: String?
)