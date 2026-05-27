package com.parkos.app.ui.map


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.domain.model.StaffRequest
import com.parkos.app.ui.theme.*

@Composable
internal fun NotificationsTab(
    modifier: Modifier = Modifier,
    role: String?,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    layoutElements: List<ParkingLayoutElement>,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    pendingStaffRequests: List<StaffRequest>,
    isLoadingPendingStaffRequests: Boolean,
    isResolvingStaffRequest: Boolean,
    onOpenMap: () -> Unit,
    onGoToParkingLots: () -> Unit,
    onResolveStaffRequest: (StaffRequest, String) -> Unit
) {
    if (role == "admin") {
        AdminNotificationsTab(
            modifier = modifier,
            selectedParkingLot = selectedParkingLot,
            spots = spots,
            layoutElements = layoutElements,
            pendingStaffRequests = pendingStaffRequests,
            isLoadingPendingStaffRequests = isLoadingPendingStaffRequests,
            isResolvingStaffRequest = isResolvingStaffRequest,
            onResolveStaffRequest = onResolveStaffRequest,
            onOpenMap = onOpenMap
        )
        return
    }

    UserNotificationsTab(
        modifier = modifier,
        selectedParkingLot = selectedParkingLot,
        spots = spots,
        activeReservation = activeReservation,
        activeReservationSpotNumber = activeReservationSpotNumber,
        activeReservationParkingLotName = activeReservationParkingLotName,
        onOpenMap = onOpenMap,
        onGoToParkingLots = onGoToParkingLots
    )
}

@Composable
private fun AdminNotificationsTab(
    modifier: Modifier = Modifier,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    layoutElements: List<ParkingLayoutElement>,
    pendingStaffRequests: List<StaffRequest>,
    isLoadingPendingStaffRequests: Boolean,
    isResolvingStaffRequest: Boolean,
    onOpenMap: () -> Unit,
    onResolveStaffRequest: (StaffRequest, String) -> Unit
){
    val availableSpots = spots.count { it.status.equals("available", ignoreCase = true) }
    val reservedSpots = spots.count { it.status.equals("reserved", ignoreCase = true) }
    val occupiedSpots = spots.count { it.status.equals("occupied", ignoreCase = true) }
    val maintenanceSpots = spots.filter { it.status.equals("maintenance", ignoreCase = true) }

    val visualElements = layoutElements.filter { it.parkingSpotId == null }

    Column(
        modifier = modifier.background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Avisos administrativos",
            subtitle = selectedParkingLot?.name ?: "Alertas operativas"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AdminStatusOverviewCard(
                    availableSpots = availableSpots,
                    reservedSpots = reservedSpots,
                    occupiedSpots = occupiedSpots,
                    maintenanceCount = maintenanceSpots.size
                )
            }
            item {
                AdminPendingStaffRequestsCard(
                    requests = pendingStaffRequests,
                    isLoading = isLoadingPendingStaffRequests,
                    isResolving = isResolvingStaffRequest,
                    onResolveStaffRequest = onResolveStaffRequest
                )
            }

            item {
                AdminMaintenanceAlertsCard(
                    maintenanceSpots = maintenanceSpots,
                    onOpenMap = onOpenMap
                )
            }

            item {
                AdminLayoutElementsCard(
                    visualElements = visualElements,
                    onOpenMap = onOpenMap
                )
            }

            item {
                AdminRulesCard()
            }
        }
    }
}

@Composable
private fun AdminStatusOverviewCard(
    availableSpots: Int,
    reservedSpots: Int,
    occupiedSpots: Int,
    maintenanceCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Estado actual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Resumen rápido del estacionamiento seleccionado.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Libres",
                    value = availableSpots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Ocupados",
                    value = occupiedSpots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Mant.",
                    value = maintenanceCount.toString()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            SummaryMiniCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Reservados",
                value = reservedSpots.toString()
            )
        }
    }
}

