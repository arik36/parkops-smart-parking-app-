package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateFullNameRequest(
    @SerializedName("p_full_name")
    val fullName: String
)