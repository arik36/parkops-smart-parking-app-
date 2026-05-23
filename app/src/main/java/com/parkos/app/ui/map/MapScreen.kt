package com.parkos.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

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
    val reservationMessage by viewModel.reservationMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var spotToReserve by remember { mutableStateOf<ParkingSpot?>(null) }
    var showOccupyDialog by remember { mutableStateOf(false) }
    var showReleaseDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        bottomBar = {
            NavigationBar {
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
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            when (selectedTab) {
                0 -> ProfileTab(
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
                    role = userRole,
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
                    reservationMessage = reservationMessage,
                    error = error,
                    onRetry = { viewModel.loadSelectedParkingLotSpots() },
                    onGoToParkingLots = { selectedTab = 1 },
                    onReserveSpotClick = { spot ->
                        spotToReserve = spot
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

                3 -> NotificationsTab()
            }
        }
    }
}

@Composable
private fun ProfileTab(
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
    Text(
        text = "Mi perfil",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Usuario ParkOs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rol: ${role ?: "cargando..."}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estacionamiento seleccionado:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = selectedParkingLot?.name ?: "Ninguno",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

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

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar sesión")
    }
}

@Composable
private fun HomeTab(
    role: String?,
    parkingLots: List<ParkingLot>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Text(
        text = when (role) {
            "admin" -> "Estacionamientos de tu organización"
            "collaborator" -> "Estacionamientos asignados"
            else -> "Busca un estacionamiento"
        },
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = when (role) {
            "admin" -> "Puedes revisar los estacionamientos, pero no reservar cajones."
            "collaborator" -> "Selecciona el estacionamiento donde estás trabajando."
            else -> "Selecciona a qué estacionamiento quieres entrar."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(16.dp))

    when {
        isLoading -> {
            CenteredMessage {
                CircularProgressIndicator(color = ParkosOrange)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cargando estacionamientos...")
            }
        }

        error != null -> {
            CenteredMessage {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onRetry) {
                    Text("Reintentar")
                }
            }
        }

        parkingLots.isEmpty() -> {
            CenteredMessage {
                Text("No hay estacionamientos disponibles.")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(parkingLots) { parkingLot ->
                    ParkingLotCard(
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

@Composable
private fun ParkingLotCard(
    parkingLot: ParkingLot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = parkingLot.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = parkingLot.address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Espacios libres",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${parkingLot.availableSpots}/${parkingLot.totalSpots}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ParkosOrange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mapa")
            }
        }
    }
}

@Composable
private fun MapTab(
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
    reservationMessage: String?,
    error: String?,
    onRetry: () -> Unit,
    onGoToParkingLots: () -> Unit,
    onReserveSpotClick: (ParkingSpot) -> Unit,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit,
    onClearReservationMessage: () -> Unit
) {
    Text(
        text = "Mapa del estacionamiento",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = selectedParkingLot?.name ?: "Selecciona un estacionamiento primero",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (selectedParkingLot == null) {
        CenteredMessage {
            Text("Aún no has seleccionado estacionamiento.")

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onGoToParkingLots) {
                Text("Buscar estacionamiento")
            }
        }

        return
    }

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

    Spacer(modifier = Modifier.height(12.dp))

    ParkingLegend()

    Spacer(modifier = Modifier.height(12.dp))

    if (reservationMessage != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE6F4EA)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reservationMessage,
                    color = Color(0xFF1B5E20),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = onClearReservationMessage) {
                    Text("OK")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (isReserving || isOccupying) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = ParkosOrange,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = if (isOccupying) {
                    "Confirmando llegada..."
                } else {
                    "Reservando cajón..."
                }
            )
        }
    }

    if (activeReservation == null && role != "admin") {
        Text(
            text = "Toca un cajón disponible para reservarlo.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (role == "admin") {
        Text(
            text = "Modo administrador: vista de cajones sin reservación.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

    when {
        isLoading -> {
            CenteredMessage {
                CircularProgressIndicator(color = ParkosOrange)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cargando cajones...")
            }
        }

        error != null -> {
            CenteredMessage {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onRetry) {
                    Text("Reintentar")
                }
            }
        }

        spots.isEmpty() -> {
            CenteredMessage {
                Text("No hay cajones registrados.")
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(spots) { spot ->
                    ParkingSpotCard(
                        role = role,
                        spot = spot,
                        activeReservation = activeReservation,
                        onClick = {
                            if (canReserveSpot(role, spot, activeReservation)) {
                                onReserveSpotClick(spot)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveReservationCard(
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isReserved) {
                Color(0xFFFFF3D6)
            } else {
                Color(0xFFE6F4EA)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = if (isReserved) {
                    "Reservación pendiente"
                } else {
                    "Cajón ocupado"
                },
                fontWeight = FontWeight.Bold,
                color = if (isReserved) Color(0xFF6D4C00) else Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$spotLabel | $parkingLotLabel",
                color = if (isReserved) Color(0xFF6D4C00) else Color(0xFF1B5E20),
                style = MaterialTheme.typography.bodyMedium
            )

            if (isReserved) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tiempo restante: ${formatRemainingTime(remainingSeconds)}",
                    color = Color(0xFF6D4C00),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOccupyClick,
                    enabled = !isOccupying && remainingSeconds > 0L,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isOccupying) "Confirmando..." else "Ya llegué")
                }
            }

            if (isActive) {
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onReleaseClick,
                    enabled = !isReleasing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isReleasing) "Liberando..." else "Estoy saliendo")
                }
            }
        }
    }
}