@Composable
private fun AdminPendingStaffRequestsCard(
    requests: List<StaffRequest>,
    isLoading: Boolean,
    isResolving: Boolean,
    onResolveStaffRequest: (StaffRequest, String) -> Unit
) {
    val hasRequests = requests.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasRequests) ParkosSoftYellow else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasRequests) Color(0xFFE8C96A) else ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Solicitudes staff",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (hasRequests) Color(0xFF7A5700) else Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Cargando solicitudes pendientes...",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                requests.isEmpty() -> {
                    Text(
                        text = "No hay solicitudes staff pendientes por ahora.",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Text(
                        text = "Revisa quién solicitó acceso como colaborador antes de aprobarlo.",
                        color = Color(0xFF7A5700),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        requests.forEach { request ->
                            AdminStaffRequestItem(
                                request = request,
                                isResolving = isResolving,
                                onApprove = {
                                    onResolveStaffRequest(request, "approve")
                                },
                                onReject = {
                                    onResolveStaffRequest(request, "reject")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStaffRequestItem(
    request: StaffRequest,
    isResolving: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE8C96A)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = request.fullName.ifBlank { "Usuario sin nombre" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = request.email,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = ParkosSoftOrange,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Pendiente de aprobación",
                    color = ParkosOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    enabled = !isResolving,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    )
                ) {
                    Text(if (isResolving) "..." else "Aprobar")
                }

                OutlinedButton(
                    onClick = onReject,
                    enabled = !isResolving,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFC94A4A))
                ) {
                    Text(
                        text = "Rechazar",
                        color = Color(0xFFC94A4A)
                    )
                }
            }
        }
    }
}


@Composable
private fun AdminMaintenanceAlertsCard(
    maintenanceSpots: List<ParkingSpot>,
    onOpenMap: () -> Unit
) {
    val hasMaintenance = maintenanceSpots.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasMaintenance) ParkosSoftMaintenance else ParkosSoftGreen
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasMaintenance) ParkosMaintenanceBorder else Color(0xFFB7D8BA)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = if (hasMaintenance) {
                    "Cajones en mantenimiento"
                } else {
                    "Sin mantenimiento activo"
                },
                fontWeight = FontWeight.Bold,
                color = if (hasMaintenance) ParkosMaintenanceText else Color(0xFF1B5E20),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (hasMaintenance) {
                Text(
                    text = "Estos cajones no están disponibles para consumidores ni colaboradores.",
                    color = ParkosMaintenanceText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                maintenanceSpots.take(6).forEach { spot ->
                    AdminAlertRow(
                        title = spot.spotNumber,
                        description = "Tipo: ${typeToSpanish(spot.type)}"
                    )
                }

                if (maintenanceSpots.size > 6) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "+ ${maintenanceSpots.size - 6} cajones más en mantenimiento",
                        color = ParkosMaintenanceText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text = "No hay cajones marcados como mantenimiento en este momento.",
                    color = Color(0xFF1B5E20),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenMap,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                )
            ) {
                Text("Abrir mapa operativo")
            }
        }
    }
}

@Composable
private fun AdminLayoutElementsCard(
    visualElements: List<ParkingLayoutElement>,
    onOpenMap: () -> Unit
) {
    val groupedElements = visualElements.groupBy { it.elementType }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Elementos del plano",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Elementos físicos que ayudan a representar el estacionamiento.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (groupedElements.isEmpty()) {
                Text(
                    text = "Aún no hay muros, entradas, casetas u otros elementos visuales.",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                groupedElements.entries
                    .sortedBy { layoutElementTypeToSpanish(it.key) }
                    .forEach { entry ->
                        AdminAlertRow(
                            title = layoutElementTypeToSpanish(entry.key),
                            description = "${entry.value.size} elemento(s)"
                        )
                    }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenMap,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                )
            ) {
                Text("Editar plano")
            }
        }
    }
}

