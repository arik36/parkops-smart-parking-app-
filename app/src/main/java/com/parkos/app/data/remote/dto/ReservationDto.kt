package com.parkos.app.data.remote.dto
import com.google.gson.annotations.SerializedName

data class ReservationDto(
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("spot_id")
    val spotId: String,

    val status: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String?,

    @SerializedName("created_at")
    val createdAt: String?
)