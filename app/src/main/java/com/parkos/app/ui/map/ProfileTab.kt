package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.domain.model.ReservationHistoryItem
import com.parkos.app.ui.theme.ParkosOrange
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun ProfileTab(
    modifier: Modifier = Modifier,
    role: String?,
    userFullName: String?,
    userEmail: String?,
    parkingLots: List<ParkingLot>,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    reservationHistory: List<ReservationHistoryItem>,
    isLoadingReservationHistory: Boolean,
    isUpdatingFullName: Boolean,
    isOccupying: Boolean,
    isReleasing: Boolean,
    onUpdateFullName: (String) -> Unit,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit,
    onLogout: () -> Unit
) {
    if (role == "admin") {
        AdminProfileTab(
            modifier = modifier,
            userFullName = userFullName,
            userEmail = userEmail,
            parkingLots = parkingLots,
            selectedParkingLot = selectedParkingLot,
            onLogout = onLogout
        )
        return
    }

    UserProfileTab(
        modifier = modifier,
        userFullName = userFullName,
        userEmail = userEmail,
        selectedParkingLot = selectedParkingLot,
        activeReservation = activeReservation,
        activeReservationSpotNumber = activeReservationSpotNumber,
        activeReservationParkingLotName = activeReservationParkingLotName,
        reservationHistory = reservationHistory,
        isLoadingReservationHistory = isLoadingReservationHistory,
        isUpdatingFullName = isUpdatingFullName,
        isOccupying = isOccupying,
        isReleasing = isReleasing,
        onUpdateFullName = onUpdateFullName,
        onOccupyClick = onOccupyClick,
        onReleaseClick = onReleaseClick,
        onReservationExpired = onReservationExpired,
        onLogout = onLogout
    )
}

