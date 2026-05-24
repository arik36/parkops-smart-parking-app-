package com.parkos.app.data.repository

import com.parkos.app.data.remote.ApiService
import com.parkos.app.data.remote.dto.ParkingLotDto
import com.parkos.app.data.remote.dto.ParkingSpotDto
import com.parkos.app.data.remote.dto.ReservationDto
import com.parkos.app.data.remote.dto.ReserveSpotRequest
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.domain.repository.ParkingRepository
import com.parkos.app.data.remote.dto.AdminUpdateParkingSpotRequest
import com.parkos.app.data.remote.dto.AdminCreateParkingSpotRequest
import com.parkos.app.domain.model.ParkingFloor
import javax.inject.Inject
import javax.inject.Singleton
import com.parkos.app.domain.model.ParkingLayoutElement

@Singleton
class ParkingRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ParkingRepository {

    override suspend fun getParkingLots(
        role: String,
        orgId: String?
    ): Result<List<ParkingLot>> {
        return try {
            expireOldReservations()

            val response = if (role == "admin" || role == "collaborator") {
                if (orgId.isNullOrBlank()) {
                    return Result.failure(Exception("Este usuario no tiene organización asignada."))
                }

                apiService.getParkingLotsByOrg(
                    orgFilter = "eq.$orgId"
                )
            } else {
                apiService.getParkingLots()
            }

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudieron cargar los estacionamientos: ${response.errorBody()?.string()}")
                )
            }

            val lots = response.body().orEmpty()

            val lotsWithAvailability = lots.map { lotDto ->
                val spotsResponse = apiService.getParkingSpots(
                    parkingLotFilter = "eq.${lotDto.id}"
                )

                val spots = if (spotsResponse.isSuccessful) {
                    spotsResponse.body().orEmpty()
                } else {
                    emptyList()
                }

                lotDto.toDomain(
                    availableSpots = spots.count {
                        it.status.equals("available", ignoreCase = true)
                    },
                    totalSpots = spots.size
                )
            }

            Result.success(lotsWithAvailability)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParkingLayoutElements(
        parkingLotId: String
    ): Result<List<ParkingLayoutElement>> {
        return try {
            val response = apiService.getParkingLayoutElements(
                parkingLotIdFilter = "eq.$parkingLotId"
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo cargar el layout del estacionamiento: ${response.errorBody()?.string()}")
                )
            }

            val layoutElements = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(layoutElements)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParkingSpots(
        parkingLotId: String
    ): Result<List<ParkingSpot>> {
        return try {
            expireOldReservations()

            val response = apiService.getParkingSpots(
                parkingLotFilter = "eq.$parkingLotId"
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudieron cargar los cajones: ${response.errorBody()?.string()}")
                )
            }

            val spots = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(spots)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveReservation(): Result<Reservation?> {
        return try {
            expireOldReservations()

            val response = apiService.getActiveReservations()

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo cargar la reservación activa: ${response.errorBody()?.string()}")
                )
            }

            val reservation = response.body()
                ?.firstOrNull()
                ?.toDomain()

            Result.success(reservation)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reserveSpot(
        spotId: String
    ): Result<Unit> {
        return try {
            val response = apiService.reserveSpot(
                ReserveSpotRequest(
                    spotId = spotId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo reservar el cajón: ${response.errorBody()?.string()}")
                )
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun occupyReservedSpot(
        spotId: String
    ): Result<Unit> {
        return try {
            val response = apiService.occupyReservedSpot(
                ReserveSpotRequest(
                    spotId = spotId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo ocupar el cajón: ${response.errorBody()?.string()}")
                )
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun releaseActiveReservation(): Result<Unit> {
        return try {
            val response = apiService.releaseActiveReservation()

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo liberar el cajón: ${response.errorBody()?.string()}")
                )
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun adminUpdateParkingSpot(
        spotId: String,
        status: String,
        type: String
    ): Result<ParkingSpot> {
        return try {
            val response = apiService.adminUpdateParkingSpot(
                AdminUpdateParkingSpotRequest(
                    spotId = spotId,
                    status = status,
                    type = type
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo actualizar el cajón: ${response.errorBody()?.string()}")
                )
            }

            val updatedSpot = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el cajón actualizado."))

            Result.success(updatedSpot.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun expireOldReservations(): Result<Unit> {
        return try {
            apiService.expireOldReservations()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ParkingLotDto.toDomain(
        availableSpots: Int,
        totalSpots: Int
    ): ParkingLot {
        return ParkingLot(
            id = id,
            orgId = orgId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            createdAt = createdAt,
            availableSpots = availableSpots,
            totalSpots = totalSpots
        )
    }

    private fun ParkingSpotDto.toDomain(): ParkingSpot {
        return ParkingSpot(
            id = id,
            parkingLotId = parkingLotId,
            spotNumber = spotNumber,
            status = status,
            type = type,
            updatedAt = updatedAt
        )
    }

    private fun ReservationDto.toDomain(): Reservation {
        return Reservation(
            id = id,
            userId = userId,
            spotId = spotId,
            status = status,
            startTime = startTime,
            endTime = endTime,
            createdAt = createdAt,
            expiresAt = expiresAt,
            occupiedAt = occupiedAt
        )
    }
    override suspend fun getParkingFloors(
        parkingLotId: String
    ): Result<List<ParkingFloor>> {
        return try {
            val response = apiService.getParkingFloors(
                parkingLotIdFilter = "eq.$parkingLotId"
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudieron cargar los pisos: ${response.errorBody()?.string()}")
                )
            }

            val floors = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(floors)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun adminCreateParkingSpot(
        parkingLotId: String,
        floorId: String,
        spotNumber: String,
        type: String,
        rowIndex: Int,
        colIndex: Int,
        widthM: Double?,
        heightM: Double?
    ): Result<ParkingSpot> {
        return try {
            val response = apiService.adminCreateParkingSpot(
                AdminCreateParkingSpotRequest(
                    parkingLotId = parkingLotId,
                    floorId = floorId,
                    spotNumber = spotNumber,
                    type = type,
                    rowIndex = rowIndex,
                    colIndex = colIndex,
                    widthM = widthM,
                    heightM = heightM
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo crear el cajón: ${response.errorBody()?.string()}")
                )
            }

            val createdSpot = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el cajón creado."))

            Result.success(createdSpot.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}