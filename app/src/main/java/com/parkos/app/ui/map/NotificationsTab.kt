package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun NotificationsTab(
    modifier: Modifier = Modifier,
    role: String?,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    layoutElements: List<ParkingLayoutElement>,
    onOpenMap: () -> Unit
) {
    if (role == "admin") {
        AdminNotificationsTab(
            modifier = modifier,
            selectedParkingLot = selectedParkingLot,
            spots = spots,
            layoutElements = layoutElements,
            onOpenMap = onOpenMap
        )
        return
    }

    UserNotificationsTab(
        modifier = modifier
    )
}

@Composable
private fun AdminNotificationsTab(
    modifier: Modifier = Modifier,
    selectedParkingLot: ParkingLot?,
    spots: List<ParkingSpot>,
    layoutElements: List<ParkingLayoutElement>,
    onOpenMap: () -> Unit
) {
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
                    text = "Los consumidores reservan cajones normales. Los colaboradores reservan cajones staff. El administrador solo visualiza y opera el estacionamiento."
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
                color = ParkosMutedText,
                textAlign = TextAlign.Start
            )
        }
    }
}