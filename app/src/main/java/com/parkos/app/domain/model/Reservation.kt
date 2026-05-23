package com.parkos.app.domain.model

data class Reservation(
    val id: String,
    val userId: String,
    val spotId: String,
    val status: String,
    val startTime: String,
    val endTime: String?,
    val createdAt: String?,
    val expiresAt: String?,
    val occupiedAt: String?
)