package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.ui.theme.ParkosOrange
import com.parkos.app.domain.model.Reservation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import com.parkos.app.domain.model.StaffMember
import com.parkos.app.domain.model.IncidentReport


@Composable
internal fun HomeTab(
    modifier: Modifier = Modifier,
    role: String?,
    userFullName: String?,
    userEmail: String?,
    parkingLots: List<ParkingLot>,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    orgStaffMembers: List<StaffMember>,
    isLoadingOrgStaffMembers: Boolean,
    isRevokingStaffAccess: Boolean,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onGoToMap: () -> Unit,
    staffStatus: String?,
    incidentReports: List<IncidentReport>,
    isLoadingIncidentReports: Boolean,
    isCreatingIncidentReport: Boolean,
    onCreateIncidentReportClick: () -> Unit,
    onRevokeStaffAccess: (StaffMember) -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
){
    if (role == "admin") {
        AdminHomeTab(
            modifier = modifier,
            parkingLots = parkingLots,
            orgStaffMembers = orgStaffMembers,
            isLoadingOrgStaffMembers = isLoadingOrgStaffMembers,
            isRevokingStaffAccess = isRevokingStaffAccess,
            isLoading = isLoading,
            error = error,
            onRetry = onRetry,
            onRevokeStaffAccess = onRevokeStaffAccess,
            onSelectParkingLot = onSelectParkingLot
        )
        return
    }

    if (role == "collaborator") {
        StaffHomeTab(
            modifier = modifier,
            staffStatus = staffStatus,
            parkingLots = parkingLots,
            selectedParkingLot = selectedParkingLot,
            incidentReports = incidentReports,
            isLoadingIncidentReports = isLoadingIncidentReports,
            isCreatingIncidentReport = isCreatingIncidentReport,
            isLoading = isLoading,
            error = error,
            onRetry = onRetry,
            onCreateIncidentReportClick = onCreateIncidentReportClick,
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
                ConsumerQuickStatusCard(
                    parkingLots = parkingLots,
                    selectedParkingLot = selectedParkingLot,
                    activeReservation = activeReservation
                )
            }

            item {
                ConsumerReservationPreviewCard(
                    activeReservation = activeReservation,
                    activeReservationSpotNumber = activeReservationSpotNumber,
                    activeReservationParkingLotName = activeReservationParkingLotName,
                    onGoToMap = onGoToMap
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
                            ConsumerParkingLotCards(
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
private fun ConsumerQuickStatusCard(
    parkingLots: List<ParkingLot>,
    selectedParkingLot: ParkingLot?,
    activeReservation: Reservation?
) {
    val selectedAvailableSpots = selectedParkingLot?.availableSpots

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(18.dp),
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
                .padding(vertical = 16.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConsumerStatusMiniItem(
                modifier = Modifier.weight(1f),
                title = "Reserva",
                value = if (activeReservation != null) "Activa" else "Ninguna"
            )

            HomeVerticalDivider()

            ConsumerStatusMiniItem(
                modifier = Modifier.weight(1f),
                title = "Espacios",
                value = parkingLots.size.toString()
            )

            HomeVerticalDivider()

            ConsumerStatusMiniItem(
                modifier = Modifier.weight(1f),
                title = "Libres aquí",
                value = selectedAvailableSpots?.toString() ?: "Elige"
            )
        }
    }
}

@Composable
private fun ConsumerStatusMiniItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = Color(0xFF777777),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ConsumerReservationPreviewCard(
    activeReservation: Reservation?,
    activeReservationSpotNumber: String?,
    activeReservationParkingLotName: String?,
    onGoToMap: () -> Unit
) {
    val hasReservation = activeReservation != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp)
            .padding(top = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasReservation) ParkosSoftYellow else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasReservation) Color(0xFFE8C96A) else Color(0xFFFFD8BD)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = when {
                    activeReservation?.status.equals("reserved", ignoreCase = true) -> "Reservación pendiente"
                    activeReservation?.status.equals("active", ignoreCase = true) -> "Cajón ocupado"
                    else -> "Listo para estacionarte"
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = if (hasReservation) Color(0xFF7A5700) else Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hasReservation) {
                    "${activeReservationSpotNumber ?: "Cajón"} · ${activeReservationParkingLotName ?: "ParkOs"}"
                } else {
                    "Busca un estacionamiento disponible y reserva un cajón cuando estés por llegar."
                },
                color = if (hasReservation) Color(0xFF7A5700) else ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            if (hasReservation) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onGoToMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
private fun ConsumerParkingLotCards(
    parkingLots: List<ParkingLot>,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        parkingLots.forEach { parkingLot ->
            ConsumerParkingLotCard(
                parkingLot = parkingLot,
                onClick = {
                    onSelectParkingLot(parkingLot)
                }
            )
        }
    }
}

@Composable
private fun ConsumerParkingLotCard(
    parkingLot: ParkingLot,
    onClick: () -> Unit
) {
    val totalSpots = parkingLot.totalSpots.coerceAtLeast(0)
    val availableSpots = parkingLot.availableSpots.coerceAtLeast(0)

    val availableRate = if (totalSpots > 0) {
        availableSpots.toFloat() / totalSpots.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = parkingLot.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = parkingLot.address,
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
                            color = ParkosSoftOrange,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "$availableSpots/$totalSpots",
                        color = ParkosOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        color = Color(0xFFFFEFE5),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                if (availableRate > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(availableRate)
                            .height(10.dp)
                            .background(
                                color = ParkosOrange,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                )
            ) {
                Text("Ver mapa")
            }
        }
    }
}
@Composable
private fun AdminHomeTab(
    modifier: Modifier = Modifier,
    parkingLots: List<ParkingLot>,
    orgStaffMembers: List<StaffMember>,
    isLoadingOrgStaffMembers: Boolean,
    isRevokingStaffAccess: Boolean,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onRevokeStaffAccess: (StaffMember) -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Panel de administración",
            subtitle = "Control operativo de tu organización"
        )

        when {
            isLoading -> {
                CenteredMessage(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = ParkosOrange)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Cargando información...")
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
                        AdminOverviewCard(
                            parkingLots = parkingLots,
                            onOpenMainParkingLot = {
                                onSelectParkingLot(parkingLots.first())
                            }
                        )
                    }

                    item {
                        AdminOccupancyChartCard(
                            parkingLots = parkingLots
                        )
                    }

                    item {
                        AdminStaffMembersCard(
                            staffMembers = orgStaffMembers,
                            isLoading = isLoadingOrgStaffMembers,
                            isRevoking = isRevokingStaffAccess,
                            onRevokeStaffAccess = onRevokeStaffAccess
                        )
                    }

                    item {
                        AdminHelpCard()
                    }

                    item {
                        Text(
                            text = "Estacionamientos de tu organización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    items(parkingLots) { parkingLot ->
                        OrganizationParkingLotCard(
                            role = "admin",
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
private fun AdminOverviewCard(
    parkingLots: List<ParkingLot>,
    onOpenMainParkingLot: () -> Unit
) {
    val totalLots = parkingLots.size
    val totalSpots = parkingLots.sumOf { it.totalSpots }
    val availableSpots = parkingLots.sumOf { it.availableSpots }
    val unavailableSpots = (totalSpots - availableSpots).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Resumen operativo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Vista rápida del estado general de tus estacionamientos.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "ParkOs",
                    value = totalLots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Libres",
                    value = availableSpots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "No libres",
                    value = unavailableSpots.toString()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Cajones",
                    value = totalSpots.toString()
                )

                SummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Acceso",
                    value = "Admin"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenMainParkingLot,
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
private fun AdminOccupancyChartCard(
    parkingLots: List<ParkingLot>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Ocupación actual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Comparación de cajones no libres por estacionamiento.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (parkingLots.isEmpty()) {
                Text(
                    text = "No hay estacionamientos para mostrar.",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    parkingLots.forEach { parkingLot ->
                        AdminOccupancyBarRow(
                            parkingLot = parkingLot
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun AdminStaffMembersCard(
    staffMembers: List<StaffMember>,
    isLoading: Boolean,
    isRevoking: Boolean,
    onRevokeStaffAccess: (StaffMember) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Personal staff activo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Colaboradores aprobados para operar dentro de tu organización.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Cargando personal staff...",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                staffMembers.isEmpty() -> {
                    Text(
                        text = "Todavía no hay colaboradores aprobados.",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        staffMembers.forEach { member ->
                            AdminStaffMemberItem(
                                member = member,
                                isRevoking = isRevoking,
                                onRevokeStaffAccess = {
                                    onRevokeStaffAccess(member)
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
private fun AdminStaffMemberItem(
    member: StaffMember,
    isRevoking: Boolean,
    onRevokeStaffAccess: () -> Unit
) {
    var showConfirmDialog by remember(member.userId) {
        mutableStateOf(false)
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isRevoking) {
                    showConfirmDialog = false
                }
            },
            title = {
                Text(
                    text = "Quitar acceso staff",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Quieres quitar el acceso staff a ${member.fullName}? Su cuenta no será eliminada; quedará como usuario normal."
                )
            },
            confirmButton = {
                Button(
                    enabled = !isRevoking,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC94A4A)
                    ),
                    onClick = {
                        onRevokeStaffAccess()
                        showConfirmDialog = false
                    }
                ) {
                    Text(if (isRevoking) "Quitando..." else "Quitar acceso")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isRevoking,
                    onClick = {
                        showConfirmDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
            Text(
                text = member.fullName.ifBlank { "Usuario sin nombre" },
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = member.email,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = ParkosSoftOrange,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Staff aprobado",
                    color = ParkosOrange,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (member.parkingLotCount > 1) {
                    "Aplica a ${member.parkingLotCount} estacionamientos:"
                } else {
                    "Aplica a:"
                },
                color = ParkosMutedText,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = member.parkingLotNames,
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    showConfirmDialog = true
                },
                enabled = !isRevoking,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFC94A4A))
            ) {
                Text(
                    text = if (isRevoking) "Quitando acceso..." else "Quitar acceso staff",
                    color = Color(0xFFC94A4A)
                )
            }
        }
    }
}
@Composable
private fun AdminOccupancyBarRow(
    parkingLot: ParkingLot
) {
    val totalSpots = parkingLot.totalSpots.coerceAtLeast(0)
    val availableSpots = parkingLot.availableSpots.coerceAtLeast(0)
    val unavailableSpots = (totalSpots - availableSpots).coerceAtLeast(0)

    val occupancyRate = if (totalSpots > 0) {
        unavailableSpots.toFloat() / totalSpots.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    val percentage = (occupancyRate * 100).toInt()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = parkingLot.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (totalSpots > 0) {
                        "$unavailableSpots de $totalSpots no libres"
                    } else {
                        "Sin cajones registrados"
                    },
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "$percentage%",
                color = ParkosOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(
                    color = Color(0xFFFFEFE5),
                    shape = RoundedCornerShape(50)
                )
        ) {
            if (occupancyRate > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(occupancyRate)
                        .height(14.dp)
                        .background(
                            color = ParkosOrange,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

@Composable
private fun AdminHelpCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = ParkosSoftOrange
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8BD)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Modo administrador",
                fontWeight = FontWeight.Bold,
                color = ParkosOrange,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Puedes editar el plano, crear cajones, mover elementos y poner cajones en mantenimiento. Los administradores no pueden reservar cajones.",
                color = ParkosOrange,
                style = MaterialTheme.typography.bodySmall
            )
        }
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
                colors = OutlinedTextFieldDefaults.colors(
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
private fun StaffHomeTab(
    modifier: Modifier = Modifier,
    staffStatus: String?,
    parkingLots: List<ParkingLot>,
    selectedParkingLot: ParkingLot?,
    incidentReports: List<IncidentReport>,
    isLoadingIncidentReports: Boolean,
    isCreatingIncidentReport: Boolean,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onCreateIncidentReportClick: () -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ParkosBackground)
    ) {
        HeaderSection(
            title = "Inicio staff",
            subtitle = "Herramientas para colaboradores"
        )

        when {
            isLoading -> {
                CenteredMessage(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = ParkosOrange)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Cargando información...")
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

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        StaffStatusCard(
                            staffStatus = staffStatus,
                            selectedParkingLot = selectedParkingLot
                        )
                    }

                    item {
                        StaffIncidentReportActionCard(
                            staffStatus = staffStatus,
                            selectedParkingLot = selectedParkingLot,
                            isCreatingIncidentReport = isCreatingIncidentReport,
                            onCreateIncidentReportClick = onCreateIncidentReportClick
                        )
                    }

                    item {
                        StaffRecentReportsCard(
                            reports = incidentReports,
                            isLoading = isLoadingIncidentReports
                        )
                    }

                    item {
                        Text(
                            text = "Estacionamientos de tu organización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (parkingLots.isEmpty()) {
                        item {
                            EmptyHomeResultCard()
                        }
                    } else {
                        items(parkingLots) { parkingLot ->
                            OrganizationParkingLotCard(
                                role = "collaborator",
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
}

@Composable
private fun StaffStatusCard(
    staffStatus: String?,
    selectedParkingLot: ParkingLot?
) {
    val isApproved = staffStatus == "approved"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isApproved) ParkosSoftGreen else ParkosSoftYellow
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isApproved) Color(0xFFB7D8BA) else Color(0xFFE8C96A)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = when (staffStatus) {
                    "approved" -> "Acceso staff aprobado"
                    "pending" -> "Solicitud staff pendiente"
                    "rejected" -> "Solicitud staff rechazada"
                    "revoked" -> "Acceso staff revocado"
                    else -> "Estado staff no disponible"
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (isApproved) Color(0xFF1B5E20) else Color(0xFF7A5700)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (staffStatus) {
                    "approved" -> "Puedes reservar cajones staff y crear reportes de incidentes."
                    "pending" -> "Un administrador debe aprobar tu solicitud antes de usar funciones staff."
                    "rejected" -> "Tu solicitud fue rechazada. Puedes seguir usando ParkOs como usuario normal."
                    "revoked" -> "Tu acceso staff fue retirado. Puedes seguir usando ParkOs como usuario normal."
                    else -> "No se pudo determinar tu estado staff."
                },
                color = if (isApproved) Color(0xFF1B5E20) else Color(0xFF7A5700),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProfileInfoLine(
                title = "Estacionamiento activo",
                value = selectedParkingLot?.name ?: "Ninguno seleccionado"
            )
        }
    }
}

@Composable
private fun StaffIncidentReportActionCard(
    staffStatus: String?,
    selectedParkingLot: ParkingLot?,
    isCreatingIncidentReport: Boolean,
    onCreateIncidentReportClick: () -> Unit
) {
    val canCreateReport = staffStatus == "approved" && selectedParkingLot != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Reporte de incidente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (selectedParkingLot == null) {
                    "Selecciona un estacionamiento para crear reportes relacionados con ese lugar."
                } else {
                    "Crea un reporte preventivo e informativo para compartirlo con un superior."
                },
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onCreateIncidentReportClick,
                enabled = canCreateReport && !isCreatingIncidentReport,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                )
            ) {
                Text(
                    when {
                        isCreatingIncidentReport -> "Guardando..."
                        selectedParkingLot == null -> "Selecciona estacionamiento"
                        else -> "Crear reporte"
                    }
                )
            }
        }
    }
}

@Composable
private fun StaffRecentReportsCard(
    reports: List<IncidentReport>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                text = "Mis reportes recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Últimos reportes creados desde tu cuenta staff.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Cargando reportes...",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                reports.isEmpty() -> {
                    Text(
                        text = "Aún no has creado reportes.",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        reports.take(5).forEach { report ->
                            StaffReportMiniItem(
                                report = report
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffReportMiniItem(
    report: IncidentReport
) {
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
            Text(
                text = report.reportNumber,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${incidentTypeToSpanish(report.incidentType)} · ${report.vehiclePlate}",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            if (!report.spotNumber.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Casilla: ${report.spotNumber}",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoLine(
    title: String,
    value: String
) {
    Column {
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

private fun incidentTypeToSpanish(type: String): String {
    return when (type) {
        "robo" -> "Robo"
        "danio_vehiculo" -> "Daño a vehículo"
        "danio_infraestructura" -> "Daño a infraestructura"
        "agresion" -> "Agresión"
        "actividad_sospechosa" -> "Actividad sospechosa"
        "vehiculo_mal_estacionado" -> "Vehículo mal estacionado"
        "otro" -> "Otro"
        else -> type
    }
}