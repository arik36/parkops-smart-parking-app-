package com.parkos.app.domain.repository

import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot

interface ParkingRepository {

    suspend fun getParkingLots(
        role: String,
        orgId: String?
    ): Result<List<ParkingLot>>

    suspend fun getParkingSpots(
        parkingLotId: String
    ): Result<List<ParkingSpot>>

    suspend fun reserveSpot(
        spotId: String
    ): Result<Unit>
}