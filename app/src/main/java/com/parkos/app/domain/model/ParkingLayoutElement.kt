package com.parkos.app.domain.model


data class ParkingLayoutElement(
    val id: String,
    val parkingLotId: String,
    val floorId: String,
    val elementType: String,
    val parkingSpotId: String?,
    val rowIndex: Int,
    val colIndex: Int,
    val widthM: Double?,
    val heightM: Double?,
    val label: String?,
    val description: String?
)