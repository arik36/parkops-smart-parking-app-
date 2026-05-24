package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminUpdateParkingSpotRequest(
    @SerializedName("p_spot_id")
    val spotId: String,

    @SerializedName("p_status")
    val status: String,

    @SerializedName("p_type")
    val type: String
)