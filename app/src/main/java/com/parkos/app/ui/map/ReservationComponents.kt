package com.parkos.app.ui.map
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange
import kotlinx.coroutines.delay

@Composable
internal fun ActiveReservationCard(
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