package com.parkos.app.data.repository

import com.parkos.app.data.remote.ApiService
import com.parkos.app.data.remote.dto.ParkingLotDto
import com.parkos.app.data.remote.dto.ParkingSpotDto
import com.parkos.app.data.remote.dto.ReserveSpotRequest
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.repository.ParkingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParkingRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ParkingRepository {

    override suspend fun getParkingLots(
        role: String,
        orgId: String?
    ): Result<List<ParkingLot>> {
        return try {
            val response = if (role == "admin" || role == "collaborator") {
                if (orgId.isNullOrBlank()) {
                    return Result.failure(
                        Exception("Este usuario no tiene organización asignada.")
                    )
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

    override suspend fun getParkingSpots(
        parkingLotId: String
    ): Result<List<ParkingSpot>> {
        return try {
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
}