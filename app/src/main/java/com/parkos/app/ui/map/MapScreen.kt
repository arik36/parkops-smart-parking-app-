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
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.ui.theme.ParkosOrange
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext


@Composable
fun MapScreen(
    viewModel: ParkingViewModel,
    onLogout: () -> Unit
) {
    val userRole by viewModel.userRole.collectAsState()
    val staffStatus by viewModel.staffStatus.collectAsState()
    val userFullName by viewModel.userFullName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    val parkingLots by viewModel.parkingLots.collectAsState()
    val selectedParkingLot by viewModel.selectedParkingLot.collectAsState()
    val spots by viewModel.spots.collectAsState()

    val activeReservation by viewModel.activeReservation.collectAsState()
    val activeReservationSpotNumber by viewModel.activeReservationSpotNumber.collectAsState()
    val activeReservationParkingLotName by viewModel.activeReservationParkingLotName.collectAsState()

    val parkingFloors by viewModel.parkingFloors.collectAsState()
    val layoutElements by viewModel.layoutElements.collectAsState()

    val isLoadingLots by viewModel.isLoadingLots.collectAsState()
    val isLoadingSpots by viewModel.isLoadingSpots.collectAsState()
    val isLoadingFloors by viewModel.isLoadingFloors.collectAsState()
    val isLoadingLayout by viewModel.isLoadingLayout.collectAsState()

    val isReserving by viewModel.isReserving.collectAsState()
    val isOccupying by viewModel.isOccupying.collectAsState()
    val isReleasing by viewModel.isReleasing.collectAsState()

    val isAdminUpdatingSpot by viewModel.isAdminUpdatingSpot.collectAsState()
    val isAdminDeletingSpot by viewModel.isAdminDeletingSpot.collectAsState()
    val isAdminCreatingSpot by viewModel.isAdminCreatingSpot.collectAsState()

    val isAdminCreatingLayoutElement by viewModel.isAdminCreatingLayoutElement.collectAsState()
    val isAdminDeletingLayoutElement by viewModel.isAdminDeletingLayoutElement.collectAsState()
    val isAdminMovingLayoutElement by viewModel.isAdminMovingLayoutElement.collectAsState()

    val reservationMessage by viewModel.reservationMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    val reservationHistory by viewModel.reservationHistory.collectAsState()
    val isLoadingReservationHistory by viewModel.isLoadingReservationHistory.collectAsState()
    val isUpdatingFullName by viewModel.isUpdatingFullName.collectAsState()

    val pendingStaffRequests by viewModel.pendingStaffRequests.collectAsState()
    val isLoadingPendingStaffRequests by viewModel.isLoadingPendingStaffRequests.collectAsState()
    val isResolvingStaffRequest by viewModel.isResolvingStaffRequest.collectAsState()

    val orgStaffMembers by viewModel.orgStaffMembers.collectAsState()
    val isLoadingOrgStaffMembers by viewModel.isLoadingOrgStaffMembers.collectAsState()
    val isRevokingStaffAccess by viewModel.isRevokingStaffAccess.collectAsState()

    val incidentReports by viewModel.incidentReports.collectAsState()
    val isLoadingIncidentReports by viewModel.isLoadingIncidentReports.collectAsState()
    val isCreatingIncidentReport by viewModel.isCreatingIncidentReport.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    var spotToReserve by remember { mutableStateOf<ParkingSpot?>(null) }
    var showOccupyDialog by remember { mutableStateOf(false) }
    var showReleaseDialog by remember { mutableStateOf(false) }

    var spotToEditByAdmin by remember { mutableStateOf<ParkingSpot?>(null) }

    var showAdminCreateSpotDialog by remember { mutableStateOf(false) }
    var adminCreateSpotTarget by remember { mutableStateOf<AdminCreateSpotTarget?>(null) }

    var adminLayoutCellTarget by remember { mutableStateOf<AdminLayoutCellTarget?>(null) }
    var layoutElementToEdit by remember { mutableStateOf<ParkingLayoutElement?>(null) }
    var layoutElementToMove by remember { mutableStateOf<ParkingLayoutElement?>(null) }

    var showStaffIncidentReportDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lastCreatedIncidentReport by viewModel.lastCreatedIncidentReport.collectAsState()

    val tabs = listOf(
        BottomTab("Perfil", Icons.Default.Person),
        BottomTab("Inicio", Icons.Default.Home),
        BottomTab("Mapa", Icons.Default.LocationOn),
        BottomTab("Avisos", Icons.Default.Notifications)
    )

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    LaunchedEffect(lastCreatedIncidentReport) {
        val report = lastCreatedIncidentReport ?: return@LaunchedEffect

        try {
            val pdfFile = IncidentReportPdfGenerator.createIncidentReportPdf(
                context = context,
                report = report,
                staffName = userFullName,
                staffEmail = userEmail
            )

            IncidentReportPdfGenerator.shareIncidentReportPdf(
                context = context,
                pdfFile = pdfFile
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "El reporte se guardó, pero no se pudo generar el PDF.",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.clearLastCreatedIncidentReport()
    }

    if (showStaffIncidentReportDialog) {
        StaffIncidentReportDialog(
            selectedParkingLot = selectedParkingLot,
            staffName = userFullName,
            staffEmail = userEmail,
            isCreating = isCreatingIncidentReport,
            onDismiss = {
                if (!isCreatingIncidentReport) {
                    showStaffIncidentReportDialog = false
                }
            },
            onSubmit = { spotNumber, vehiclePlate, incidentType, customIncidentType, details ->
                viewModel.staffCreateIncidentReport(
                    spotNumber = spotNumber,
                    vehiclePlate = vehiclePlate,
                    incidentType = incidentType,
                    customIncidentType = customIncidentType,
                    details = details
                )

                showStaffIncidentReportDialog = false
            }
        )
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

    adminLayoutCellTarget?.let { target ->
        AdminLayoutCellActionDialog(
            target = target,
            isCreatingElement = isAdminCreatingLayoutElement,
            onDismiss = {
                if (!isAdminCreatingLayoutElement) {
                    adminLayoutCellTarget = null
                }
            },
            onCreateSpot = {
                adminCreateSpotTarget = AdminCreateSpotTarget(
                    floorId = target.floorId,
                    rowIndex = target.rowIndex,
                    colIndex = target.colIndex
                )

                adminLayoutCellTarget = null
                showAdminCreateSpotDialog = true
            },
            onCreateElement = { elementType, label, description ->
                viewModel.adminCreateLayoutElement(
                    floorId = target.floorId,
                    elementType = elementType,
                    rowIndex = target.rowIndex,
                    colIndex = target.colIndex,
                    label = label,
                    description = description
                )

                adminLayoutCellTarget = null
            }
        )
    }

    layoutElementToEdit?.let { element ->
        AdminLayoutElementDialog(
            element = element,
            isDeleting = isAdminDeletingLayoutElement,
            isMoving = isAdminMovingLayoutElement,
            onDismiss = {
                if (!isAdminDeletingLayoutElement && !isAdminMovingLayoutElement) {
                    layoutElementToEdit = null
                }
            },
            onMove = {
                layoutElementToMove = element
                layoutElementToEdit = null
            },
            onDelete = {
                viewModel.adminDeleteLayoutElement(element)
                layoutElementToEdit = null
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

    spotToEditByAdmin?.let { spot ->
        AdminEditParkingSpotDialog(
            spot = spot,
            isSaving = isAdminUpdatingSpot,
            isDeleting = isAdminDeletingSpot,
            isMoving = isAdminMovingLayoutElement,
            canMove = spot.status.equals("maintenance", ignoreCase = true) &&
                    layoutElements.any { it.parkingSpotId == spot.id },
            onDismiss = {
                if (!isAdminUpdatingSpot &&
                    !isAdminDeletingSpot &&
                    !isAdminMovingLayoutElement
                ) {
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
            onMove = {
                val element = layoutElements.firstOrNull { it.parkingSpotId == spot.id }

                if (element != null) {
                    layoutElementToMove = element
                }

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
                        onClick = {
                            selectedTab = index
                        },
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
                userFullName = userFullName,
                userEmail = userEmail,
                parkingLots = parkingLots,
                selectedParkingLot = selectedParkingLot,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                reservationHistory = reservationHistory,
                isLoadingReservationHistory = isLoadingReservationHistory,
                isUpdatingFullName = isUpdatingFullName,
                isOccupying = isOccupying,
                isReleasing = isReleasing,
                onUpdateFullName = { newName ->
                    viewModel.updateMyFullName(newName)
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
                selectedParkingLot = selectedParkingLot,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                orgStaffMembers = orgStaffMembers,
                isLoadingOrgStaffMembers = isLoadingOrgStaffMembers,
                isRevokingStaffAccess = isRevokingStaffAccess,
                isLoading = isLoadingLots,
                error = error,
                onRetry = {
                    viewModel.loadDashboard()
                },
                onGoToMap = {
                    selectedTab = 2
                },
                staffStatus = staffStatus,
                incidentReports = incidentReports,
                isLoadingIncidentReports = isLoadingIncidentReports,
                isCreatingIncidentReport = isCreatingIncidentReport,
                onCreateIncidentReportClick = {
                    showStaffIncidentReportDialog = true
                },
                onRevokeStaffAccess = { member ->
                    viewModel.revokeStaffAccess(member)
                },
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
                movingLayoutElement = layoutElementToMove,
                isAdminMovingLayoutElement = isAdminMovingLayoutElement,
                error = error,
                onRetry = {
                    viewModel.loadSelectedParkingLotSpots()
                },
                onGoToParkingLots = {
                    selectedTab = 1
                },
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
                    adminLayoutCellTarget = AdminLayoutCellTarget(
                        floorId = floorId,
                        rowIndex = rowIndex,
                        colIndex = colIndex
                    )
                },
                onAdminLayoutElementClick = { element ->
                    layoutElementToEdit = element
                },
                onCancelMoveLayoutElement = {
                    if (!isAdminMovingLayoutElement) {
                        layoutElementToMove = null
                    }
                },
                onAdminMoveLayoutElementToCell = { floorId, rowIndex, colIndex ->
                    layoutElementToMove?.let { element ->
                        viewModel.adminMoveLayoutElement(
                            element = element,
                            targetFloorId = floorId,
                            targetRowIndex = rowIndex,
                            targetColIndex = colIndex
                        )
                    }

                    layoutElementToMove = null
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
                    .padding(innerPadding),
                role = userRole,
                selectedParkingLot = selectedParkingLot,
                spots = spots,
                layoutElements = layoutElements,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                pendingStaffRequests = pendingStaffRequests,
                isLoadingPendingStaffRequests = isLoadingPendingStaffRequests,
                isResolvingStaffRequest = isResolvingStaffRequest,
                onOpenMap = {
                    selectedTab = 2
                },
                onGoToParkingLots = {
                    selectedTab = 1
                },
                onResolveStaffRequest = { request, action ->
                    viewModel.resolveStaffRequest(
                        request = request,
                        action = action
                    )
                }
            )
        }
    }
}