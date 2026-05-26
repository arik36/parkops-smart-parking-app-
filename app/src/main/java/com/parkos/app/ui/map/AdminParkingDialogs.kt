package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingFloor
import com.parkos.app.domain.model.ParkingSpot
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun AdminEditParkingSpotDialog(
    spot: ParkingSpot,
    isSaving: Boolean,
    isDeleting: Boolean,
    isMoving: Boolean,
    canMove: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    var selectedStatus by remember(spot.id) {
        mutableStateOf(
            if (spot.status.equals("maintenance", ignoreCase = true)) {
                "maintenance"
            } else {
                "available"
            }
        )
    }

    var selectedType by remember(spot.id) {
        mutableStateOf(spot.type.lowercase())
    }

    var showDeleteConfirm by remember(spot.id) {
        mutableStateOf(false)
    }

    val isBusy = isSaving || isDeleting || isMoving

    val isBlockedByUser = spot.status.equals("occupied", ignoreCase = true) ||
            spot.status.equals("reserved", ignoreCase = true)

    val canDelete = spot.status.equals("maintenance", ignoreCase = true)

    val statusOptions = listOf(
        "available" to "Disponible",
        "maintenance" to "Mantenimiento"
    )

    val typeOptions = listOf(
        "normal" to "Normal",
        "disabled" to "Discapacitado",
        "electric" to "Eléctrico",
        "staff" to "Staff"
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = {
                if (!isBusy) {
                    showDeleteConfirm = false
                }
            },
            title = {
                Text(
                    text = "Eliminar cajón ${spot.spotNumber}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta acción eliminará el cajón del estacionamiento y del plano. Solo debe hacerse si el cajón fue creado por error o nunca debe existir físicamente."
                )
            },
            confirmButton = {
                Button(
                    enabled = !isBusy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC94A4A)
                    ),
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text(if (isDeleting) "Eliminando..." else "Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isBusy,
                    onClick = {
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )

        return
    }

    AlertDialog(
        onDismissRequest = {
            if (!isBusy) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Editar cajón ${spot.spotNumber}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Estado actual: ${statusToSpanish(spot.status)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParkosMutedText
                )

                Text(
                    text = "Tipo actual: ${typeToSpanish(spot.type)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParkosMutedText
                )

                if (isBlockedByUser) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ParkosSoftRed
                        )
                    ) {
                        Text(
                            text = "Este cajón no se puede editar ni mover porque está reservado u ocupado por un usuario.",
                            modifier = Modifier.padding(14.dp),
                            color = Color(0xFF8E1B1B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    return@Column
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Estado",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { option ->
                        AdminOptionButton(
                            label = option.second,
                            selected = selectedStatus == option.first,
                            enabled = !isBusy,
                            onClick = {
                                selectedStatus = option.first
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipo",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    typeOptions.forEach { option ->
                        AdminOptionButton(
                            label = option.second,
                            selected = selectedType == option.first,
                            enabled = !isBusy,
                            onClick = {
                                selectedType = option.first
                            }
                        )
                    }
                }

                if (canMove) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        enabled = !isBusy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParkosOrange
                        ),
                        onClick = onMove
                    ) {
                        Text(if (isMoving) "Moviendo..." else "Mover cajón")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tip: usa mantenimiento cuando el cajón tenga baches, cambios de tamaño, reparación, pintura o cualquier bloqueo operativo.",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )

                if (canDelete) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEFEF)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFFFFC4C4)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "Zona de eliminación",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E1B1B)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Solo puedes eliminar cajones en mantenimiento. Si este cajón ya tiene historial de reservaciones, el servidor impedirá eliminarlo.",
                                color = Color(0xFF8E1B1B),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                enabled = !isBusy,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFC94A4A)
                                ),
                                onClick = {
                                    showDeleteConfirm = true
                                }
                            ) {
                                Text("Eliminar cajón")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isBusy && !isBlockedByUser,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    onSave(selectedStatus, selectedType)
                }
            ) {
                Text(
                    when {
                        isSaving -> "Guardando..."
                        isDeleting -> "Eliminando..."
                        isMoving -> "Moviendo..."
                        else -> "Guardar"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isBusy,
                onClick = onDismiss
            ) {
                Text(if (isBlockedByUser) "Entendido" else "Cancelar")
            }
        }
    )
}
@Composable
internal fun AdminCreateParkingSpotDialog(
    floors: List<ParkingFloor>,
    initialTarget: AdminCreateSpotTarget?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onCreate: (
        floorId: String,
        spotNumber: String,
        type: String,
        rowIndex: Int,
        colIndex: Int,
        widthM: Double?,
        heightM: Double?
    ) -> Unit
) {
    val firstFloor = floors.firstOrNull()
    val lockedLocation = initialTarget != null

    var selectedFloorId by remember(floors, initialTarget) {
        mutableStateOf(initialTarget?.floorId ?: firstFloor?.id.orEmpty())
    }

    var spotNumber by remember {
        mutableStateOf("")
    }

    var selectedType by remember {
        mutableStateOf("normal")
    }

    var rowIndexText by remember(initialTarget) {
        mutableStateOf(initialTarget?.rowIndex?.toString().orEmpty())
    }

    var colIndexText by remember(initialTarget) {
        mutableStateOf(initialTarget?.colIndex?.toString().orEmpty())
    }

    var widthText by remember {
        mutableStateOf("")
    }

    var heightText by remember {
        mutableStateOf("")
    }

    var localError by remember {
        mutableStateOf<String?>(null)
    }

    val selectedFloor = floors.firstOrNull { it.id == selectedFloorId }

    val typeOptions = listOf(
        "normal" to "Normal",
        "disabled" to "Discapacitado",
        "electric" to "Eléctrico",
        "staff" to "Staff"
    )

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Agregar cajón",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "El cajón nuevo se creará en mantenimiento para que nadie pueda reservarlo hasta que lo revises.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }

                item {
                    Text(
                        text = "Piso",
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    if (floors.isEmpty()) {
                        Text(
                            text = "No hay pisos disponibles para este estacionamiento.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            floors.forEach { floor ->
                                AdminOptionButton(
                                    label = "${floor.name} (${floor.rows}x${floor.cols})",
                                    selected = selectedFloorId == floor.id,
                                    enabled = !isSaving && !lockedLocation,
                                    onClick = {
                                        selectedFloorId = floor.id
                                        localError = null
                                    }
                                )
                            }
                        }
                    }
                }

                if (lockedLocation) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ParkosSoftOrange
                            )
                        ) {
                            Text(
                                text = "Ubicación seleccionada desde el plano: fila ${initialTarget?.rowIndex}, columna ${initialTarget?.colIndex}.",
                                modifier = Modifier.padding(12.dp),
                                color = ParkosOrange,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = spotNumber,
                        onValueChange = {
                            spotNumber = it.uppercase()
                            localError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Identificador")
                        },
                        placeholder = {
                            Text("Ej. A-04")
                        },
                        singleLine = true,
                        enabled = !isSaving
                    )
                }

                item {
                    Text(
                        text = "Tipo",
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        typeOptions.forEach { option ->
                            AdminOptionButton(
                                label = option.second,
                                selected = selectedType == option.first,
                                enabled = !isSaving,
                                onClick = {
                                    selectedType = option.first
                                    localError = null
                                }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Ubicación en el grid",
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rowIndexText,
                            onValueChange = {
                                rowIndexText = it.filter { char -> char.isDigit() }
                                localError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Fila")
                            },
                            singleLine = true,
                            enabled = !isSaving && !lockedLocation,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )

                        OutlinedTextField(
                            value = colIndexText,
                            onValueChange = {
                                colIndexText = it.filter { char -> char.isDigit() }
                                localError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Columna")
                            },
                            singleLine = true,
                            enabled = !isSaving && !lockedLocation,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }
                }

                item {
                    Text(
                        text = selectedFloor?.let {
                            "Rango permitido: filas 0-${it.rows - 1}, columnas 0-${it.cols - 1}"
                        } ?: "Selecciona un piso para ver el rango permitido.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }

                item {
                    Text(
                        text = "Dimensiones opcionales",
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Text(
                        text = "Puedes definir el tamaño físico del cajón. Si lo dejas vacío, el sistema usará las medidas por defecto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = widthText,
                            onValueChange = {
                                widthText = it.filter { char ->
                                    char.isDigit() || char == '.'
                                }
                                localError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Ancho m")
                            },
                            placeholder = {
                                Text("2.5")
                            },
                            singleLine = true,
                            enabled = !isSaving,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        OutlinedTextField(
                            value = heightText,
                            onValueChange = {
                                heightText = it.filter { char ->
                                    char.isDigit() || char == '.'
                                }
                                localError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Largo m")
                            },
                            placeholder = {
                                Text("5")
                            },
                            singleLine = true,
                            enabled = !isSaving,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                    }
                }

                if (localError != null) {
                    item {
                        Text(
                            text = localError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving && floors.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    val floor = selectedFloor
                    val cleanSpotNumber = spotNumber.trim().uppercase()
                    val rowIndex = rowIndexText.toIntOrNull()
                    val colIndex = colIndexText.toIntOrNull()
                    val widthM = widthText.toDoubleOrNull()
                    val heightM = heightText.toDoubleOrNull()

                    when {
                        floor == null -> {
                            localError = "Selecciona un piso."
                        }

                        cleanSpotNumber.isBlank() -> {
                            localError = "Escribe el identificador del cajón."
                        }

                        rowIndex == null -> {
                            localError = "Escribe una fila válida."
                        }

                        colIndex == null -> {
                            localError = "Escribe una columna válida."
                        }

                        rowIndex < 0 || rowIndex >= floor.rows -> {
                            localError = "La fila debe estar entre 0 y ${floor.rows - 1}."
                        }

                        colIndex < 0 || colIndex >= floor.cols -> {
                            localError = "La columna debe estar entre 0 y ${floor.cols - 1}."
                        }

                        widthText.isNotBlank() && widthM == null -> {
                            localError = "Escribe un ancho válido."
                        }

                        heightText.isNotBlank() && heightM == null -> {
                            localError = "Escribe un largo válido."
                        }

                        widthM != null && widthM <= 0.0 -> {
                            localError = "El ancho debe ser mayor a 0."
                        }

                        heightM != null && heightM <= 0.0 -> {
                            localError = "El largo debe ser mayor a 0."
                        }

                        else -> {
                            onCreate(
                                selectedFloorId,
                                cleanSpotNumber,
                                selectedType,
                                rowIndex,
                                colIndex,
                                widthM,
                                heightM
                            )
                        }
                    }
                }
            ) {
                Text(if (isSaving) "Creando..." else "Crear")
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

@Composable
internal fun AdminOptionButton(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) ParkosOrange else Color.LightGray
        )
    ) {
        Text(
            text = label,
            color = if (selected) ParkosOrange else Color.DarkGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}