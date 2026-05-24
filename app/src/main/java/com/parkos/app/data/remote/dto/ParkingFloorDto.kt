package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.ParkingFloor

data class ParkingFloorDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("parking_lot_id")
    val parkingLotId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("floor_order")
    val floorOrder: Int,

    @SerializedName("rows")
    val rows: Int,

    @SerializedName("cols")
    val cols: Int
) {
    fun toDomain(): ParkingFloor {
        return ParkingFloor(
            id = id,
            parkingLotId = parkingLotId,
            name = name,
            floorOrder = floorOrder,
            rows = rows,
            cols = cols
        )
    }
}