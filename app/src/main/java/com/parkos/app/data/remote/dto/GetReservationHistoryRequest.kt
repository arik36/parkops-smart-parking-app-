package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetReservationHistoryRequest(
    @SerializedName("p_limit")
    val limit: Int = 10
)