package com.parkos.app.ui.map


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.parkos.app.domain.model.ParkingFloor
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.domain.model.Reservation
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun ParkingLayoutGrid(
    role: String?,
    floors: List<ParkingFloor>,
    spots: List<ParkingSpot>,
    layoutElements: List<ParkingLayoutElement>,
    activeReservation: Reservation?,
    movingLayoutElement: ParkingLayoutElement?,
    isAdminMovingLayoutElement: Boolean,
    onAdminMoveLayoutElementToCell: (String, Int, Int) -> Unit,
    isLoadingLayout: Boolean,
    onReserveSpotClick: (ParkingSpot) -> Unit,
    onAdminEditSpotClick: (ParkingSpot) -> Unit,
    onAdminCreateSpotAtCell: (String, Int, Int) -> Unit,
    onAdminLayoutElementClick: (ParkingLayoutElement) -> Unit
) {
    val firstFloor = floors.firstOrNull()

    var selectedFloorId by remember(floors) {
        mutableStateOf(firstFloor?.id.orEmpty())
    }

    val selectedFloor = floors.firstOrNull { it.id == selectedFloorId }
        ?: firstFloor

    if (selectedFloor == null) {
        CenteredMessage(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Text("No hay piso registrado para este estacionamiento.")
        }
        return
    }

    val elementsForFloor = layoutElements.filter {
        it.floorId == selectedFloor.id
    }

    val elementsByPosition = elementsForFloor.associateBy {
        it.rowIndex to it.colIndex
    }

    val spotsById = spots.associateBy {
        it.id
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Plano del estacionamiento",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${selectedFloor.name} · ${selectedFloor.rows} filas x ${selectedFloor.cols} columnas",
                        color = ParkosMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isLoadingLayout) {
                    CircularProgressIndicator(
                        color = ParkosOrange,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            if (floors.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    floors.forEach { floor ->
                        OutlinedButton(
                            onClick = {
                                selectedFloorId = floor.id
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (selectedFloorId == floor.id) 2.dp else 1.dp,
                                color = if (selectedFloorId == floor.id) {
                                    ParkosOrange
                                } else {
                                    Color.LightGray
                                }
                            )
                        ) {
                            Text(
                                text = floor.name,
                                color = if (selectedFloorId == floor.id) {
                                    ParkosOrange
                                } else {
                                    Color.DarkGray
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0 until selectedFloor.rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until selectedFloor.cols) {
                                val element = elementsByPosition[row to col]
                                val spot = element?.parkingSpotId?.let { spotId ->
                                    spotsById[spotId]
                                }

                                ParkingLayoutCell(
                                    role = role,
                                    floorId = selectedFloor.id,
                                    row = row,
                                    col = col,
                                    element = element,
                                    spot = spot,
                                    activeReservation = activeReservation,
                                    movingLayoutElement = movingLayoutElement,
                                    isAdminMovingLayoutElement = isAdminMovingLayoutElement,
                                    onAdminMoveLayoutElementToCell = onAdminMoveLayoutElementToCell,
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Puedes deslizar horizontalmente para ver todas las columnas.",
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ParkingLayoutCell(
    role: String?,
    floorId: String,
    row: Int,
    col: Int,
    element: ParkingLayoutElement?,
    spot: ParkingSpot?,
    activeReservation: Reservation?,
    movingLayoutElement: ParkingLayoutElement?,
    isAdminMovingLayoutElement: Boolean,
    onAdminMoveLayoutElementToCell: (String, Int, Int) -> Unit,
    onReserveSpotClick: (ParkingSpot) -> Unit,
    onAdminEditSpotClick: (ParkingSpot) -> Unit,
    onAdminCreateSpotAtCell: (String, Int, Int) -> Unit,
    onAdminLayoutElementClick: (ParkingLayoutElement) -> Unit
) {
    if (spot != null) {
        ParkingSpotMiniCard(
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
        return
    }

    if (element != null) {
        LayoutElementMiniCard(
            element = element,
            canEdit = role == "admin" && element.parkingSpotId == null,
            onClick = {
                if (role == "admin" && element.parkingSpotId == null) {
                    onAdminLayoutElementClick(element)
                }
            }
        )
        return
    }

    EmptyLayoutCell(
        row = row,
        col = col,
        canCreate = role == "admin",
        isMoveTarget = movingLayoutElement != null,
        isBusy = isAdminMovingLayoutElement,
        onClick = {
            if (movingLayoutElement != null) {
                onAdminMoveLayoutElementToCell(floorId, row, col)
            } else {
                onAdminCreateSpotAtCell(floorId, row, col)
            }
        }
    )
}

@Composable
private fun ParkingSpotMiniCard(
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
            .size(width = 72.dp, height = 82.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isActiveUserSpot) 3.dp else 1.dp,
                color = if (isActiveUserSpot) ParkosOrange else borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = role == "admin" || canReserveSpot(role, spot, activeReservation),
                onClick = onClick
            )
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = spot.spotNumber,
            fontWeight = FontWeight.Bold,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = statusToCardLabel(spot.status),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = typeToCardLabel(spot.type),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyLayoutCell(
    row: Int,
    col: Int,
    canCreate: Boolean,
    isMoveTarget: Boolean,
    isBusy: Boolean,
    onClick: () -> Unit
) {
    val enabled = canCreate && !isBusy
    val borderColor = when {
        isMoveTarget -> ParkosOrange
        canCreate -> Color(0xFFFFD8BD)
        else -> Color(0xFFE1E1E1)
    }

    val backgroundColor = when {
        isMoveTarget -> ParkosSoftOrange
        canCreate -> Color(0xFFFFFBF7)
        else -> Color(0xFFF7F7F7)
    }

    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 82.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isMoveTarget) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$row,$col",
                color = if (canCreate) ParkosOrange else Color(0xFFB0B0B0),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp
            )

            if (isMoveTarget) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Mover",
                    color = ParkosOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            } else if (canCreate) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "+",
                    color = ParkosOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun LayoutElementMiniCard(
    element: ParkingLayoutElement,
    canEdit: Boolean,
    onClick: () -> Unit
) {
    val label = element.label?.takeIf { it.isNotBlank() }
        ?: layoutElementShortLabel(element.elementType)

    val backgroundColor = layoutElementBackgroundColor(element.elementType)
    val borderColor = layoutElementBorderColor(element.elementType)
    val textColor = layoutElementTextColor(element.elementType)

    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 82.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = canEdit,
                onClick = onClick
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
private fun layoutElementShortLabel(type: String): String {
    return when (type) {
        "wall" -> "Muro"
        "pillar" -> "Col."
        "barrier" -> "Barrera"
        "cabin" -> "Caseta"
        "entrance" -> "Entrada"
        "stairs" -> "Esc."
        "reserved_area" -> "Área"
        else -> type
    }
}

private fun layoutElementBackgroundColor(type: String): Color {
    return when (type) {
        "wall" -> Color(0xFFD4D4D4)
        "pillar" -> Color(0xFFB8B8B8)
        "barrier" -> Color(0xFFFFD7B5)
        "cabin" -> Color(0xFFE8D8C3)
        "entrance" -> Color(0xFFDDF3F2)
        "stairs" -> Color(0xFFDDE5EC)
        "reserved_area" -> Color(0xFFEAD8B8)
        else -> Color(0xFFE5E2DC)
    }
}

private fun layoutElementBorderColor(type: String): Color {
    return when (type) {
        "wall" -> Color(0xFF555555)
        "pillar" -> Color(0xFF3F3F3F)
        "barrier" -> Color(0xFFB85C19)
        "cabin" -> Color(0xFF8A6A45)
        "entrance" -> Color(0xFF278486)
        "stairs" -> Color(0xFF5B7184)
        "reserved_area" -> Color(0xFF8A6A35)
        else -> Color(0xFF8A8378)
    }
}

private fun layoutElementTextColor(type: String): Color {
    return when (type) {
        "wall" -> Color(0xFF333333)
        "pillar" -> Color(0xFF252525)
        "barrier" -> Color(0xFF7A3500)
        "cabin" -> Color(0xFF5E4528)
        "entrance" -> Color(0xFF176063)
        "stairs" -> Color(0xFF34495A)
        "reserved_area" -> Color(0xFF5E4528)
        else -> Color(0xFF4E4A45)
    }
}