@Composable
private fun AdminProfileTab(
    modifier: Modifier = Modifier,
    userFullName: String?,
    userEmail: String?,
    parkingLots: List<ParkingLot>,
    selectedParkingLot: ParkingLot?,
    onLogout: () -> Unit
) {
    val totalSpots = parkingLots.sumOf { it.totalSpots }

    LazyColumn(
        modifier = modifier
            .background(ParkosBackground)
            .padding(bottom = 20.dp)
    ) {
        item {
            HeaderSection(
                title = "Perfil administrativo",
                subtitle = "Cuenta de operación ParkOs"
            )
        }

        item {
            AdminIdentityCard(
                userFullName = userFullName,
                userEmail = userEmail
            )
        }

        item {
            AdminOrganizationCard(
                parkingLotsCount = parkingLots.size,
                totalSpots = totalSpots,
                selectedParkingLot = selectedParkingLot
            )
        }

        item {
            AdminPermissionsCard()
        }

        item {
            AdminSessionCard(
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun AdminIdentityCard(
    userFullName: String?,
    userEmail: String?
) {
    val displayName = userFullName?.takeIf { it.isNotBlank() } ?: "Administrador ParkOs"
    val displayEmail = userEmail?.takeIf { it.isNotBlank() } ?: "Correo no disponible"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(ParkosSoftOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitial(displayName),
                        color = ParkosOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = displayEmail,
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
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

            Box(
                modifier = Modifier
                    .background(
                        color = ParkosOrange,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Administrador",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Esta cuenta está habilitada para operar estacionamientos, editar planos y gestionar cajones. No puede reservar espacios como consumidor.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AdminOrganizationCard(
    parkingLotsCount: Int,
    totalSpots: Int,
    selectedParkingLot: ParkingLot?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Alcance administrativo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Esta cuenta puede operar los estacionamientos vinculados a su organización.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminProfileInfoRow(
                title = "Estacionamientos vinculados",
                value = parkingLotsCount.toString()
            )

            AdminProfileInfoRow(
                title = "Cajones bajo administración",
                value = totalSpots.toString()
            )

            AdminProfileInfoRow(
                title = "Estacionamiento activo",
                value = selectedParkingLot?.name ?: "Ninguno seleccionado"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ParkosSoftOrange
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFFFD8BD)
                )
            ) {
                Text(
                    text = "Para ver métricas operativas como cajones libres, ocupados o no disponibles, usa la pantalla de Inicio.",
                    modifier = Modifier.padding(14.dp),
                    color = ParkosOrange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AdminProfileInfoRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = title,
            color = ParkosMutedText,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminPermissionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = ParkosSoftOrange),
        border = BorderStroke(1.dp, Color(0xFFFFD8BD))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Permisos administrativos",
                color = ParkosOrange,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminPermissionRow("Editar plano del estacionamiento")
            AdminPermissionRow("Crear y mover cajones en mantenimiento")
            AdminPermissionRow("Agregar muros, entradas, casetas y columnas")
            AdminPermissionRow("Poner cajones en mantenimiento")
            AdminPermissionRow("No puede reservar cajones")
        }
    }
}

@Composable
private fun AdminPermissionRow(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ParkosOrange, CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = ParkosOrange,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AdminSessionCard(
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cierra sesión cuando termines de operar la consola administrativa.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ParkosOrange)
            ) {
                Text("Cerrar sesión", color = ParkosOrange)
            }
        }
    }
}

@Composable
private fun UserProfileTab(
    modifier: Modifier = Modifier,
    userFullName: String?,
    userEmail: String?,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    reservationHistory: List<ReservationHistoryItem>,
    isLoadingReservationHistory: Boolean,
    isUpdatingFullName: Boolean,
    isOccupying: Boolean,
    isReleasing: Boolean,
    onUpdateFullName: (String) -> Unit,
    onOccupyClick: () -> Unit,
    onReleaseClick: () -> Unit,
    onReservationExpired: () -> Unit,
    onLogout: () -> Unit
) {
    var showEditNameDialog by remember {
        mutableStateOf(false)
    }

    if (showEditNameDialog) {
        EditFullNameDialog(
            currentName = userFullName.orEmpty(),
            isSaving = isUpdatingFullName,
            onDismiss = {
                if (!isUpdatingFullName) {
                    showEditNameDialog = false
                }
            },
            onSave = { newName ->
                onUpdateFullName(newName)
                showEditNameDialog = false
            }
        )
    }

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
                userFullName = userFullName,
                userEmail = userEmail,
                selectedParkingLot = selectedParkingLot,
                activeReservation = activeReservation,
                activeReservationSpotNumber = activeReservationSpotNumber,
                onSettingsClick = {
                    showEditNameDialog = true
                }
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
            UserReservationHistoryCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                history = reservationHistory,
                isLoading = isLoadingReservationHistory
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
private fun ProfileHeroCard(
    userFullName: String?,
    userEmail: String?,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    onSettingsClick: () -> Unit
) {
    val displayName = userFullName?.takeIf { it.isNotBlank() } ?: "Usuario ParkOs"
    val displayEmail = userEmail?.takeIf { it.isNotBlank() } ?: "Correo no disponible"

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
                        text = getInitial(displayName),
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
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = displayEmail,
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(ParkosSoftOrange, CircleShape)
                        .clickable(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Editar nombre",
                        tint = ParkosOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFBF7)
                ),
                border = BorderStroke(1.dp, ParkosBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ProfileInfoRow(
                        title = "Reservación actual",
                        value = when {
                            activeReservation?.status.equals("reserved", ignoreCase = true) -> "Pendiente"
                            activeReservation?.status.equals("active", ignoreCase = true) -> "En uso"
                            else -> "Sin reservación activa"
                        }
                    )

                    ProfileInfoRow(
                        title = "Estacionamiento seleccionado",
                        value = selectedParkingLot?.name ?: "Ninguno seleccionado"
                    )

                    ProfileInfoRow(
                        title = "Cajón",
                        value = activeReservationSpotNumber ?: "--"
                    )
                }
            }
        }
    }
}

