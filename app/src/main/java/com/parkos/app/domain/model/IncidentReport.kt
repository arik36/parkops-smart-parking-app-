package com.parkos.app.domain.model

data class IncidentReport(
    val id: String,
    val reportNumber: String,
    val parkingLotId: String,
    val parkingLotName: String,
    val parkingLotAddress: String?,
    val spotNumber: String?,
    val vehiclePlate: String,
    val incidentType: String,
    val customIncidentType: String?,
    val details: String?,
    val status: String,
    val createdAt: String
)