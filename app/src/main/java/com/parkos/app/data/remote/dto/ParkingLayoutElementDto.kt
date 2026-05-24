package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.ParkingLayoutElement

data class ParkingLayoutElementDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("parking_lot_id")
    val parkingLotId: String,

    @SerializedName("floor_id")
    val floorId: String,

    @SerializedName("element_type")
    val elementType: String,

    @SerializedName("parking_spot_id")
    val parkingSpotId: String?,

    @SerializedName("row_index")
    val rowIndex: Int,

    @SerializedName("col_index")
    val colIndex: Int,

    @SerializedName("width_m")
    val widthM: Double?,

    @SerializedName("height_m")
    val heightM: Double?,

    @SerializedName("label")
    val label: String?,

    @SerializedName("description")
    val description: String?
) {
    fun toDomain(): ParkingLayoutElement {
        return ParkingLayoutElement(
            id = id,
            parkingLotId = parkingLotId,
            floorId = floorId,
            elementType = elementType,
            parkingSpotId = parkingSpotId,
            rowIndex = rowIndex,
            colIndex = colIndex,
            widthM = widthM,
            heightM = heightM,
            label = label,
            description = description
        )
    }
}