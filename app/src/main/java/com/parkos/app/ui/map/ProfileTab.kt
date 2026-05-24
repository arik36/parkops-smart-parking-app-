package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun ProfileTab(
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