@Composable
private fun AdminRulesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ParkosSoftOrange
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8BD)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Reglas operativas",
                color = ParkosOrange,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminRuleText("Los administradores no pueden reservar cajones.")
            AdminRuleText("Los cajones en mantenimiento no aparecen como disponibles.")
            AdminRuleText("Solo los cajones en mantenimiento pueden moverse o eliminarse.")
            AdminRuleText("Los elementos visuales no son reservables.")
        }
    }
}

@Composable
private fun AdminRuleText(
    text: String
) {
    Text(
        text = "• $text",
        color = ParkosOrange,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}

@Composable
private fun AdminAlertRow(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun UserNotificationsTab(
    modifier: Modifier = Modifier,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    onOpenMap: () -> Unit,
    onGoToParkingLots: () -> Unit
) {
    val maintenanceSpots = spots.filter {
        it.status.equals("maintenance", ignoreCase = true)
    }

    val availableSpots = spots.count {
        it.status.equals("available", ignoreCase = true)
    }

    Column(
        modifier = modifier
            .background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Avisos",
            subtitle = "Información para tu estacionamiento"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ConsumerReservationNoticeCard(
                    activeReservation = activeReservation,
                    activeReservationSpotNumber = activeReservationSpotNumber,
                    activeReservationParkingLotName = activeReservationParkingLotName,
                    onOpenMap = onOpenMap
                )
            }

            item {
                ConsumerParkingLotStatusCard(
                    selectedParkingLot = selectedParkingLot,
                    availableSpots = availableSpots,
                    maintenanceCount = maintenanceSpots.size,
                    onOpenMap = onOpenMap,
                    onGoToParkingLots = onGoToParkingLots
                )
            }

            item {
                ConsumerMaintenanceNoticeCard(
                    selectedParkingLot = selectedParkingLot,
                    maintenanceSpots = maintenanceSpots
                )
            }

            item {
                ConsumerRulesCard()
            }

            item {
                ConsumerHelpCard()
            }
        }
    }
}
@Composable
private fun ConsumerReservationNoticeCard(
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    onOpenMap: () -> Unit
) {
    val hasReservation = activeReservation != null
    val isReserved = activeReservation?.status.equals("reserved", ignoreCase = true)
    val isActive = activeReservation?.status.equals("active", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasReservation) ParkosSoftYellow else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasReservation) Color(0xFFE8C96A) else ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = when {
                    isReserved -> "Tienes una reservación pendiente"
                    isActive -> "Tienes un cajón en uso"
                    else -> "Sin reservación activa"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (hasReservation) Color(0xFF7A5700) else Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    isReserved -> "Confirma tu llegada antes de que termine el tiempo de espera."
                    isActive -> "Cuando salgas, libera tu cajón desde el mapa para que otra persona pueda usarlo."
                    else -> "Cuando reserves un cajón, aquí verás el estado de tu reservación."
                },
                color = if (hasReservation) Color(0xFF7A5700) else ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            if (hasReservation) {
                Spacer(modifier = Modifier.height(12.dp))

                ConsumerNoticeDetailRow(
                    title = "Cajón",
                    value = activeReservationSpotNumber ?: "--"
                )

                ConsumerNoticeDetailRow(
                    title = "Estacionamiento",
                    value = activeReservationParkingLotName ?: "ParkOs"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    )
                ) {
                    Text("Ver en mapa")
                }
            }
        }
    }
}

@Composable
private fun ConsumerParkingLotStatusCard(
    selectedParkingLot: ParkingLot?,
    availableSpots: Int,
    maintenanceCount: Int,
    onOpenMap: () -> Unit,
    onGoToParkingLots: () -> Unit
) {
    val hasSelectedParkingLot = selectedParkingLot != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Estacionamiento seleccionado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = selectedParkingLot?.name ?: "Aún no has elegido estacionamiento",
                color = if (hasSelectedParkingLot) Color.Black else ParkosMutedText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (hasSelectedParkingLot) FontWeight.SemiBold else FontWeight.Normal
            )

            if (hasSelectedParkingLot) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = selectedParkingLot?.address.orEmpty(),
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "Libres",
                        value = availableSpots.toString()
                    )

                    SummaryMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "Mant.",
                        value = maintenanceCount.toString()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    )
                ) {
                    Text("Abrir mapa")
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onGoToParkingLots,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkosOrange
                    )
                ) {
                    Text("Buscar estacionamiento")
                }
            }
        }
    }
}

