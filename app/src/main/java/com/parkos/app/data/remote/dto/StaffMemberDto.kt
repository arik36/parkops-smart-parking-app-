package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.StaffMember

data class StaffMemberDto(
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
    val staffStatus: String?,

    @SerializedName("parking_lot_names")
    val parkingLotNames: String,

    @SerializedName("parking_lot_count")
    val parkingLotCount: Int
) {
    fun toDomain(): StaffMember {
        return StaffMember(
            userId = userId,
            email = email,
            fullName = fullName,
            role = role,
            orgId = orgId,
            staffStatus = staffStatus,
            parkingLotNames = parkingLotNames,
            parkingLotCount = parkingLotCount
        )
    }
}