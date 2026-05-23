package com.parkos.app.domain.repository

import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation

interface ParkingRepository {

    suspend fun getParkingLots(
        role: String,
        orgId: String?
    ): Result<List<ParkingLot>>

    suspend fun getParkingSpots(
        parkingLotId: String
    ): Result<List<ParkingSpot>>

    suspend fun getActiveReservation(): Result<Reservation?>

    suspend fun reserveSpot(
        spotId: String
    ): Result<Unit>

    suspend fun occupyReservedSpot(
        spotId: String
    ): Result<Unit>

    suspend fun releaseActiveReservation(): Result<Unit>

    suspend fun expireOldReservations(): Result<Unit>
}