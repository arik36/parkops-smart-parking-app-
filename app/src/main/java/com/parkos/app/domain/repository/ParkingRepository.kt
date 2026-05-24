package com.parkos.app.domain.repository

import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.domain.model.ParkingFloor
import com.parkos.app.domain.model.ParkingLayoutElement

interface ParkingRepository {

    suspend fun getParkingLots(
        role: String,
        orgId: String?
    ): Result<List<ParkingLot>>

    suspend fun getParkingSpots(
        parkingLotId: String
    ): Result<List<ParkingSpot>>

    suspend fun adminUpdateParkingSpot(
        spotId: String,
        status: String,
        type: String
    ): Result<ParkingSpot>

    suspend fun getActiveReservation(): Result<Reservation?>

    suspend fun reserveSpot(
        spotId: String
    ): Result<Unit>

    suspend fun occupyReservedSpot(
        spotId: String
    ): Result<Unit>

    suspend fun getParkingFloors(
        parkingLotId: String
    ): Result<List<ParkingFloor>>

    suspend fun adminCreateParkingSpot(
        parkingLotId: String,
        floorId: String,
        spotNumber: String,
        type: String,
        rowIndex: Int,
        colIndex: Int,
        widthM: Double?,
        heightM: Double?
    ): Result<ParkingSpot>

    suspend fun getParkingLayoutElements(
        parkingLotId: String
    ): Result<List<ParkingLayoutElement>>

    suspend fun releaseActiveReservation(): Result<Unit>

    suspend fun expireOldReservations(): Result<Unit>
}