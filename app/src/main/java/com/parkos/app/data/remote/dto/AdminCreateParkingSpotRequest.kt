package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminCreateParkingSpotRequest(
    @SerializedName("p_parking_lot_id")
    val parkingLotId: String,

    @SerializedName("p_floor_id")
    val floorId: String,

    @SerializedName("p_spot_number")
    val spotNumber: String,

    @SerializedName("p_type")
    val type: String,

    @SerializedName("p_row_index")
    val rowIndex: Int,

    @SerializedName("p_col_index")
    val colIndex: Int,

    @SerializedName("p_width_m")
    val widthM: Double? = null,

    @SerializedName("p_height_m")
    val heightM: Double? = null
)