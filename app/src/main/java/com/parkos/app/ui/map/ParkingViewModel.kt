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

@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository,
    private val tokenManager: TokenManager
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

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoadingLots.value = true
            _error.value = null

            val role = tokenManager.getUserTypeFlow().first()
            val orgId = tokenManager.getOrgIdFlow().first()
            val fullName = tokenManager.getUserFullNameFlow().first()
            val email = tokenManager.getUserEmailFlow().first()

            _userRole.value = role
            _userOrgId.value = orgId
            _userFullName.value = fullName
            _userEmail.value = email

            loadActiveReservationInternal()

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

    fun selectParkingLot(parkingLot: ParkingLot) {
        _selectedParkingLot.value = parkingLot
        _reservationMessage.value = null
        loadParkingSpots(parkingLot.id)
        loadParkingFloors(parkingLot.id)
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
                loadSelectedParkingLotSpots()
                loadDashboard()
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
                loadDashboard()

            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo crear el cajón."
            }

            _isAdminCreatingSpot.value = false
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
}