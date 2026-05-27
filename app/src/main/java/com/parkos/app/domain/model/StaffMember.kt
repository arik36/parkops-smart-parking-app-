package com.parkos.app.domain.model

data class StaffMember(
    val userId: String,
    val email: String,
    val fullName: String,
    val role: String,
    val orgId: String?,
    val staffStatus: String?,
    val parkingLotNames: String,
    val parkingLotCount: Int
)