package com.parkos.app.ui.map


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.ui.theme.ParkosOrange


@Composable
fun MapScreen(
    viewModel: ParkingViewModel,
    onLogout: () -> Unit
) {
    val userRole by viewModel.userRole.collectAsState()
    val userFullName by viewModel.userFullName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val parkingLots by viewModel.parkingLots.collectAsState()
    val selectedParkingLot by viewModel.selectedParkingLot.collectAsState()
    val spots by viewModel.spots.collectAsState()
    val activeReservation by viewModel.activeReservation.collectAsState()
    val parkingFloors by viewModel.parkingFloors.collectAsState()
    val layoutElements by viewModel.layoutElements.collectAsState()
    val isLoadingLayout by viewModel.isLoadingLayout.collectAsState()
    val isLoadingFloors by viewModel.isLoadingFloors.collectAsState()
    val isAdminCreatingSpot by viewModel.isAdminCreatingSpot.collectAsState()
    val activeReservationSpotNumber by viewModel.activeReservationSpotNumber.collectAsState()
    val activeReservationParkingLotName by viewModel.activeReservationParkingLotName.collectAsState()
    val isLoadingLots by viewModel.isLoadingLots.collectAsState()
    val isLoadingSpots by viewModel.isLoadingSpots.collectAsState()
    val isReserving by viewModel.isReserving.collectAsState()
    val isOccupying by viewModel.isOccupying.collectAsState()
    val isReleasing by viewModel.isReleasing.collectAsState()
    val isAdminUpdatingSpot by viewModel.isAdminUpdatingSpot.collectAsState()
    val isAdminDeletingSpot by viewModel.isAdminDeletingSpot.collectAsState()
    val reservationMessage by viewModel.reservationMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var spotToReserve by remember { mutableStateOf<ParkingSpot?>(null) }
    var showOccupyDialog by remember { mutableStateOf(false) }
    var showReleaseDialog by remember { mutableStateOf(false) }
    var spotToEditByAdmin by remember { mutableStateOf<ParkingSpot?>(null) }
    var showAdminCreateSpotDialog by remember { mutableStateOf(false) }
    var adminCreateSpotTarget by remember { mutableStateOf<AdminCreateSpotTarget?>(null) }


    val tabs = listOf(
        BottomTab("Perfil", Icons.Default.Person),
        BottomTab("Inicio", Icons.Default.Home),
        BottomTab("Mapa", Icons.Default.LocationOn),
        BottomTab("Avisos", Icons.Default.Notifications)
    )

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    spotToReserve?.let { spot ->
        AlertDialog(
            onDismissRequest = {
                if (!isReserving) {
                    spotToReserve = null
                }
            },
            title = {
                Text("Reservar cajón")
            },
            text = {
                Text("¿Quieres reservar el cajón ${spot.spotNumber} por 5 minutos?")
            },
            confirmButton = {
                Button(
                    enabled = !isReserving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    ),
                    onClick = {
                        viewModel.reserveSpot(spot)
                        spotToReserve = null
                    }
                ) {
                    Text(if (isReserving) "Reservando..." else "Reservar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isReserving,
                    onClick = {
                        spotToReserve = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showAdminCreateSpotDialog) {
        AdminCreateParkingSpotDialog(
            floors = parkingFloors,
            initialTarget = adminCreateSpotTarget,
            isSaving = isAdminCreatingSpot,
            onDismiss = {
                if (!isAdminCreatingSpot) {
                    showAdminCreateSpotDialog = false
                    adminCreateSpotTarget = null
                }
            },
            onCreate = { floorId, spotNumber, type, rowIndex, colIndex, widthM, heightM ->
                viewModel.adminCreateParkingSpot(
                    floorId = floorId,
                    spotNumber = spotNumber,
                    type = type,
                    rowIndex = rowIndex,
                    colIndex = colIndex,
                    widthM = widthM,
                    heightM = heightM
                )
                showAdminCreateSpotDialog = false
                adminCreateSpotTarget = null
            }
        )
    }

    if (showOccupyDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isOccupying) {
                    showOccupyDialog = false
                }
            },
            title = {
                Text("Ya llegué")
            },
            text = {
                Text("¿Confirmas que ya estás en el cajón reservado?")
            },
            confirmButton = {
                Button(
                    enabled = !isOccupying && activeReservation != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    ),
                    onClick = {
                        activeReservation?.let { reservation ->
                            viewModel.occupyReservedSpot(reservation.spotId)
                        }
                        showOccupyDialog = false
                    }
                ) {
                    Text(if (isOccupying) "Confirmando..." else "Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isOccupying,
                    onClick = {
                        showOccupyDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showReleaseDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isReleasing) {
                    showReleaseDialog = false
                }
            },
            title = {
                Text("Estoy saliendo")
            },
            text = {
                Text("¿Quieres liberar tu cajón y terminar tu uso del estacionamiento?")
            },
            confirmButton = {
                Button(
                    enabled = !isReleasing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    ),
                    onClick = {
                        viewModel.releaseActiveReservation()
                        showReleaseDialog = false
                    }
                ) {
                    Text(if (isReleasing) "Liberando..." else "Liberar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isReleasing,
                    onClick = {
                        showReleaseDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    spotToEditByAdmin?.let { spot ->
        AdminEditParkingSpotDialog(
            spot = spot,
            isSaving = isAdminUpdatingSpot,
            isDeleting = isAdminDeletingSpot,
            onDismiss = {
                if (!isAdminUpdatingSpot && !isAdminDeletingSpot) {
                    spotToEditByAdmin = null
                }
            },
            onSave = { newStatus, newType ->
                viewModel.adminUpdateParkingSpot(
                    spot = spot,
                    newStatus = newStatus,
                    newType = newType
                )
                spotToEditByAdmin = null
            },
            onDelete = {
                viewModel.adminDeleteParkingSpot(spot)
                spotToEditByAdmin = null
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = ParkosBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(tab.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ParkosOrange,
                            selectedTextColor = ParkosOrange,
                            indicatorColor = ParkosSoftOrange,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> ProfileTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                role = userRole,
                selectedParkingLot = selectedParkingLot,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                isOccupying = isOccupying,
                isReleasing = isReleasing,
                onOccupyClick = { showOccupyDialog = true },
                onReleaseClick = { showReleaseDialog = true },
                onReservationExpired = {
                    viewModel.refreshAfterReservationExpiration()
                },
                onLogout = onLogout
            )

            1 -> HomeTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                role = userRole,
                userFullName = userFullName,
                userEmail = userEmail,
                parkingLots = parkingLots,
                isLoading = isLoadingLots,
                error = error,
                onRetry = { viewModel.loadDashboard() },
                onSelectParkingLot = { lot ->
                    viewModel.selectParkingLot(lot)
                    selectedTab = 2
                }
            )

            2 -> MapTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                role = userRole,
                selectedParkingLot = selectedParkingLot,
                spots = spots,
                parkingFloors = parkingFloors,
                layoutElements = layoutElements,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                isLoading = isLoadingSpots,
                isLoadingFloors = isLoadingFloors,
                isLoadingLayout = isLoadingLayout,
                isReserving = isReserving,
                isOccupying = isOccupying,
                isReleasing = isReleasing,
                isAdminUpdatingSpot = isAdminUpdatingSpot,
                isAdminCreatingSpot = isAdminCreatingSpot,
                reservationMessage = reservationMessage,
                error = error,
                onRetry = { viewModel.loadSelectedParkingLotSpots() },
                onGoToParkingLots = { selectedTab = 1 },
                onReserveSpotClick = { spot ->
                    spotToReserve = spot
                },
                onAdminEditSpotClick = { spot ->
                    spotToEditByAdmin = spot
                },
                onAdminCreateSpotClick = {
                    adminCreateSpotTarget = null
                    showAdminCreateSpotDialog = true
                },
                onAdminCreateSpotAtCell = { floorId, rowIndex, colIndex ->
                    adminCreateSpotTarget = AdminCreateSpotTarget(
                        floorId = floorId,
                        rowIndex = rowIndex,
                        colIndex = colIndex
                    )
                    showAdminCreateSpotDialog = true
                },
                onOccupyClick = {
                    showOccupyDialog = true
                },
                onReleaseClick = {
                    showReleaseDialog = true
                },
                onReservationExpired = {
                    viewModel.refreshAfterReservationExpiration()
                },
                onClearReservationMessage = {
                    viewModel.clearReservationMessage()
                }
            )

            3 -> NotificationsTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}
