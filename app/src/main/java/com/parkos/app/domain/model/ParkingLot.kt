package com.parkos.app.domain.model

data class ParkingLot(
    val id: String,
    val orgId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String?,
    val availableSpots: Int = 0,
    val totalSpots: Int = 0
)