@Composable
private fun NotificationsTab() {
    Text(
        text = "Avisos",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Dudas frecuentes",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aquí podrás ver ayuda, políticas del estacionamiento y notificaciones.",
                color = Color.DarkGray
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
            .height(124.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = if (isActiveUserSpot) 3.dp else 1.dp,
                color = if (isActiveUserSpot) ParkosOrange else borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                enabled = canReserveSpot(role, spot, activeReservation),
                onClick = onClick
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = spot.spotNumber,
            fontWeight = FontWeight.Bold,
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = spot.status,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = spot.type,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ParkingLegend() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Leyenda",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem("Disponible", Color(0xFFE6F4EA))
            LegendItem("Ocupado", Color(0xFFFFE5E5))
            LegendItem("Reservado", Color(0xFFFFF3D6))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LegendItem("Staff", Color(0xFFEDE7F6))
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CenteredMessage(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
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
        spot.type.lowercase() == "staff" -> Color(0xFFEDE7F6)
        spot.type.lowercase() == "disabled" -> Color(0xFFE3F2FD)
        spot.status.lowercase() == "available" -> Color(0xFFE6F4EA)
        spot.status.lowercase() == "occupied" -> Color(0xFFFFE5E5)
        spot.status.lowercase() == "reserved" -> Color(0xFFFFF3D6)
        else -> Color.White
    }
}

private fun getSpotBorderColor(spot: ParkingSpot): Color {
    return when {
        spot.type.lowercase() == "staff" -> Color(0xFF6A1B9A)
        spot.type.lowercase() == "disabled" -> Color(0xFF1976D2)
        spot.status.lowercase() == "available" -> Color(0xFF2E7D32)
        spot.status.lowercase() == "occupied" -> Color(0xFFC62828)
        spot.status.lowercase() == "reserved" -> Color(0xFFF9A825)
        else -> Color.LightGray
    }
}

private fun getSpotTextColor(spot: ParkingSpot): Color {
    return when {
        spot.type.lowercase() == "staff" -> Color(0xFF4A148C)
        spot.type.lowercase() == "disabled" -> Color(0xFF0D47A1)
        spot.status.lowercase() == "available" -> Color(0xFF1B5E20)
        spot.status.lowercase() == "occupied" -> Color(0xFF8E0000)
        spot.status.lowercase() == "reserved" -> Color(0xFF6D4C00)
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