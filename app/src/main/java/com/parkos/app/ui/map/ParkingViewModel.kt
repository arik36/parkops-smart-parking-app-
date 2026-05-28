package com.parkos.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkos.app.core.TokenManager
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.domain.repository.ParkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.parkos.app.domain.model.ParkingFloor
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.domain.model.ReservationHistoryItem
import com.parkos.app.domain.model.StaffRequest
import com.parkos.app.domain.model.StaffMember
import com.parkos.app.domain.model.IncidentReport
import com.parkos.app.core.OfflineModeManager


@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository,
    private val tokenManager: TokenManager,
    private val offlineModeManager: OfflineModeManager
) : ViewModel() {

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    private val _userFullName = MutableStateFlow<String?>(null)
    val userFullName = _userFullName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()

    private val _userOrgId = MutableStateFlow<String?>(null)
    val userOrgId = _userOrgId.asStateFlow()

    private val _parkingLots = MutableStateFlow<List<ParkingLot>>(emptyList())
    val parkingLots = _parkingLots.asStateFlow()

    private val _isAdminMovingLayoutElement = MutableStateFlow(false)
    val isAdminMovingLayoutElement = _isAdminMovingLayoutElement.asStateFlow()

    private val _selectedParkingLot = MutableStateFlow<ParkingLot?>(null)
    val selectedParkingLot = _selectedParkingLot.asStateFlow()

    private val _spots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val spots = _spots.asStateFlow()

    private val _activeReservation = MutableStateFlow<Reservation?>(null)
    val activeReservation = _activeReservation.asStateFlow()

    private val _activeReservationSpotNumber = MutableStateFlow<String?>(null)
    val activeReservationSpotNumber = _activeReservationSpotNumber.asStateFlow()

    private val _activeReservationParkingLotName = MutableStateFlow<String?>(null)
    val activeReservationParkingLotName = _activeReservationParkingLotName.asStateFlow()

    private val _isLoadingLots = MutableStateFlow(false)
    val isLoadingLots = _isLoadingLots.asStateFlow()

    private val _isLoadingSpots = MutableStateFlow(false)
    val isLoadingSpots = _isLoadingSpots.asStateFlow()

    private val _isReserving = MutableStateFlow(false)
    val isReserving = _isReserving.asStateFlow()

    private val _isOccupying = MutableStateFlow(false)
    val isOccupying = _isOccupying.asStateFlow()

    private val _isReleasing = MutableStateFlow(false)
    val isReleasing = _isReleasing.asStateFlow()

    private val _reservationMessage = MutableStateFlow<String?>(null)
    val reservationMessage = _reservationMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isAdminUpdatingSpot = MutableStateFlow(false)
    val isAdminUpdatingSpot = _isAdminUpdatingSpot.asStateFlow()

    private val _parkingFloors = MutableStateFlow<List<ParkingFloor>>(emptyList())
    val parkingFloors = _parkingFloors.asStateFlow()

    private val _isLoadingFloors = MutableStateFlow(false)
    val isLoadingFloors = _isLoadingFloors.asStateFlow()

    private val _isAdminCreatingSpot = MutableStateFlow(false)
    val isAdminCreatingSpot = _isAdminCreatingSpot.asStateFlow()

    private val _layoutElements = MutableStateFlow<List<ParkingLayoutElement>>(emptyList())
    val layoutElements = _layoutElements.asStateFlow()

    private val _isLoadingLayout = MutableStateFlow(false)
    val isLoadingLayout = _isLoadingLayout.asStateFlow()

    private val _isAdminDeletingSpot = MutableStateFlow(false)
    val isAdminDeletingSpot = _isAdminDeletingSpot.asStateFlow()

    private val _isAdminCreatingLayoutElement = MutableStateFlow(false)
    val isAdminCreatingLayoutElement = _isAdminCreatingLayoutElement.asStateFlow()

    private val _isAdminDeletingLayoutElement = MutableStateFlow(false)
    val isAdminDeletingLayoutElement = _isAdminDeletingLayoutElement.asStateFlow()

    private val _reservationHistory = MutableStateFlow<List<ReservationHistoryItem>>(emptyList())
    val reservationHistory = _reservationHistory.asStateFlow()

    private val _isLoadingReservationHistory = MutableStateFlow(false)
    val isLoadingReservationHistory = _isLoadingReservationHistory.asStateFlow()

    private val _isUpdatingFullName = MutableStateFlow(false)
    val isUpdatingFullName = _isUpdatingFullName.asStateFlow()

    private val _staffStatus = MutableStateFlow<String?>(null)
    val staffStatus = _staffStatus.asStateFlow()

    private val _pendingStaffRequests = MutableStateFlow<List<StaffRequest>>(emptyList())
    val pendingStaffRequests = _pendingStaffRequests.asStateFlow()

    private val _isLoadingPendingStaffRequests = MutableStateFlow(false)
    val isLoadingPendingStaffRequests = _isLoadingPendingStaffRequests.asStateFlow()

    private val _isResolvingStaffRequest = MutableStateFlow(false)
    val isResolvingStaffRequest = _isResolvingStaffRequest.asStateFlow()

    private val _orgStaffMembers = MutableStateFlow<List<StaffMember>>(emptyList())
    val orgStaffMembers = _orgStaffMembers.asStateFlow()

    private val _isLoadingOrgStaffMembers = MutableStateFlow(false)
    val isLoadingOrgStaffMembers = _isLoadingOrgStaffMembers.asStateFlow()

    private val _isRevokingStaffAccess = MutableStateFlow(false)
    val isRevokingStaffAccess = _isRevokingStaffAccess.asStateFlow()

    private val _incidentReports = MutableStateFlow<List<IncidentReport>>(emptyList())
    val incidentReports = _incidentReports.asStateFlow()

    private val _isLoadingIncidentReports = MutableStateFlow(false)
    val isLoadingIncidentReports = _isLoadingIncidentReports.asStateFlow()

    private val _isCreatingIncidentReport = MutableStateFlow(false)
    val isCreatingIncidentReport = _isCreatingIncidentReport.asStateFlow()

    private val _lastCreatedIncidentReport = MutableStateFlow<IncidentReport?>(null)
    val lastCreatedIncidentReport = _lastCreatedIncidentReport.asStateFlow()

    val isOfflineMode = offlineModeManager.isOfflineMode


    fun loadDashboard() {
        viewModelScope.launch {
            _isLoadingLots.value = true
            _error.value = null

            val role = tokenManager.getUserTypeFlow().first()
            val orgId = tokenManager.getOrgIdFlow().first()
            val fullName = tokenManager.getUserFullNameFlow().first()
            val email = tokenManager.getUserEmailFlow().first()
            val staffStatus = tokenManager.getStaffStatusFlow().first()

            _userRole.value = role
            _userOrgId.value = orgId
            _userFullName.value = fullName
            _userEmail.value = email
            _staffStatus.value = staffStatus

            if (role == "admin") {
                loadPendingStaffRequests()
                loadOrgStaffMembers()
            }

            loadActiveReservationInternal()

            if (role != "admin") {
                loadReservationHistory()
            }

            if (role == "collaborator" && staffStatus == "approved") {
                loadMyIncidentReports()
            }

            val result = parkingRepository.getParkingLots(
                role = role ?: "consumer",
                orgId = orgId
            )

            result.onSuccess { lots ->
                _parkingLots.value = lots

                if (_activeReservation.value != null) {
                    resolveActiveReservationDisplay(lots)
                }

                val shouldAutoSelectParkingLot =
                    role == "admin" || role == "collaborator"

                if (
                    shouldAutoSelectParkingLot &&
                    _selectedParkingLot.value == null &&
                    lots.size == 1
                ) {
                    selectParkingLot(lots.first())
                }
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "Error al cargar estacionamientos"
            }

            _isLoadingLots.value = false
        }
    }

    fun loadReservationHistory() {
        viewModelScope.launch {
            val role = _userRole.value

            if (role == "admin") {
                _reservationHistory.value = emptyList()
                return@launch
            }

            _isLoadingReservationHistory.value = true

            val result = parkingRepository.getMyReservationHistory(
                limit = 8
            )

            result.onSuccess { history ->
                _reservationHistory.value = history
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo cargar tu historial."
            }

            _isLoadingReservationHistory.value = false
        }
    }

    fun updateMyFullName(
        fullName: String
    ) {
        viewModelScope.launch {
            val cleanName = fullName.trim()

            if (cleanName.length < 2) {
                _error.value = "El nombre debe tener al menos 2 caracteres."
                return@launch
            }

            _isUpdatingFullName.value = true
            _error.value = null

            val result = parkingRepository.updateMyFullName(cleanName)

            result.onSuccess { updatedName ->
                _userFullName.value = updatedName
                tokenManager.updateUserFullName(updatedName)
                _reservationMessage.value = "Nombre actualizado correctamente."
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo actualizar tu nombre."
            }

            _isUpdatingFullName.value = false
        }
    }

    fun adminMoveLayoutElement(
        element: ParkingLayoutElement,
        targetFloorId: String,
        targetRowIndex: Int,
        targetColIndex: Int
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden mover elementos del plano."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de mover elementos."
                return@launch
            }

            if (element.parkingSpotId != null) {
                val spot = _spots.value.firstOrNull { it.id == element.parkingSpotId }

                if (spot == null) {
                    _error.value = "No se encontró el cajón asociado a este elemento."
                    return@launch
                }

                if (!spot.status.equals("maintenance", ignoreCase = true)) {
                    _error.value = "Solo puedes mover cajones en mantenimiento."
                    return@launch
                }
            }

            _isAdminMovingLayoutElement.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminMoveLayoutElement(
                elementId = element.id,
                targetFloorId = targetFloorId,
                targetRowIndex = targetRowIndex,
                targetColIndex = targetColIndex
            )

            result.onSuccess {
                _reservationMessage.value = "Elemento movido correctamente."

                loadParkingLayoutElements(parkingLot.id)
                loadSelectedParkingLotSpots()
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo mover el elemento."
            }

            _isAdminMovingLayoutElement.value = false
        }
    }

    fun selectParkingLot(parkingLot: ParkingLot) {
        _selectedParkingLot.value = parkingLot
        _reservationMessage.value = null

        loadParkingSpots(parkingLot.id)
        loadParkingFloors(parkingLot.id)
        loadParkingLayoutElements(parkingLot.id)
    }

    fun loadSelectedParkingLotSpots() {
        val lot = _selectedParkingLot.value ?: return
        loadParkingSpots(lot.id)
    }

    fun loadParkingFloors(parkingLotId: String) {
        viewModelScope.launch {
            _isLoadingFloors.value = true

            val result = parkingRepository.getParkingFloors(parkingLotId)

            result.onSuccess { floors ->
                _parkingFloors.value = floors
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudieron cargar los pisos."
            }

            _isLoadingFloors.value = false
        }
    }

    private fun loadParkingSpots(parkingLotId: String) {
        viewModelScope.launch {
            _isLoadingSpots.value = true
            _error.value = null

            val result = parkingRepository.getParkingSpots(parkingLotId)

            result.onSuccess { loadedSpots ->
                _spots.value = loadedSpots
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "Error al cargar cajones"
            }

            _isLoadingSpots.value = false
        }
    }
    fun loadParkingLayoutElements(parkingLotId: String) {
        viewModelScope.launch {
            _isLoadingLayout.value = true

            val result = parkingRepository.getParkingLayoutElements(parkingLotId)

            result.onSuccess { elements ->
                _layoutElements.value = elements
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo cargar el layout del estacionamiento."
            }

            _isLoadingLayout.value = false
        }
    }

    fun reserveSpot(spot: ParkingSpot) {
        viewModelScope.launch {
            if (!spot.status.equals("available", ignoreCase = true)) {
                _error.value = "Este cajón no está disponible."
                return@launch
            }

            if (_userRole.value == "admin") {
                _error.value = "Los administradores no pueden reservar cajones."
                return@launch
            }
            if (_userRole.value == "consumer" && spot.type.equals("staff", ignoreCase = true)) {
                _error.value = "Este cajón es exclusivo para colaboradores."
                return@launch
            }

            if (_userRole.value == "collaborator" && _staffStatus.value != "approved") {
                _error.value = "Tu acceso como colaborador está pendiente de aprobación."
                return@launch
            }

            if (_userRole.value == "collaborator" && !spot.type.equals("staff", ignoreCase = true)) {
                _error.value = "Los colaboradores solo pueden reservar cajones staff."
                return@launch
            }

            if (spot.type == "staff" && _userRole.value != "collaborator") {
                _error.value = "Este cajón es exclusivo para colaboradores."
                return@launch
            }

            if (_activeReservation.value != null) {
                _error.value = "Ya tienes una reservación activa. Termínala antes de reservar otro cajón."
                return@launch
            }

            _isReserving.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.reserveSpot(spot.id)

            result.onSuccess {
                _reservationMessage.value = "Cajón ${spot.spotNumber} reservado por 5 minutos."

                loadActiveReservationInternal()
                _activeReservationSpotNumber.value = spot.spotNumber
                _activeReservationParkingLotName.value = _selectedParkingLot.value?.name

                loadSelectedParkingLotSpots()
                loadDashboard()
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo reservar el cajón"
            }

            _isReserving.value = false
        }
    }

    fun occupyReservedSpot(spotId: String) {
        viewModelScope.launch {
            _isOccupying.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.occupyReservedSpot(spotId)

            result.onSuccess {
                _reservationMessage.value = "Cajón ocupado correctamente."
                loadActiveReservationInternal()
                loadSelectedParkingLotSpots()
                loadDashboard()
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo ocupar el cajón"
                loadActiveReservationInternal()
                loadSelectedParkingLotSpots()
                loadDashboard()
            }

            _isOccupying.value = false
        }
    }

    fun releaseActiveReservation() {
        viewModelScope.launch {
            if (_activeReservation.value?.status != "active") {
                _error.value = "No tienes un cajón ocupado."
                return@launch
            }

            _isReleasing.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.releaseActiveReservation()

            result.onSuccess {
                _reservationMessage.value = "Cajón liberado correctamente."
                _activeReservation.value = null
                _activeReservationSpotNumber.value = null
                _activeReservationParkingLotName.value = null

                loadSelectedParkingLotSpots()
                loadDashboard()
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo liberar el cajón"
            }

            _isReleasing.value = false
        }
    }

    fun refreshAfterReservationExpiration() {
        viewModelScope.launch {
            parkingRepository.expireOldReservations()
            loadActiveReservationInternal()
            loadSelectedParkingLotSpots()
            loadDashboard()
        }
    }
    fun adminUpdateParkingSpot(
        spot: ParkingSpot,
        newStatus: String,
        newType: String
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden editar cajones."
                return@launch
            }

            _isAdminUpdatingSpot.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminUpdateParkingSpot(
                spotId = spot.id,
                status = newStatus,
                type = newType
            )

            result.onSuccess {
                _reservationMessage.value = "Cajón ${spot.spotNumber} actualizado correctamente."

                val parkingLot = _selectedParkingLot.value

                loadSelectedParkingLotSpots()
                loadDashboard()

                if (parkingLot != null) {
                    loadParkingLayoutElements(parkingLot.id)
                }

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo actualizar el cajón."
            }

            _isAdminUpdatingSpot.value = false
        }
    }
    fun adminCreateParkingSpot(
        floorId: String,
        spotNumber: String,
        type: String,
        rowIndex: Int,
        colIndex: Int,
        widthM: Double?,
        heightM: Double?
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden crear cajones."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de crear cajones."
                return@launch
            }

            if (spotNumber.isBlank()) {
                _error.value = "El identificador del cajón es obligatorio."
                return@launch
            }

            _isAdminCreatingSpot.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminCreateParkingSpot(
                parkingLotId = parkingLot.id,
                floorId = floorId,
                spotNumber = spotNumber,
                type = type,
                rowIndex = rowIndex,
                colIndex = colIndex,
                widthM = widthM,
                heightM = heightM
            )

            result.onSuccess { createdSpot ->
                _reservationMessage.value =
                    "Cajón ${createdSpot.spotNumber} creado en mantenimiento."

                loadSelectedParkingLotSpots()
                loadParkingFloors(parkingLot.id)
                loadParkingLayoutElements(parkingLot.id)
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo crear el cajón."
            }

            _isAdminCreatingSpot.value = false
        }
    }
    fun adminDeleteParkingSpot(
        spot: ParkingSpot
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden eliminar cajones."
                return@launch
            }

            if (!spot.status.equals("maintenance", ignoreCase = true)) {
                _error.value = "Solo puedes eliminar cajones en mantenimiento."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de eliminar cajones."
                return@launch
            }

            _isAdminDeletingSpot.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminDeleteParkingSpot(
                spotId = spot.id
            )

            result.onSuccess { deletedSpot ->
                _reservationMessage.value =
                    "Cajón ${deletedSpot.spotNumber} eliminado correctamente."

                loadSelectedParkingLotSpots()
                loadParkingFloors(parkingLot.id)
                loadParkingLayoutElements(parkingLot.id)
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo eliminar el cajón."
            }

            _isAdminDeletingSpot.value = false
        }
    }
    fun adminCreateLayoutElement(
        floorId: String,
        elementType: String,
        rowIndex: Int,
        colIndex: Int,
        label: String?,
        description: String?
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden agregar elementos al plano."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de agregar elementos."
                return@launch
            }

            val allowedTypes = setOf(
                "wall",
                "pillar",
                "barrier",
                "cabin",
                "entrance",
                "stairs",
                "reserved_area"
            )

            if (elementType !in allowedTypes) {
                _error.value = "Tipo de elemento inválido."
                return@launch
            }

            _isAdminCreatingLayoutElement.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminCreateLayoutElement(
                parkingLotId = parkingLot.id,
                floorId = floorId,
                elementType = elementType,
                rowIndex = rowIndex,
                colIndex = colIndex,
                label = label?.takeIf { it.isNotBlank() },
                description = description?.takeIf { it.isNotBlank() }
            )

            result.onSuccess {
                _reservationMessage.value = "Elemento agregado al plano correctamente."

                loadParkingLayoutElements(parkingLot.id)
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo agregar el elemento al plano."
            }

            _isAdminCreatingLayoutElement.value = false
        }
    }

    fun adminDeleteLayoutElement(
        element: ParkingLayoutElement
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden eliminar elementos del plano."
                return@launch
            }

            if (element.parkingSpotId != null) {
                _error.value = "Este elemento está asociado a un cajón. Usa eliminar cajón."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de eliminar elementos."
                return@launch
            }

            _isAdminDeletingLayoutElement.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminDeleteLayoutElement(
                elementId = element.id
            )

            result.onSuccess {
                _reservationMessage.value = "Elemento eliminado del plano correctamente."

                loadParkingLayoutElements(parkingLot.id)
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo eliminar el elemento del plano."
            }

            _isAdminDeletingLayoutElement.value = false
        }
    }

    private suspend fun loadActiveReservationInternal() {
        val result = parkingRepository.getActiveReservation()

        result.onSuccess { reservation ->
            _activeReservation.value = reservation

            if (reservation == null) {
                _activeReservationSpotNumber.value = null
                _activeReservationParkingLotName.value = null
            }
        }.onFailure {
            _activeReservation.value = null
            _activeReservationSpotNumber.value = null
            _activeReservationParkingLotName.value = null
        }
    }

    private suspend fun resolveActiveReservationDisplay(lots: List<ParkingLot>) {
        val reservation = _activeReservation.value ?: return

        for (lot in lots) {
            val spotsResult = parkingRepository.getParkingSpots(lot.id)
            val lotSpots = spotsResult.getOrNull().orEmpty()
            val spot = lotSpots.firstOrNull { it.id == reservation.spotId }

            if (spot != null) {
                _activeReservationSpotNumber.value = spot.spotNumber
                _activeReservationParkingLotName.value = lot.name

                if (_selectedParkingLot.value == null) {
                    _selectedParkingLot.value = lot
                    _spots.value = lotSpots
                }

                return
            }
        }

        _activeReservationSpotNumber.value = null
        _activeReservationParkingLotName.value = null
    }

    fun clearSelectedParkingLot() {
        _selectedParkingLot.value = null
        _spots.value = emptyList()
    }

    fun clearReservationMessage() {
        _reservationMessage.value = null
    }
    fun loadPendingStaffRequests() {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _pendingStaffRequests.value = emptyList()
                return@launch
            }

            _isLoadingPendingStaffRequests.value = true

            val result = parkingRepository.adminGetPendingStaffRequests()

            result.onSuccess { requests ->
                _pendingStaffRequests.value = requests
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudieron cargar las solicitudes staff."
            }

            _isLoadingPendingStaffRequests.value = false
        }
    }

    fun resolveStaffRequest(
        request: StaffRequest,
        action: String
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden resolver solicitudes staff."
                return@launch
            }

            if (action !in listOf("approve", "reject")) {
                _error.value = "Acción inválida."
                return@launch
            }

            _isResolvingStaffRequest.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminResolveStaffRequest(
                userId = request.userId,
                action = action
            )

            result.onSuccess {
                _reservationMessage.value = if (action == "approve") {
                    "${request.fullName} fue aprobado como colaborador."
                } else {
                    "${request.fullName} fue rechazado como colaborador."
                }

                loadPendingStaffRequests()
                loadOrgStaffMembers()
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo resolver la solicitud staff."
            }

            _isResolvingStaffRequest.value = false
        }
    }
    fun loadOrgStaffMembers() {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _orgStaffMembers.value = emptyList()
                return@launch
            }

            _isLoadingOrgStaffMembers.value = true

            val result = parkingRepository.adminGetOrgStaffMembers()

            result.onSuccess { members ->
                _orgStaffMembers.value = members
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo cargar el personal staff."
            }

            _isLoadingOrgStaffMembers.value = false
        }
    }

    fun revokeStaffAccess(
        member: StaffMember
    ) {
        viewModelScope.launch {
            if (_userRole.value != "admin") {
                _error.value = "Solo administradores pueden quitar acceso staff."
                return@launch
            }

            _isRevokingStaffAccess.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.adminRevokeStaffAccess(
                userId = member.userId
            )

            result.onSuccess {
                _reservationMessage.value =
                    "${member.fullName} ya no tiene acceso staff."

                loadOrgStaffMembers()
                loadPendingStaffRequests()
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo quitar el acceso staff."
            }

            _isRevokingStaffAccess.value = false
        }
    }
    fun loadMyIncidentReports() {
        viewModelScope.launch {
            val isApprovedStaff =
                _userRole.value == "collaborator" && _staffStatus.value == "approved"

            if (!isApprovedStaff) {
                _incidentReports.value = emptyList()
                return@launch
            }

            _isLoadingIncidentReports.value = true

            val result = parkingRepository.staffGetMyIncidentReports(
                limit = 10
            )

            result.onSuccess { reports ->
                _incidentReports.value = reports
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudieron cargar tus reportes."
            }

            _isLoadingIncidentReports.value = false
        }
    }

    fun staffCreateIncidentReport(
        spotNumber: String?,
        vehiclePlate: String,
        incidentType: String,
        customIncidentType: String?,
        details: String?
    ) {
        viewModelScope.launch {
            val isApprovedStaff =
                _userRole.value == "collaborator" && _staffStatus.value == "approved"

            if (!isApprovedStaff) {
                _error.value = "Tu acceso staff debe estar aprobado para crear reportes."
                return@launch
            }

            val parkingLot = _selectedParkingLot.value

            if (parkingLot == null) {
                _error.value = "Selecciona un estacionamiento antes de crear un reporte."
                return@launch
            }

            val cleanPlate = vehiclePlate.trim().uppercase()

            if (cleanPlate.length < 3) {
                _error.value = "La placa debe tener al menos 3 caracteres."
                return@launch
            }

            if (incidentType == "otro" && customIncidentType.isNullOrBlank()) {
                _error.value = "Describe el incidente cuando selecciones Otro."
                return@launch
            }

            _isCreatingIncidentReport.value = true
            _error.value = null
            _reservationMessage.value = null
            _lastCreatedIncidentReport.value = null

            val result = parkingRepository.staffCreateIncidentReport(
                parkingLotId = parkingLot.id,
                spotNumber = spotNumber,
                vehiclePlate = cleanPlate,
                incidentType = incidentType,
                customIncidentType = customIncidentType,
                details = details
            )

            result.onSuccess { report ->
                _lastCreatedIncidentReport.value = report
                _reservationMessage.value = "Reporte ${report.reportNumber} creado correctamente."
                loadMyIncidentReports()
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo crear el reporte."
            }

            _isCreatingIncidentReport.value = false
        }
    }

    fun clearLastCreatedIncidentReport() {
        _lastCreatedIncidentReport.value = null
    }
}