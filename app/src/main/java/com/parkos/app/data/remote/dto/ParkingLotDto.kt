package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ParkingLotDto(
    val id: String,

    @SerializedName("org_id")
    val orgId: String,

    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,

    @SerializedName("created_at")
    val createdAt: String?
)