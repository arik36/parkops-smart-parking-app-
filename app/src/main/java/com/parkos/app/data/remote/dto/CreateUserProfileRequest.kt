package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateUserProfileRequest(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("org_id")
    val orgId: String?,

    @SerializedName("staff_status")
    val staffStatus: String? = null
)