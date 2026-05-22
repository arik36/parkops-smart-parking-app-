package com.parkos.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class CreateUserProfileRequest(
    val id: String,
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    val role: String,
    @SerializedName("org_id")
    val orgId: String?
)