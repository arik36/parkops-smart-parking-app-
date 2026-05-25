package com.parkos.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AdminCreateLayoutElementRequest(
    @SerializedName("p_parking_lot_id")
    val parkingLotId: String,

    @SerializedName("p_floor_id")
    val floorId: String,

    @SerializedName("p_element_type")
    val elementType: String,

    @SerializedName("p_row_index")
    val rowIndex: Int,

    @SerializedName("p_col_index")
    val colIndex: Int,

    @SerializedName("p_label")
    val label: String? = null,

    @SerializedName("p_description")
    val description: String? = null
)