package com.parkos.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.parkos.app.domain.model.IncidentReport

data class IncidentReportDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("report_number")
    val reportNumber: String,

    @SerializedName("parking_lot_id")
    val parkingLotId: String,

    @SerializedName("parking_lot_name")
    val parkingLotName: String,

    @SerializedName("parking_lot_address")
    val parkingLotAddress: String?,

    @SerializedName("spot_number")
    val spotNumber: String?,

    @SerializedName("vehicle_plate")
    val vehiclePlate: String,

    @SerializedName("incident_type")
    val incidentType: String,

    @SerializedName("custom_incident_type")
    val customIncidentType: String?,

    @SerializedName("details")
    val details: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String
) {
    fun toDomain(): IncidentReport {
        return IncidentReport(
            id = id,
            reportNumber = reportNumber,
            parkingLotId = parkingLotId,
            parkingLotName = parkingLotName,
            parkingLotAddress = parkingLotAddress,
            spotNumber = spotNumber,
            vehiclePlate = vehiclePlate,
            incidentType = incidentType,
            customIncidentType = customIncidentType,
            details = details,
            status = status,
            createdAt = createdAt
        )
    }
}