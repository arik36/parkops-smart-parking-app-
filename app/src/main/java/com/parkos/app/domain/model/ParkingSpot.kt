package com.parkos.app.domain.model

data class ParkingSpot(
    val id: String,
    val parkingLotId: String,
    val spotNumber: String,
    val status: String,
    val type: String,
    val updatedAt: String?
)