package com.parkos.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkos.app.core.TokenManager
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.repository.ParkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    private val _userOrgId = MutableStateFlow<String?>(null)
    val userOrgId = _userOrgId.asStateFlow()

    private val _parkingLots = MutableStateFlow<List<ParkingLot>>(emptyList())
    val parkingLots = _parkingLots.asStateFlow()

    private val _selectedParkingLot = MutableStateFlow<ParkingLot?>(null)
    val selectedParkingLot = _selectedParkingLot.asStateFlow()

    private val _spots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val spots = _spots.asStateFlow()

    private val _isLoadingLots = MutableStateFlow(false)
    val isLoadingLots = _isLoadingLots.asStateFlow()

    private val _isLoadingSpots = MutableStateFlow(false)
    val isLoadingSpots = _isLoadingSpots.asStateFlow()

    private val _isReserving = MutableStateFlow(false)
    val isReserving = _isReserving.asStateFlow()

    private val _reservationMessage = MutableStateFlow<String?>(null)
    val reservationMessage = _reservationMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoadingLots.value = true
            _error.value = null

            val role = tokenManager.getUserTypeFlow().first()
            val orgId = tokenManager.getOrgIdFlow().first()

            _userRole.value = role
            _userOrgId.value = orgId

            val result = parkingRepository.getParkingLots(
                role = role ?: "consumer",
                orgId = orgId
            )

            result.onSuccess { lots ->
                _parkingLots.value = lots

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
    }

    fun loadSelectedParkingLotSpots() {
        val lot = _selectedParkingLot.value ?: return
        loadParkingSpots(lot.id)
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

            _isReserving.value = true
            _error.value = null
            _reservationMessage.value = null

            val result = parkingRepository.reserveSpot(spot.id)

            result.onSuccess {
                _reservationMessage.value = "Cajón ${spot.spotNumber} reservado correctamente."
                loadSelectedParkingLotSpots()
                loadDashboard()
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo reservar el cajón"
            }

            _isReserving.value = false
        }
    }

    fun clearSelectedParkingLot() {
        _selectedParkingLot.value = null
        _spots.value = emptyList()
    }

    fun clearReservationMessage() {
        _reservationMessage.value = null
    }
}