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

private val ParkosBackground = Color(0xFFF8EFE4)
private val ParkosHomeOrange = Color(0xFFFE854F)
private val ParkosCard = Color.White
private val ParkosMutedText = Color(0xFF6F6F6F)
private val ParkosBorder = Color(0xFFE9DED1)
private val ParkosSoftOrange = Color(0xFFFFE7D6)
private val ParkosSoftGreen = Color(0xFFE8F4EA)
private val ParkosSoftRed = Color(0xFFFBE5E5)
private val ParkosSoftYellow = Color(0xFFFFF4D8)
private val ParkosSoftBlue = Color(0xFFE4F0FB)
private val ParkosSoftPurple = Color(0xFFF0E9F8)
private val ParkosSearchBorder = Color(0xFFFFA15E)
private val ParkosTableHeader = Color(0xFFFFBD59)
private val SearchFrameWhite = Color.White.copy(alpha = 0.37f)
private val SearchInnerWhite = Color.White.copy(alpha = 0.74f)
private val ResultsPanelWhite = Color.White.copy(alpha = 0.38f)
private val ResultsHeaderOrange = Color(0xFFF6A01E).copy(alpha = 0.45f)
private val ParkosSoftMaintenance = Color(0xFFE6E3DD)
private val ParkosMaintenanceText = Color(0xFF4E4A45)
private val ParkosMaintenanceBorder = Color(0xFF8A8378)

private data class BottomTab(
    val label: String,
    val icon: ImageVector
)

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
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                activeReservationParkingLotName = activeReservationParkingLotName,
                isLoading = isLoadingSpots,
                isReserving = isReserving,
                isOccupying = isOccupying,
                isReleasing = isReleasing,
                isAdminUpdatingSpot = isAdminUpdatingSpot,
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
private fun ProfileTab(
    modifier: Modifier = Modifier,
    role: String?,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    isOccupying: Boolean,
    isReleasing: Boolean,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .background(ParkosBackground)
            .padding(bottom = 20.dp)
    ) {
        item {
            HeaderSection(
                title = "Mi perfil",
                subtitle = "Tu cuenta ParkOs"
            )
        }

        item {
            ProfileHeroCard(
                role = role,
                selectedParkingLot = selectedParkingLot,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber
            )
        }

        item {
            ActiveReservationCard(
                modifier = Modifier.padding(horizontal = 20.dp),
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

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ParkosOrange)
            ) {
                Text("Cerrar sesión", color = ParkosOrange)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkosOrange)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.92f)
        )
    }
}

