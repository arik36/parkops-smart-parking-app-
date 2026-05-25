package com.parkos.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AdminDeleteParkingSpotRequest(
    @SerializedName("p_spot_id")
    val spotId: String
)