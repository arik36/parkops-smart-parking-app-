package com.parkos.app.domain.model

data class ParkingFloor(
    val id: String,
    val parkingLotId: String,
    val name: String,
    val floorOrder: Int,
    val rows: Int,
    val cols: Int
)