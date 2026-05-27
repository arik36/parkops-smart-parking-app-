package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RevokeStaffAccessRequest(
    @SerializedName("p_user_id")
    val userId: String
)