@Composable
private fun ProfileHeroCard(
    role: String?,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 14.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ParkosCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .background(ParkosSoftOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        color = ParkosOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Usuario ParkOs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = roleToDisplay(role),
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(ParkosSoftOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = ParkosOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Rol",
                    value = shortRole(role)
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Mapa",
                    value = if (selectedParkingLot != null) "Listo" else "Pendiente"
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Cajón",
                    value = activeReservationSpotNumber ?: "--"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Estacionamiento seleccionado",
                style = MaterialTheme.typography.labelLarge,
                color = ParkosMutedText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = selectedParkingLot?.name ?: "Ninguno",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (activeReservation != null) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tienes una reservación o uso activo.",
                    color = ParkosOrange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF7)),
        border = BorderStroke(1.dp, ParkosBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = ParkosMutedText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
private fun HomeTab(
    modifier: Modifier = Modifier,
    role: String?,
    userFullName: String?,
    userEmail: String?,
    parkingLots: List<ParkingLot>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    if (role == "admin" || role == "collaborator") {
        OrganizationHomeTab(
            modifier = modifier,
            role = role,
            parkingLots = parkingLots,
            isLoading = isLoading,
            error = error,
            onRetry = onRetry,
            onSelectParkingLot = onSelectParkingLot
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredParkingLots = remember(parkingLots, searchQuery) {
        val cleanQuery = searchQuery.trim().lowercase()

        if (cleanQuery.isBlank()) {
            parkingLots
        } else {
            parkingLots.filter { parkingLot ->
                parkingLot.name.lowercase().contains(cleanQuery) ||
                        parkingLot.address.lowercase().contains(cleanQuery)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        HomeCurvedOrangeBackground()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                HomeProfileHeader(
                    fullName = userFullName,
                    email = userEmail
                )
            }

            item {
                HomeQuickActionsCard(
                    role = role,
                    parkingLots = parkingLots
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }

            when {
                isLoading -> {
                    item {
                        HomeStateCard(
                            message = "Cargando estacionamientos...",
                            showLoading = true
                        )
                    }
                }

                error != null -> {
                    item {
                        HomeStateCard(
                            message = error,
                            actionText = "Reintentar",
                            onAction = onRetry,
                            isError = true
                        )
                    }
                }

                parkingLots.isEmpty() -> {
                    item {
                        HomeStateCard(
                            message = "No hay estacionamientos disponibles."
                        )
                    }
                }

                else -> {
                    item {
                        Text(
                            text = "Estacionamientos disponibles",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            fontSize = 17.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    item {
                        HomeSearchCard(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    item {
                        if (filteredParkingLots.isEmpty()) {
                            EmptyHomeResultCard()
                        } else {
                            ParkingLotResultsPanel(
                                parkingLots = filteredParkingLots,
                                onSelectParkingLot = onSelectParkingLot
                            )
                        }
                    }
                }
            }
        }

        HomeBottomWhiteOverlay()
    }
}

@Composable
private fun OrganizationHomeTab(
    modifier: Modifier = Modifier,
    role: String?,
    parkingLots: List<ParkingLot>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ParkosBackground)
    ) {
        HeaderSection(
            title = when (role) {
                "admin" -> "Panel de organización"
                else -> "Estacionamiento asignado"
            },
            subtitle = when (role) {
                "admin" -> "Administra los estacionamientos vinculados a tu organización."
                else -> "Consulta el estacionamiento vinculado a tu organización."
            }
        )

        when {
            isLoading -> {
                CenteredMessage(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = ParkosOrange)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Cargando estacionamientos...")
                }
            }

            error != null -> {
                CenteredMessage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParkosOrange
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            parkingLots.isEmpty() -> {
                CenteredMessage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "No hay estacionamientos vinculados a tu organización.",
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        OrganizationOverviewCard(
                            role = role,
                            parkingLots = parkingLots
                        )
                    }

                    item {
                        Text(
                            text = if (role == "admin") {
                                "Estacionamientos de tu organización"
                            } else {
                                "Tu estacionamiento"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    items(parkingLots) { parkingLot ->
                        OrganizationParkingLotCard(
                            role = role,
                            parkingLot = parkingLot,
                            onClick = {
                                onSelectParkingLot(parkingLot)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizationOverviewCard(
    role: String?,
    parkingLots: List<ParkingLot>
) {
    val totalSpots = parkingLots.sumOf { it.totalSpots }
    val availableSpots = parkingLots.sumOf { it.availableSpots }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = if (role == "admin") {
                    "Resumen operativo"
                } else {
                    "Resumen de trabajo"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "ParkOs",
                    value = parkingLots.size.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Libres",
                    value = availableSpots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Total",
                    value = totalSpots.toString()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (role == "admin") {
                    "Puedes visualizar los cajones, pero no reservarlos."
                } else {
                    "Puedes reservar únicamente cajones staff disponibles."
                },
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun OrganizationParkingLotCard(
    role: String?,
    parkingLot: ParkingLot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8BD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = parkingLot.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = parkingLot.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = ParkosSoftOrange,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${parkingLot.availableSpots}/${parkingLot.totalSpots}",
                        color = ParkosOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                )
            ) {
                Text(
                    text = if (role == "admin") {
                        "Ver mapa operativo"
                    } else {
                        "Ver cajones staff"
                    }
                )
            }
        }
    }
}
@Composable
private fun HomeCurvedOrangeBackground() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val topWhiteHeight = 178.dp.toPx()
        val orangeStart = 145.dp.toPx()

        drawRect(
            color = Color.White,
            size = Size(size.width, topWhiteHeight)
        )

        val orangePath = Path().apply {
            moveTo(0f, orangeStart)

            quadraticBezierTo(
                size.width * 0.5f,
                orangeStart + 58.dp.toPx(),
                size.width,
                orangeStart
            )

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(
            path = orangePath,
            color = ParkosHomeOrange
        )
    }
}
@Composable
private fun HomeBottomWhiteOverlay() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val ovalTop = size.height - 48.dp.toPx()

        drawOval(
            color = Color.White,
            topLeft = Offset(
                x = -size.width * 0.18f,
                y = ovalTop
            ),
            size = Size(
                width = size.width * 1.36f,
                height = 112.dp.toPx()
            )
        )

        drawRect(
            color = Color.White,
            topLeft = Offset(
                x = 0f,
                y = ovalTop + 56.dp.toPx()
            ),
            size = Size(
                width = size.width,
                height = size.height
            )
        )
    }
}
@Composable
private fun HomeProfileHeader(
    fullName: String?,
    email: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(Color(0xFF9BCFD0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getInitial(fullName),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fullName?.takeIf { it.isNotBlank() } ?: "Usuario ParkOs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email?.takeIf { it.isNotBlank() } ?: "Sin correo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF777777),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFF1E5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = ParkosOrange
            )
        }
    }
}

@Composable
private fun HomeQuickActionsCard(
    role: String?,
    parkingLots: List<ParkingLot>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
            .padding(top = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .padding(top = 14.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFFFD8BD)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeQuickActionValue(
                    modifier = Modifier.weight(1f),
                    value = shortRole(role),
                    label = "Tipo de cuenta"
                )

                HomeVerticalDivider()

                HomeQuickActionValue(
                    modifier = Modifier.weight(1f),
                    value = parkingLots.size.toString(),
                    label = "ParkOs"
                )

                HomeVerticalDivider()

                HomeQuickActionValue(
                    modifier = Modifier.weight(1f),
                    value = "0",
                    label = "Avisos"
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            FloatingQuickIcon(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Person,
                label = "Tipo de cuenta"
            )

            FloatingQuickIcon(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Home,
                label = "ParkOs"
            )

            FloatingQuickIcon(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Notifications,
                label = "Avisos"
            )
        }
    }
}

@Composable
private fun FloatingQuickIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String
) {
    Box(
        modifier = modifier.offset(y = 0.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(ParkosOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HomeQuickActionValue(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 22.dp, start = 6.dp, end = 6.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF777777),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun HomeQuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ParkosOrange,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF777777),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun HomeVerticalDivider() {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(58.dp)
            .background(Color(0xFFFFE4D0))
    )
}

@Composable
private fun HomeSearchCard(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = SearchFrameWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                placeholder = {
                    Text(
                        text = "estacionamiento / dirección",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp
                ),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SearchInnerWhite,
                    unfocusedContainerColor = SearchInnerWhite,
                    focusedBorderColor = ParkosOrange,
                    unfocusedBorderColor = ParkosOrange,
                    cursorColor = ParkosOrange
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = SearchInnerWhite,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = ParkosOrange,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = ParkosOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
@Composable
private fun ParkingLotResultsPanel(
    parkingLots: List<ParkingLot>,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ResultsPanelWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(
                        color = ResultsHeaderOrange,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell(
                    text = "Nombre",
                    modifier = Modifier.weight(1.05f)
                )

                TableHeaderCell(
                    text = "Dirección",
                    modifier = Modifier.weight(1.05f)
                )

                TableHeaderCell(
                    text = "Libres",
                    modifier = Modifier.weight(0.6f)
                )
            }

            parkingLots.forEachIndexed { index, parkingLot ->
                ParkingLotTransparentRow(
                    parkingLot = parkingLot,
                    showDivider = index != parkingLots.lastIndex,
                    onClick = {
                        onSelectParkingLot(parkingLot)
                    }
                )
            }

            repeat((3 - parkingLots.size).coerceAtLeast(0)) {
                EmptyTransparentRow()
            }
        }
    }
}
@Composable
private fun ParkingLotTransparentRow(
    parkingLot: ParkingLot,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parkingLot.name,
                modifier = Modifier.weight(1.05f),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                softWrap = true
            )

            Text(
                text = parkingLot.address,
                modifier = Modifier.weight(1.05f),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${parkingLot.availableSpots}/${parkingLot.totalSpots}",
                modifier = Modifier.weight(0.6f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.28f))
            )
        }
    }
}
@Composable
private fun EmptyTransparentRow() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.22f))
        )
    }
}
@Composable
private fun ParkingLotResultCard(
    parkingLot: ParkingLot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7F0)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFCCAA)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = parkingLot.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = parkingLot.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = ParkosOrange,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${parkingLot.availableSpots}/${parkingLot.totalSpots}",
                        color = ParkosOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Libres",
                    color = Color(0xFF777777),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun EmptyHomeResultCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No encontramos resultados",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Intenta buscar por nombre o dirección.",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HomeStateCard(
    message: String,
    showLoading: Boolean = false,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    isError: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showLoading) {
                CircularProgressIndicator(
                    color = ParkosOrange,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = message,
                color = if (isError) MaterialTheme.colorScheme.error else Color.DarkGray,
                textAlign = TextAlign.Center
            )

            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    )
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

