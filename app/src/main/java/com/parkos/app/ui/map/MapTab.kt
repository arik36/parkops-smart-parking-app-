package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkos.app.domain.model.ParkingFloor
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun MapTab(
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
    onAdminLayoutElementClick: (ParkingLayoutElement) -> Unit,
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
                            onAdminCreateSpotAtCell = onAdminCreateSpotAtCell,
                            onAdminLayoutElementClick = onAdminLayoutElementClick
                        )
                    }
                }
            }
        }
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