@Composable
private fun ConsumerMaintenanceNoticeCard(
    selectedParkingLot: ParkingLot?,
    maintenanceSpots: List<ParkingSpot>
) {
    val hasSelectedParkingLot = selectedParkingLot != null
    val hasMaintenance = maintenanceSpots.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !hasSelectedParkingLot -> Color.White
                hasMaintenance -> ParkosSoftMaintenance
                else -> ParkosSoftGreen
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                !hasSelectedParkingLot -> ParkosBorder
                hasMaintenance -> ParkosMaintenanceBorder
                else -> Color(0xFFB7D8BA)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = when {
                    !hasSelectedParkingLot -> "Mantenimiento"
                    hasMaintenance -> "Cajones en mantenimiento"
                    else -> "Sin mantenimiento activo"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    !hasSelectedParkingLot -> Color.Black
                    hasMaintenance -> ParkosMaintenanceText
                    else -> Color(0xFF1B5E20)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                !hasSelectedParkingLot -> {
                    Text(
                        text = "Cuando elijas un estacionamiento, aquí verás si hay cajones bloqueados por mantenimiento.",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                hasMaintenance -> {
                    Text(
                        text = "Por seguridad, no entres en estos cajones aunque los veas libres físicamente:",
                        color = ParkosMaintenanceText,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    maintenanceSpots.take(8).forEach { spot ->
                        ConsumerNoticeDetailRow(
                            title = spot.spotNumber,
                            value = "En mantenimiento"
                        )
                    }

                    if (maintenanceSpots.size > 8) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "+ ${maintenanceSpots.size - 8} cajones más",
                            color = ParkosMaintenanceText,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                else -> {
                    Text(
                        text = "No hay cajones bloqueados por mantenimiento en este estacionamiento.",
                        color = Color(0xFF1B5E20),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsumerRulesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ParkosSoftOrange
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8BD)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Reglas rápidas",
                color = ParkosOrange,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConsumerRuleText("Puedes reservar un cajón por 5 minutos.")
            ConsumerRuleText("Al llegar, toca “Ya llegué” para confirmar tu lugar.")
            ConsumerRuleText("Al salir, toca “Estoy saliendo” para liberar el cajón.")
            ConsumerRuleText("Los cajones staff son exclusivos para colaboradores.")
        }
    }
}

@Composable
private fun ConsumerHelpCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = ParkosBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Ayuda rápida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoSmallSection(
                title = "El cajón aparece libre pero hay un coche",
                text = "Evita ocuparlo. Elige otro cajón disponible en el mapa."
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoSmallSection(
                title = "No alcancé a llegar",
                text = "Si pasan 5 minutos, la reservación expira y el cajón vuelve a estar disponible."
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoSmallSection(
                title = "El cajón está en mantenimiento",
                text = "No lo uses aunque físicamente parezca libre. Espera a que vuelva a aparecer disponible."
            )
        }
    }
}

@Composable
private fun ConsumerNoticeDetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = ParkosMutedText,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ConsumerRuleText(
    text: String
) {
    Text(
        text = "• $text",
        color = ParkosOrange,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}

@Composable
private fun InfoSmallSection(
    title: String,
    text: String
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = text,
            color = ParkosMutedText,
            style = MaterialTheme.typography.bodySmall
        )
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
                color = ParkosMutedText,
                textAlign = TextAlign.Start
            )
        }
    }
}