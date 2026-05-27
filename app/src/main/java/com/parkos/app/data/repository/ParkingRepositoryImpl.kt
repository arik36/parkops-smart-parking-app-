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
import com.parkos.app.data.remote.dto.AdminDeleteParkingSpotRequest
import com.parkos.app.data.remote.dto.AdminCreateLayoutElementRequest
import com.parkos.app.data.remote.dto.AdminDeleteLayoutElementRequest
import com.parkos.app.data.remote.dto.AdminMoveLayoutElementRequest
import com.parkos.app.data.remote.dto.GetReservationHistoryRequest
import com.parkos.app.domain.model.ReservationHistoryItem
import com.parkos.app.data.remote.dto.UpdateFullNameRequest
import com.parkos.app.data.remote.dto.ResolveStaffRequestRequest
import com.parkos.app.domain.model.StaffRequest
import com.parkos.app.data.remote.dto.RevokeStaffAccessRequest
import com.parkos.app.domain.model.StaffMember
import com.parkos.app.data.remote.dto.CreateIncidentReportRequest
import com.parkos.app.data.remote.dto.GetIncidentReportsRequest
import com.parkos.app.domain.model.IncidentReport



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
    override suspend fun adminMoveLayoutElement(
        elementId: String,
        targetFloorId: String,
        targetRowIndex: Int,
        targetColIndex: Int
    ): Result<ParkingLayoutElement> {
        return try {
            val response = apiService.adminMoveLayoutElement(
                AdminMoveLayoutElementRequest(
                    elementId = elementId,
                    targetFloorId = targetFloorId,
                    targetRowIndex = targetRowIndex,
                    targetColIndex = targetColIndex
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo mover el elemento del plano: ${response.errorBody()?.string()}")
                )
            }

            val movedElement = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el elemento movido."))

            Result.success(movedElement.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMyFullName(
        fullName: String
    ): Result<String> {
        return try {
            val response = apiService.updateMyFullName(
                UpdateFullNameRequest(
                    fullName = fullName.trim()
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo actualizar tu nombre.")
                )
            }

            val updatedUser = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el usuario actualizado."))

            Result.success(updatedUser.fullName)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getMyReservationHistory(
        limit: Int
    ): Result<List<ReservationHistoryItem>> {
        return try {
            val response = apiService.getMyReservationHistory(
                GetReservationHistoryRequest(
                    limit = limit
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudo cargar tu historial."
                        )
                    )
                )
            }

            val history = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(history)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun adminDeleteParkingSpot(
        spotId: String
    ): Result<ParkingSpot> {
        return try {
            val response = apiService.adminDeleteParkingSpot(
                AdminDeleteParkingSpotRequest(
                    spotId = spotId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo eliminar el cajón: ${response.errorBody()?.string()}")
                )
            }

            val deletedSpot = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el cajón eliminado."))

            Result.success(deletedSpot.toDomain())

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
                val rawError = response.errorBody()?.string()

                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = rawError,
                            fallback = "No se pudo crear el cajón."
                        )
                    )
                )
            }

            val createdSpot = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el cajón creado."))

            Result.success(createdSpot.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun adminCreateLayoutElement(
        parkingLotId: String,
        floorId: String,
        elementType: String,
        rowIndex: Int,
        colIndex: Int,
        label: String?,
        description: String?
    ): Result<ParkingLayoutElement> {
        return try {
            val response = apiService.adminCreateLayoutElement(
                AdminCreateLayoutElementRequest(
                    parkingLotId = parkingLotId,
                    floorId = floorId,
                    elementType = elementType,
                    rowIndex = rowIndex,
                    colIndex = colIndex,
                    label = label,
                    description = description
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo crear el elemento del plano: ${response.errorBody()?.string()}")
                )
            }

            val createdElement = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el elemento creado."))

            Result.success(createdElement.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun adminDeleteLayoutElement(
        elementId: String
    ): Result<ParkingLayoutElement> {
        return try {
            val response = apiService.adminDeleteLayoutElement(
                AdminDeleteLayoutElementRequest(
                    elementId = elementId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("No se pudo eliminar el elemento del plano: ${response.errorBody()?.string()}")
                )
            }

            val deletedElement = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el elemento eliminado."))

            Result.success(deletedElement.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun friendlyParkingError(
        rawError: String?,
        fallback: String
    ): String {
        val cleanMessage = extractRemoteMessage(rawError)
        val searchableText = "${rawError.orEmpty()} ${cleanMessage.orEmpty()}".lowercase()

        return when {
            "duplicate" in searchableText ||
                    "23505" in searchableText ||
                    "unique" in searchableText ||
                    "spot_number" in searchableText ||
                    "ya existe" in searchableText ||
                    "identificador" in searchableText -> {
                "Ya existe un cajón con ese identificador. Usa otro, por ejemplo A-06."
            }

            "celda" in searchableText && "ocupada" in searchableText -> {
                "Esa celda ya está ocupada. Selecciona otra posición en el plano."
            }

            "fila" in searchableText && "rango" in searchableText -> {
                "La fila seleccionada está fuera del rango permitido."
            }

            "columna" in searchableText && "rango" in searchableText -> {
                "La columna seleccionada está fuera del rango permitido."
            }

            "solo administradores" in searchableText -> {
                "Solo los administradores pueden realizar esta acción."
            }

            !cleanMessage.isNullOrBlank() -> {
                cleanMessage
            }

            else -> {
                fallback
            }
        }
    }
    private fun friendlyIncidentReportError(
        rawError: String?,
        fallback: String
    ): String {
        val cleanMessage = extractRemoteMessage(rawError)
        val searchableText = "${rawError.orEmpty()} ${cleanMessage.orEmpty()}".lowercase()

        return when {
            "casilla indicada no existe" in searchableText ||
                    "no existe en este estacionamiento" in searchableText -> {
                "La casilla indicada no existe en este estacionamiento. Revisa el identificador, por ejemplo A-03."
            }

            "placa debe tener" in searchableText -> {
                "La placa debe tener al menos 3 caracteres."
            }

            "placa es demasiado larga" in searchableText -> {
                "La placa es demasiado larga."
            }

            "tipo de incidente inválido" in searchableText -> {
                "Selecciona un tipo de incidente válido."
            }

            "describe el tipo de incidente" in searchableText -> {
                "Describe el incidente cuando selecciones Otro."
            }

            "detalles son demasiado largos" in searchableText -> {
                "Los detalles son demasiado largos. Intenta resumir el reporte."
            }

            "solo personal staff" in searchableText -> {
                "Solo personal staff aprobado puede crear reportes."
            }

            "acceso staff" in searchableText && "aprobado" in searchableText -> {
                "Tu acceso staff aún no está aprobado."
            }

            "estacionamiento no pertenece" in searchableText -> {
                "No puedes crear reportes para este estacionamiento."
            }

            !cleanMessage.isNullOrBlank() -> {
                cleanMessage
            }

            else -> {
                fallback
            }
        }
    }

    private fun extractRemoteMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) {
            return null
        }

        val messageRegex = Regex(
            pattern = "\"(?:message|msg|details|hint)\"\\s*:\\s*\"([^\"]+)\"",
            option = RegexOption.IGNORE_CASE
        )

        return messageRegex
            .find(rawError)
            ?.groupValues
            ?.getOrNull(1)
            ?.replace("\\n", " ")
            ?.replace("\\\"", "\"")
            ?.trim()
    }
    override suspend fun adminGetPendingStaffRequests(): Result<List<StaffRequest>> {
        return try {
            val response = apiService.adminGetPendingStaffRequests()

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudieron cargar las solicitudes staff."
                        )
                    )
                )
            }

            val requests = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(requests)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun adminResolveStaffRequest(
        userId: String,
        action: String
    ): Result<Unit> {
        return try {
            val response = apiService.adminResolveStaffRequest(
                ResolveStaffRequestRequest(
                    userId = userId,
                    action = action
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudo resolver la solicitud staff."
                        )
                    )
                )
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun adminGetOrgStaffMembers(): Result<List<StaffMember>> {
        return try {
            val response = apiService.adminGetOrgStaffMembers()

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudo cargar el personal staff."
                        )
                    )
                )
            }

            val staffMembers = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(staffMembers)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun adminRevokeStaffAccess(
        userId: String
    ): Result<Unit> {
        return try {
            val response = apiService.adminRevokeStaffAccess(
                RevokeStaffAccessRequest(
                    userId = userId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudo quitar el acceso staff."
                        )
                    )
                )
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun staffCreateIncidentReport(
        parkingLotId: String,
        spotNumber: String?,
        vehiclePlate: String,
        incidentType: String,
        customIncidentType: String?,
        details: String?
    ): Result<IncidentReport> {
        return try {
            val response = apiService.staffCreateIncidentReport(
                CreateIncidentReportRequest(
                    parkingLotId = parkingLotId,
                    spotNumber = spotNumber.orEmpty().trim().uppercase(),
                    vehiclePlate = vehiclePlate.trim().uppercase(),
                    incidentType = incidentType,
                    customIncidentType = customIncidentType.orEmpty().trim(),
                    details = details.orEmpty().trim()
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyIncidentReportError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudo crear el reporte."
                        )
                    )
                )
            }

            val report = response.body()
                ?: return Result.failure(Exception("Supabase no devolvió el reporte creado."))

            Result.success(report.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun staffGetMyIncidentReports(
        limit: Int
    ): Result<List<IncidentReport>> {
        return try {
            val response = apiService.staffGetMyIncidentReports(
                GetIncidentReportsRequest(
                    limit = limit
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(
                        friendlyParkingError(
                            rawError = response.errorBody()?.string(),
                            fallback = "No se pudieron cargar tus reportes."
                        )
                    )
                )
            }

            val reports = response.body()
                ?.map { it.toDomain() }
                ?: emptyList()

            Result.success(reports)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}