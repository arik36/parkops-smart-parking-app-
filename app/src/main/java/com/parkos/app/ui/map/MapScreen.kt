package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.parkos.app.domain.model.ParkingFloor
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.parkos.app.domain.model.ParkingLayoutElement



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
            onDismiss = {
                if (!isAdminUpdatingSpot) {
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

@Composable
private fun MapTab(
    modifier: Modifier = Modifier,
    role: String?,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    parkingFloors: List<ParkingFloor>,
    layoutElements: List<ParkingLayoutElement>,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    isLoading: Boolean,
    isLoadingFloors: Boolean,
    isLoadingLayout: Boolean,
    isReserving: Boolean,
    isOccupying: Boolean,
    isReleasing: Boolean,
    isAdminUpdatingSpot: Boolean,
    isAdminCreatingSpot: Boolean,
    reservationMessage: String?,
    error: String?,
    onRetry: () -> Unit,
    onGoToParkingLots: () -> Unit,
    onReserveSpotClick: (ParkingSpot) -> Unit,
    onAdminEditSpotClick: (ParkingSpot) -> Unit,
    onAdminCreateSpotClick: () -> Unit,
    onAdminCreateSpotAtCell: (String, Int, Int) -> Unit,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit,
    onClearReservationMessage: () -> Unit
) {
    Column(
        modifier = modifier.background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Mapa del estacionamiento",
            subtitle = selectedParkingLot?.name ?: "Selecciona un estacionamiento"
        )

        if (selectedParkingLot == null) {
            CenteredMessage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Aún no has seleccionado un estacionamiento.",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onGoToParkingLots,
                    colors = ButtonDefaults.buttonColors(containerColor = ParkosOrange)
                ) {
                    Text("Buscar estacionamiento")
                }
            }

            return
        }

        val maintenanceSpots = spots.filter {
            it.status.equals("maintenance", ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (activeReservation != null) {
                item {
                    ActiveReservationCard(
                        activeReservation = activeReservation,
                        activeReservationSpotNumber = activeReservationSpotNumber,
                        activeReservationParkingLotName = activeReservationParkingLotName,
                        isOccupying = isOccupying,
                        isReleasing = isReleasing,
                        onOccupyClick = onOccupyClick,
                        onReleaseClick = onReleaseClick,
                        onReservationExpired = onReservationExpired
                    )
                }
            }

            item {
                ParkingLegend()
            }

            if (role != "admin" && maintenanceSpots.isNotEmpty()) {
                item {
                    MaintenanceWarningCard(
                        maintenanceSpots = maintenanceSpots
                    )
                }
            }

            if (reservationMessage != null) {
                item {
                    MessageCard(
                        message = reservationMessage,
                        backgroundColor = ParkosSoftGreen,
                        textColor = Color(0xFF1B5E20),
                        onDismiss = onClearReservationMessage
                    )
                }
            }

            if (error != null) {
                item {
                    MessageCard(
                        message = error,
                        backgroundColor = ParkosSoftRed,
                        textColor = Color(0xFF8E1B1B),
                        actionText = "Reintentar",
                        onDismiss = onRetry
                    )
                }
            }

            if (isReserving || isOccupying) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = ParkosOrange,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 3.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (isOccupying) {
                                "Confirmando llegada..."
                            } else {
                                "Reservando cajón..."
                            }
                        )
                    }
                }
            }

            if (isAdminUpdatingSpot) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = ParkosOrange,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 3.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Actualizando cajón...")
                    }
                }
            }

            if (activeReservation == null && role != "admin") {
                item {
                    Text(
                        text = if (role == "collaborator") {
                            "Toca un cajón staff disponible para reservarlo."
                        } else {
                            "Toca un cajón disponible para reservarlo."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }
            }

            if (role == "admin") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Modo administrador",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Toca un cajón disponible o en mantenimiento para editarlo. Para agregar uno nuevo, selecciona una celda libre con fila y columna.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ParkosMutedText
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onAdminCreateSpotClick,
                                enabled = !isLoadingFloors && !isAdminCreatingSpot && parkingFloors.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ParkosOrange
                                )
                            ) {
                                Text(
                                    text = when {
                                        isLoadingFloors -> "Cargando pisos..."
                                        isAdminCreatingSpot -> "Creando cajón..."
                                        parkingFloors.isEmpty() -> "No hay pisos disponibles"
                                        else -> "Agregar cajón"
                                    }
                                )
                            }
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    item {
                        CenteredMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            CircularProgressIndicator(color = ParkosOrange)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Cargando cajones...")
                        }
                    }
                }

                spots.isEmpty() -> {
                    item {
                        CenteredMessage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            Text("No hay cajones registrados.")
                        }
                    }
                }
                else -> {
                    item {
                        ParkingLayoutGrid(
                            role = role,
                            floors = parkingFloors,
                            spots = spots,
                            layoutElements = layoutElements,
                            activeReservation = activeReservation,
                            isLoadingLayout = isLoadingLayout,
                            onReserveSpotClick = onReserveSpotClick,
                            onAdminEditSpotClick = onAdminEditSpotClick,
                            onAdminCreateSpotAtCell = onAdminCreateSpotAtCell
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun NotificationsTab(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Avisos",
            subtitle = "Información y ayuda"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                InfoCard(
                    title = "Ayuda rápida",
                    text = "Aquí puedes mostrar reglas del estacionamiento, preguntas frecuentes y avisos importantes."
                )
            }

            item {
                InfoCard(
                    title = "Reservaciones",
                    text = "Las reservaciones duran 5 minutos. Si no confirmas llegada antes de ese tiempo, el cajón vuelve a quedar disponible."
                )
            }

            item {
                InfoCard(
                    title = "Accesos por rol",
                    text = "Los consumidores reservan cajones normales. Los colaboradores reservan cajones staff. El administrador solo visualiza."
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                color = ParkosMutedText
            )
        }
    }
}

@Composable
private fun MessageCard(
    message: String,
    backgroundColor: Color,
    textColor: Color,
    actionText: String = "OK",
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            TextButton(onClick = onDismiss) {
                Text(actionText, color = textColor)
            }
        }
    }
}
@Composable
private fun MaintenanceWarningCard(
    maintenanceSpots: List<ParkingSpot>
) {
    val spotNumbers = maintenanceSpots
        .joinToString(", ") { it.spotNumber }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ParkosSoftMaintenance
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ParkosMaintenanceBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Cajones en mantenimiento",
                fontWeight = FontWeight.Bold,
                color = ParkosMaintenanceText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Por su seguridad no entre en la casilla $spotNumbers, ya que está en mantenimiento. Espere hasta que en la app aparezca como disponible. Gracias por su paciencia.",
                color = ParkosMaintenanceText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ParkingSpotCard(
    role: String?,
    spot: ParkingSpot,
    activeReservation: Reservation?,
    onClick: () -> Unit
) {
    val backgroundColor = getSpotBackgroundColor(spot)
    val borderColor = getSpotBorderColor(spot)
    val textColor = getSpotTextColor(spot)
    val isActiveUserSpot = activeReservation?.spotId == spot.id

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = if (isActiveUserSpot) 3.dp else 1.dp,
                color = if (isActiveUserSpot) ParkosOrange else borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                enabled = role == "admin" || canReserveSpot(role, spot, activeReservation),
                onClick = onClick
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = spot.spotNumber,
            fontWeight = FontWeight.Bold,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = statusToCardLabel(spot.status),
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontSize = if (spot.status.equals("maintenance", ignoreCase = true)) {
                10.sp
            } else {
                12.sp
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )

        Text(
            text = typeToCardLabel(spot.type),
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}
@Composable
private fun ParkingLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Leyenda",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LegendItem(
                        label = "Disponible",
                        color = ParkosSoftGreen,
                        modifier = Modifier.weight(1f)
                    )

                    LegendItem(
                        label = "Ocupado",
                        color = ParkosSoftRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LegendItem(
                        label = "Reservado",
                        color = ParkosSoftYellow,
                        modifier = Modifier.weight(1f)
                    )

                    LegendItem(
                        label = "Discapacitado",
                        color = ParkosSoftBlue,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LegendItem(
                        label = "Staff",
                        color = ParkosSoftPurple,
                        modifier = Modifier.weight(1f)
                    )

                    LegendItem(
                        label = "Mantenimiento",
                        color = ParkosSoftMaintenance,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
@Composable
private fun LegendItem(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
