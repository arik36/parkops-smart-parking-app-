package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.ReservationHistoryItem

data class ReservationHistoryDto(
    @SerializedName("reservation_id")
    val reservationId: String,

    @SerializedName("spot_id")
    val spotId: String,

    @SerializedName("parking_lot_id")
    val parkingLotId: String,

    @SerializedName("spot_number")
    val spotNumber: String,

    @SerializedName("spot_type")
    val spotType: String,

    @SerializedName("parking_lot_name")
    val parkingLotName: String,

    @SerializedName("parking_lot_address")
    val parkingLotAddress: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("start_time")
    val startTime: String?,

    @SerializedName("end_time")
    val endTime: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("expires_at")
    val expiresAt: String?,

    @SerializedName("occupied_at")
    val occupiedAt: String?
) {
    fun toDomain(): ReservationHistoryItem {
        return ReservationHistoryItem(
            reservationId = reservationId,
            spotId = spotId,
            parkingLotId = parkingLotId,
            spotNumber = spotNumber,
            spotType = spotType,
            parkingLotName = parkingLotName,
            parkingLotAddress = parkingLotAddress,
            status = status,
            startTime = startTime,
            endTime = endTime,
            createdAt = createdAt,
            expiresAt = expiresAt,
            occupiedAt = occupiedAt
        )
    }
}