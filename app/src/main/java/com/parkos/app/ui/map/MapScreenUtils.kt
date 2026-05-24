package com.parkos.app.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun CenteredMessage(
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

internal fun canReserveSpot(
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

internal fun getSpotBackgroundColor(spot: ParkingSpot): Color {
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

internal fun getSpotBorderColor(spot: ParkingSpot): Color {
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

internal fun getSpotTextColor(spot: ParkingSpot): Color {
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

internal fun calculateRemainingSeconds(expiresAt: String?): Long {
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

internal fun normalizeSupabaseTimestamp(value: String): String {
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

internal fun formatRemainingTime(seconds: Long): String {
    val safeSeconds = if (seconds < 0L) 0L else seconds
    val minutesPart = safeSeconds / 60
    val secondsPart = safeSeconds % 60

    return "%02d:%02d".format(minutesPart, secondsPart)
}

internal fun roleToDisplay(role: String?): String {
    return when (role) {
        "admin" -> "Administrador"
        "collaborator" -> "Colaborador"
        "consumer" -> "Consumidor"
        else -> "Cargando..."
    }
}

internal fun shortRole(role: String?): String {
    return when (role) {
        "admin" -> "Admin"
        "collaborator" -> "Colab."
        "consumer" -> "User"
        else -> "--"
    }
}

internal fun homeSubtitle(role: String?): String {
    return when (role) {
        "admin" -> "Consulta los estacionamientos de tu organización."
        "collaborator" -> "Selecciona tu estacionamiento para trabajar."
        else -> "Busca y elige dónde quieres estacionarte."
    }
}

internal fun getInitial(fullName: String?): String {
    return fullName
        ?.trim()
        ?.firstOrNull()
        ?.uppercase()
        ?: "P"
}

internal fun statusToSpanish(status: String): String {
    return when (status.lowercase()) {
        "available" -> "Disponible"
        "reserved" -> "Reservado"
        "occupied" -> "Ocupado"
        "maintenance" -> "Mantenimiento"
        else -> status
    }
}

internal fun typeToSpanish(type: String): String {
    return when (type.lowercase()) {
        "normal" -> "Normal"
        "disabled" -> "Discapacitado"
        "electric" -> "Eléctrico"
        "staff" -> "Staff"
        else -> type
    }
}

internal fun statusToCardLabel(status: String): String {
    return when (status.lowercase()) {
        "available" -> "Libre"
        "reserved" -> "Reservado"
        "occupied" -> "Ocupado"
        "maintenance" -> "Mant."
        else -> status
    }
}

internal fun typeToCardLabel(type: String): String {
    return when (type.lowercase()) {
        "normal" -> "Normal"
        "disabled" -> "Discap."
        "electric" -> "Eléctrico"
        "staff" -> "Staff"
        else -> type
    }
}