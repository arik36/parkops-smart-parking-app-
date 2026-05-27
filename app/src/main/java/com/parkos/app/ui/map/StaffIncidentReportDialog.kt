package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.parkos.app.domain.model.ParkingLot
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun StaffIncidentReportDialog(
    selectedParkingLot: ParkingLot?,
    staffName: String?,
    staffEmail: String?,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        spotNumber: String?,
        vehiclePlate: String,
        incidentType: String,
        customIncidentType: String?,
        details: String?
    ) -> Unit
) {
    var spotNumber by remember {
        mutableStateOf("")
    }

    var vehiclePlate by remember {
        mutableStateOf("")
    }

    var incidentType by remember {
        mutableStateOf("actividad_sospechosa")
    }

    var customIncidentType by remember {
        mutableStateOf("")
    }

    var details by remember {
        mutableStateOf("")
    }

    val incidentOptions = listOf(
        "robo" to "Robo",
        "danio_vehiculo" to "Daño a vehículo",
        "danio_infraestructura" to "Daño a infraestructura",
        "agresion" to "Agresión a trabajador o persona",
        "actividad_sospechosa" to "Actividad sospechosa",
        "vehiculo_mal_estacionado" to "Vehículo mal estacionado",
        "otro" to "Otro"
    )

    val cleanPlate = vehiclePlate.trim().uppercase()
    val canSubmit = selectedParkingLot != null &&
            cleanPlate.length >= 3 &&
            !isCreating &&
            (incidentType != "otro" || customIncidentType.trim().length >= 3)

    AlertDialog(
        onDismissRequest = {
            if (!isCreating) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Nuevo reporte de incidente",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    StaffReportInfoCard(
                        parkingLotName = selectedParkingLot?.name ?: "Sin estacionamiento seleccionado",
                        parkingLotAddress = selectedParkingLot?.address ?: "Selecciona un estacionamiento antes de crear el reporte.",
                        staffName = staffName?.takeIf { it.isNotBlank() } ?: "Personal staff",
                        staffEmail = staffEmail?.takeIf { it.isNotBlank() } ?: "Correo no disponible"
                    )
                }

                item {
                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { value ->
                            vehiclePlate = value.uppercase()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Placa del vehículo")
                        },
                        placeholder = {
                            Text("Ej. ABC-123")
                        },
                        singleLine = true,
                        enabled = !isCreating
                    )
                }

                item {
                    OutlinedTextField(
                        value = spotNumber,
                        onValueChange = { value ->
                            spotNumber = value.uppercase()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Cajón / casilla relacionada")
                        },
                        placeholder = {
                            Text("Ej. A-03")
                        },
                        singleLine = true,
                        enabled = !isCreating
                    )
                }

                item {
                    Text(
                        text = "Tipo de incidente",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        incidentOptions.forEach { option ->
                            IncidentTypeOptionButton(
                                label = option.second,
                                selected = incidentType == option.first,
                                enabled = !isCreating,
                                onClick = {
                                    incidentType = option.first
                                }
                            )
                        }
                    }
                }

                if (incidentType == "otro") {
                    item {
                        OutlinedTextField(
                            value = customIncidentType,
                            onValueChange = {
                                customIncidentType = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Describe el tipo de incidente")
                            },
                            placeholder = {
                                Text("Ej. Obstrucción de acceso")
                            },
                            singleLine = true,
                            enabled = !isCreating
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = details,
                        onValueChange = {
                            details = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Detalles adicionales")
                        },
                        placeholder = {
                            Text("Describe lo ocurrido con claridad.")
                        },
                        minLines = 4,
                        enabled = !isCreating
                    )
                }

                item {
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
                            text = "El reporte se guardará como registro interno. Después podrás generar y compartir un PDF formal.",
                            modifier = Modifier.fillMaxWidth(),
                            color = ParkosOrange,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    onSubmit(
                        spotNumber.takeIf { it.isNotBlank() },
                        cleanPlate,
                        incidentType,
                        customIncidentType.takeIf { it.isNotBlank() },
                        details.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text(if (isCreating) "Guardando..." else "Guardar reporte")
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun StaffReportInfoCard(
    parkingLotName: String,
    parkingLotAddress: String,
    staffName: String,
    staffEmail: String
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Datos automáticos",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estacionamiento: $parkingLotName",
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = parkingLotAddress,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Reporta: $staffName",
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = staffEmail,
                color = ParkosMutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun IncidentTypeOptionButton(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) ParkosOrange else Color.White,
            contentColor = if (selected) Color.White else ParkosOrange,
            disabledContainerColor = Color(0xFFF3F3F3),
            disabledContentColor = Color(0xFF999999)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) ParkosOrange else Color(0xFFFFD8BD)
        )
    ) {
        Text(label)
    }
}