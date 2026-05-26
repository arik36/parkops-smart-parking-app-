package com.parkos.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AdminMoveLayoutElementRequest(
    @SerializedName("p_element_id")
    val elementId: String,

    @SerializedName("p_target_floor_id")
    val targetFloorId: String,

    @SerializedName("p_target_row_index")
    val targetRowIndex: Int,

    @SerializedName("p_target_col_index")
    val targetColIndex: Int
)