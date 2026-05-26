package com.parkos.app.domain.model

data class ReservationHistoryItem(
    val reservationId: String,
    val spotId: String,
    val parkingLotId: String,
    val spotNumber: String,
    val spotType: String,
    val parkingLotName: String,
    val parkingLotAddress: String,
    val status: String,
    val startTime: String?,
    val endTime: String?,
    val createdAt: String?,
    val expiresAt: String?,
    val occupiedAt: String?
)