@Composable
private fun ParkingSearchCard(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, ParkosSearchBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("estacionamiento / dirección")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = ParkosOrange
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

@Composable
private fun EmptySearchCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No encontramos resultados",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Intenta buscar por nombre o dirección.",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ParkingLotsTable(
    parkingLots: List<ParkingLot>,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, ParkosOrange)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ParkosTableHeader)
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell(
                    text = "Nombre",
                    modifier = Modifier.weight(1f)
                )

                TableHeaderCell(
                    text = "Dirección",
                    modifier = Modifier.weight(1.25f)
                )

                TableHeaderCell(
                    text = "Libres",
                    modifier = Modifier.weight(0.8f)
                )
            }

            parkingLots.forEachIndexed { index, parkingLot ->
                ParkingLotTableRow(
                    parkingLot = parkingLot,
                    showDivider = index != parkingLots.lastIndex,
                    onClick = {
                        onSelectParkingLot(parkingLot)
                    }
                )
            }
        }
    }
}
@Composable
private fun TableHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 8.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ParkingLotTableRow(
    parkingLot: ParkingLot,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parkingLot.name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = parkingLot.address,
                modifier = Modifier.weight(1.25f),
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${parkingLot.availableSpots}/${parkingLot.totalSpots}",
                modifier = Modifier.weight(0.8f),
                color = ParkosOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFFFE2C7))
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
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    isLoading: Boolean,
    isReserving: Boolean,
    isOccupying: Boolean,
    isReleasing: Boolean,
    isAdminUpdatingSpot: Boolean,
    reservationMessage: String?,
    error: String?,
    onRetry: () -> Unit,
    onGoToParkingLots: () -> Unit,
    onReserveSpotClick: (ParkingSpot) -> Unit,
    onAdminEditSpotClick: (ParkingSpot) -> Unit,
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
                    Text(
                        text = "Modo administrador: toca un cajón disponible o en mantenimiento para editarlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
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
                        val rowCount = ((spots.size + 2) / 3).coerceAtLeast(1)
                        val gridHeight =
                            (128.dp * rowCount.toFloat()) +
                                    (12.dp * (rowCount - 1).toFloat())

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(spots) { spot ->
                                ParkingSpotCard(
                                    role = role,
                                    spot = spot,
                                    activeReservation = activeReservation,
                                    onClick = {
                                        if (role == "admin") {
                                            onAdminEditSpotClick(spot)
                                        } else if (canReserveSpot(role, spot, activeReservation)) {
                                            onReserveSpotClick(spot)
                                        }
                                    }
                                )
                            }
                        }
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
private fun ActiveReservationCard(
    modifier: Modifier = Modifier,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    isOccupying: Boolean,
    isReleasing: Boolean,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit
) {
    if (activeReservation == null) {
        return
    }

    val spotLabel = activeReservationSpotNumber ?: "Cajón desconocido"
    val parkingLotLabel = activeReservationParkingLotName ?: "Estacionamiento no identificado"
    val isReserved = activeReservation.status.equals("reserved", ignoreCase = true)
    val isActive = activeReservation.status.equals("active", ignoreCase = true)

    var remainingSeconds by remember(activeReservation.id, activeReservation.expiresAt) {
        mutableLongStateOf(calculateRemainingSeconds(activeReservation.expiresAt))
    }

    LaunchedEffect(activeReservation.id, activeReservation.status, activeReservation.expiresAt) {
        if (isReserved) {
            while (true) {
                val seconds = calculateRemainingSeconds(activeReservation.expiresAt)
                remainingSeconds = seconds

                if (seconds <= 0L) {
                    onReservationExpired()
                    break
                }

                delay(1000)
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isReserved) {
                ParkosSoftYellow
            } else {
                ParkosSoftGreen
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = if (isReserved) "Reservación pendiente" else "Cajón ocupado",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (isReserved) Color(0xFF7A5700) else Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$spotLabel | $parkingLotLabel",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isReserved) Color(0xFF7A5700) else Color(0xFF1B5E20)
            )

            if (isReserved) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tiempo restante: ${formatRemainingTime(remainingSeconds)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7A5700)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOccupyClick,
                    enabled = !isOccupying && remainingSeconds > 0L,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ParkosOrange)
                ) {
                    Text(if (isOccupying) "Confirmando..." else "Ya llegué")
                }
            }

            if (isActive) {
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onReleaseClick,
                    enabled = !isReleasing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ParkosOrange)
                ) {
                    Text(if (isReleasing) "Liberando..." else "Estoy saliendo")
                }
            }
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
private fun AdminEditParkingSpotDialog(
    spot: ParkingSpot,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var selectedStatus by remember(spot.id) {
        mutableStateOf(
            if (spot.status.equals("maintenance", ignoreCase = true)) {
                "maintenance"
            } else {
                "available"
            }
        )
    }

    var selectedType by remember(spot.id) {
        mutableStateOf(spot.type.lowercase())
    }

    val isBlockedByUser = spot.status.equals("occupied", ignoreCase = true) ||
            spot.status.equals("reserved", ignoreCase = true)

    val statusOptions = listOf(
        "available" to "Disponible",
        "maintenance" to "Mantenimiento"
    )

    val typeOptions = listOf(
        "normal" to "Normal",
        "disabled" to "Discapacitado",
        "electric" to "Eléctrico",
        "staff" to "Staff"
    )

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Editar cajón ${spot.spotNumber}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Estado actual: ${statusToSpanish(spot.status)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParkosMutedText
                )

                Text(
                    text = "Tipo actual: ${typeToSpanish(spot.type)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParkosMutedText
                )

                if (isBlockedByUser) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ParkosSoftRed
                        )
                    ) {
                        Text(
                            text = "Este cajón no se puede editar porque está reservado u ocupado por un usuario. Espera a que sea liberado para iniciar mantenimiento.",
                            modifier = Modifier.padding(14.dp),
                            color = Color(0xFF8E1B1B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    return@Column
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Estado",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { option ->
                        AdminOptionButton(
                            label = option.second,
                            selected = selectedStatus == option.first,
                            enabled = !isSaving,
                            onClick = {
                                selectedStatus = option.first
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipo",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    typeOptions.forEach { option ->
                        AdminOptionButton(
                            label = option.second,
                            selected = selectedType == option.first,
                            enabled = !isSaving,
                            onClick = {
                                selectedType = option.first
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tip: usa mantenimiento cuando el cajón tenga baches, cambios de tamaño, reparación, pintura o cualquier bloqueo operativo.",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving && !isBlockedByUser,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    onSave(selectedStatus, selectedType)
                }
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSaving,
                onClick = onDismiss
            ) {
                Text(if (isBlockedByUser) "Entendido" else "Cancelar")
            }
        }
    )
}


@Composable
private fun AdminOptionButton(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) ParkosOrange else Color.LightGray
        )
    ) {
        Text(
            text = label,
            color = if (selected) ParkosOrange else Color.DarkGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
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
@Composable
private fun CenteredMessage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

private fun canReserveSpot(
    role: String?,
    spot: ParkingSpot,
    activeReservation: Reservation?
): Boolean {
    if (activeReservation != null) return false
    if (role == "admin") return false
    if (!spot.status.equals("available", ignoreCase = true)) return false

    if (role == "consumer" && spot.type.equals("staff", ignoreCase = true)) {
        return false
    }

    if (role == "collaborator" && !spot.type.equals("staff", ignoreCase = true)) {
        return false
    }

    return true
}
private fun getSpotBackgroundColor(spot: ParkingSpot): Color {
    return when {
        spot.status.equals("maintenance", ignoreCase = true) -> ParkosSoftMaintenance
        spot.status.equals("occupied", ignoreCase = true) -> ParkosSoftRed
        spot.status.equals("reserved", ignoreCase = true) -> ParkosSoftYellow
        spot.type.equals("staff", ignoreCase = true) -> ParkosSoftPurple
        spot.type.equals("disabled", ignoreCase = true) -> ParkosSoftBlue
        spot.status.equals("available", ignoreCase = true) -> ParkosSoftGreen
        else -> Color.White
    }
}

private fun getSpotBorderColor(spot: ParkingSpot): Color {
    return when {
        spot.status.equals("maintenance", ignoreCase = true) -> ParkosMaintenanceBorder
        spot.status.equals("occupied", ignoreCase = true) -> Color(0xFFC94A4A)
        spot.status.equals("reserved", ignoreCase = true) -> Color(0xFFC49A22)
        spot.type.equals("staff", ignoreCase = true) -> Color(0xFF7A4BB7)
        spot.type.equals("disabled", ignoreCase = true) -> Color(0xFF2B6CB0)
        spot.status.equals("available", ignoreCase = true) -> Color(0xFF3C8D40)
        else -> Color.LightGray
    }
}

private fun getSpotTextColor(spot: ParkingSpot): Color {
    return when {
        spot.status.equals("maintenance", ignoreCase = true) -> ParkosMaintenanceText
        spot.status.equals("occupied", ignoreCase = true) -> Color(0xFF9F2F2F)
        spot.status.equals("reserved", ignoreCase = true) -> Color(0xFF7A5700)
        spot.type.equals("staff", ignoreCase = true) -> Color(0xFF5A2B93)
        spot.type.equals("disabled", ignoreCase = true) -> Color(0xFF144D84)
        spot.status.equals("available", ignoreCase = true) -> Color(0xFF256C2B)
        else -> Color.Black
    }
}

private fun calculateRemainingSeconds(expiresAt: String?): Long {
    if (expiresAt.isNullOrBlank()) return 0L

    return try {
        val normalizedDate = normalizeSupabaseTimestamp(expiresAt)

        val formatter = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            Locale.US
        )

        val expiresDate = formatter.parse(normalizedDate) ?: return 0L
        val nowMillis = System.currentTimeMillis()
        val diffMillis = expiresDate.time - nowMillis
        val seconds = diffMillis / 1000L

        if (seconds < 0L) 0L else seconds
    } catch (e: Exception) {
        0L
    }
}

private fun normalizeSupabaseTimestamp(value: String): String {
    var normalized = value.trim().replace(" ", "T")

    if (normalized.endsWith("+00")) {
        normalized = normalized.removeSuffix("+00") + "+00:00"
    }

    if (normalized.endsWith("-00")) {
        normalized = normalized.removeSuffix("-00") + "-00:00"
    }

    val timezoneRegex = Regex("([+-]\\d{2})(\\d{2})$")
    normalized = normalized.replace(timezoneRegex) {
        "${it.groupValues[1]}:${it.groupValues[2]}"
    }

    val fractionRegex = Regex("\\.(\\d{1,9})(Z|[+-]\\d{2}:\\d{2})$")
    normalized = normalized.replace(fractionRegex) {
        val milliseconds = it.groupValues[1]
            .padEnd(3, '0')
            .take(3)

        ".$milliseconds${it.groupValues[2]}"
    }

    val noFractionRegex = Regex("(\\d{2}:\\d{2}:\\d{2})(Z|[+-]\\d{2}:\\d{2})$")
    normalized = normalized.replace(noFractionRegex) {
        "${it.groupValues[1]}.000${it.groupValues[2]}"
    }

    if (normalized.endsWith("Z")) {
        normalized = normalized.removeSuffix("Z") + "+00:00"
    }

    return normalized
}

private fun formatRemainingTime(seconds: Long): String {
    val safeSeconds = if (seconds < 0L) 0L else seconds
    val minutesPart = safeSeconds / 60
    val secondsPart = safeSeconds % 60

    return "%02d:%02d".format(minutesPart, secondsPart)
}

private fun roleToDisplay(role: String?): String {
    return when (role) {
        "admin" -> "Administrador"
        "collaborator" -> "Colaborador"
        "consumer" -> "Consumidor"
        else -> "Cargando..."
    }
}

private fun shortRole(role: String?): String {
    return when (role) {
        "admin" -> "Admin"
        "collaborator" -> "Colab."
        "consumer" -> "User"
        else -> "--"
    }
}

private fun homeSubtitle(role: String?): String {
    return when (role) {
        "admin" -> "Consulta los estacionamientos de tu organización."
        "collaborator" -> "Selecciona tu estacionamiento para trabajar."
        else -> "Busca y elige dónde quieres estacionarte."
    }
}

private fun getInitial(fullName: String?): String {
    return fullName
        ?.trim()
        ?.firstOrNull()
        ?.uppercase()
        ?: "P"
}

private fun statusToSpanish(status: String): String {
    return when (status.lowercase()) {
        "available" -> "Disponible"
        "reserved" -> "Reservado"
        "occupied" -> "Ocupado"
        "maintenance" -> "Mantenimiento"
        else -> status
    }
}

private fun typeToSpanish(type: String): String {
    return when (type.lowercase()) {
        "normal" -> "Normal"
        "disabled" -> "Discapacitado"
        "electric" -> "Eléctrico"
        "staff" -> "Staff"
        else -> type
    }
}
private fun statusToCardLabel(status: String): String {
    return when (status.lowercase()) {
        "available" -> "Libre"
        "reserved" -> "Reservado"
        "occupied" -> "Ocupado"
        "maintenance" -> "Mant."
        else -> status
    }
}

private fun typeToCardLabel(type: String): String {
    return when (type.lowercase()) {
        "normal" -> "Normal"
        "disabled" -> "Discap."
        "electric" -> "Eléctrico"
        "staff" -> "Staff"
        else -> type
    }
}