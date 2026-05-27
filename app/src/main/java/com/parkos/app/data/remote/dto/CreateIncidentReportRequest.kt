package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateIncidentReportRequest(
    @SerializedName("p_parking_lot_id")
    val parkingLotId: String,

    @SerializedName("p_spot_number")
    val spotNumber: String,

    @SerializedName("p_vehicle_plate")
    val vehiclePlate: String,

    @SerializedName("p_incident_type")
    val incidentType: String,

    @SerializedName("p_custom_incident_type")
    val customIncidentType: String,

    @SerializedName("p_details")
    val details: String
)