@Composable
private fun UserReservationHistoryCard(
    modifier: Modifier = Modifier,
    history: List<ReservationHistoryItem>,
    isLoading: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Historial reciente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tus últimos cajones reservados o utilizados.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Cargando historial...",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                history.isEmpty() -> {
                    Text(
                        text = "Aún no tienes reservaciones registradas.",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        history.forEach { item ->
                            ReservationHistoryRow(
                                item = item
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationHistoryRow(
    item: ReservationHistoryItem
) {
    val displayDate = formatHistoryDate(
        item.occupiedAt ?: item.startTime ?: item.createdAt
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFBF7)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${item.spotNumber} · ${item.parkingLotName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.parkingLotAddress,
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = reservationHistoryStatusBackground(item.status),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = reservationHistoryStatusLabel(item.status),
                        color = reservationHistoryStatusTextColor(item.status),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = displayDate,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = title,
            color = ParkosMutedText,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EditFullNameDialog(
    currentName: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fullName by remember(currentName) {
        mutableStateOf(currentName)
    }

    val cleanName = fullName.trim()

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Editar nombre",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Este nombre aparecerá en tu perfil ParkOs.",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre completo")
                    },
                    singleLine = true,
                    enabled = !isSaving
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving && cleanName.length >= 2,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    onSave(cleanName)
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
                Text("Cancelar")
            }
        }
    )
}

private fun reservationHistoryStatusLabel(status: String): String {
    return when (status.lowercase()) {
        "reserved" -> "Reservado"
        "active" -> "En uso"
        "completed" -> "Completado"
        "expired" -> "Expirado"
        "cancelled", "canceled" -> "Cancelado"
        else -> status
    }
}

private fun reservationHistoryStatusBackground(status: String): Color {
    return when (status.lowercase()) {
        "reserved" -> ParkosSoftYellow
        "active" -> ParkosSoftGreen
        "completed" -> ParkosSoftBlue
        "expired" -> ParkosSoftMaintenance
        "cancelled", "canceled" -> ParkosSoftRed
        else -> ParkosSoftOrange
    }
}

private fun reservationHistoryStatusTextColor(status: String): Color {
    return when (status.lowercase()) {
        "reserved" -> Color(0xFF7A5700)
        "active" -> Color(0xFF1B5E20)
        "completed" -> Color(0xFF144D84)
        "expired" -> ParkosMaintenanceText
        "cancelled", "canceled" -> Color(0xFF8E1B1B)
        else -> ParkosOrange
    }
}

private fun formatHistoryDate(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Fecha no disponible"
    }

    return try {
        val normalized = normalizeProfileHistoryTimestamp(value)

        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            Locale.US
        )

        val outputFormat = SimpleDateFormat(
            "dd MMM yyyy · HH:mm",
            Locale("es", "MX")
        )

        val date = inputFormat.parse(normalized)

        if (date != null) {
            outputFormat.format(date)
        } else {
            "Fecha no disponible"
        }
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}

private fun normalizeProfileHistoryTimestamp(value: String): String {
    val cleanValue = value.trim().replace("Z", "+00:00")

    val fractionalRegex = Regex("(\\.\\d{1,9})([+-]\\d{2}:\\d{2})$")

    val fixedFractional = fractionalRegex.replace(cleanValue) { match ->
        val fraction = match.groupValues[1]
            .removePrefix(".")
            .padEnd(3, '0')
            .take(3)

        ".$fraction${match.groupValues[2]}"
    }

    val hasFraction = Regex("\\.\\d{3}[+-]\\d{2}:\\d{2}$").containsMatchIn(fixedFractional)

    if (hasFraction) {
        return fixedFractional
    }

    val timezoneRegex = Regex("([+-]\\d{2}:\\d{2})$")

    val withMilliseconds = timezoneRegex.replace(fixedFractional) { match ->
        ".000${match.groupValues[1]}"
    }

    return if (timezoneRegex.containsMatchIn(withMilliseconds)) {
        withMilliseconds
    } else {
        "$withMilliseconds.000+00:00"
    }
}

@Composable
internal fun SummaryMiniCard(
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