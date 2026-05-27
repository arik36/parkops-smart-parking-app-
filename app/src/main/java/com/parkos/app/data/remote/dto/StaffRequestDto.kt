package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.StaffRequest

data class StaffRequestDto(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("org_id")
    val orgId: String?,

    @SerializedName("staff_status")
    val staffStatus: String?
) {
    fun toDomain(): StaffRequest {
        return StaffRequest(
            userId = userId,
            email = email,
            fullName = fullName,
            role = role,
            orgId = orgId,
            staffStatus = staffStatus
        )
    }
}