package com.parkos.app.data.remote.dto
import com.google.gson.annotations.SerializedName

data class ParkingSpotDto(
    val id: String,

    @SerializedName("parking_lot_id")
    val parkingLotId: String,

    @SerializedName("spot_number")
    val spotNumber: String,

    val status: String,
    val type: String,

    @SerializedName("updated_at")
    val updatedAt: String?
)
