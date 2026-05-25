package com.parkos.app.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.parkos.app.domain.model.ParkingLayoutElement
import com.parkos.app.ui.theme.ParkosOrange

@Composable
internal fun AdminLayoutCellActionDialog(
    target: AdminLayoutCellTarget,
    isCreatingElement: Boolean,
    onDismiss: () -> Unit,
    onCreateSpot: () -> Unit,
    onCreateElement: (
        elementType: String,
        label: String?,
        description: String?
    ) -> Unit
) {
    var selectedElementType by remember {
        mutableStateOf("wall")
    }

    var label by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    val elementOptions = listOf(
        "wall" to "Muro",
        "pillar" to "Columna",
        "barrier" to "Barrera",
        "cabin" to "Caseta",
        "entrance" to "Entrada",
        "stairs" to "Escalera",
        "reserved_area" to "Área reservada"
    )

    AlertDialog(
        onDismissRequest = {
            if (!isCreatingElement) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Celda ${target.rowIndex}, ${target.colIndex}",
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
                        text = "Elige qué quieres agregar en esta celda vacía.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParkosMutedText
                    )
                }

                item {
                    Button(
                        enabled = !isCreatingElement,
                        onClick = onCreateSpot,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParkosOrange
                        )
                    ) {
                        Text("Crear cajón aquí")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Text(
                        text = "Agregar elemento visual",
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        elementOptions.forEach { option ->
                            AdminOptionButton(
                                label = option.second,
                                selected = selectedElementType == option.first,
                                enabled = !isCreatingElement,
                                onClick = {
                                    selectedElementType = option.first
                                }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = label,
                        onValueChange = {
                            label = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Etiqueta opcional")
                        },
                        placeholder = {
                            Text("Ej. Entrada Norte")
                        },
                        singleLine = true,
                        enabled = !isCreatingElement
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Descripción opcional")
                        },
                        placeholder = {
                            Text("Ej. Acceso principal")
                        },
                        enabled = !isCreatingElement,
                        minLines = 2
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ParkosSoftOrange
                        )
                    ) {
                        Text(
                            text = "Estos elementos no son reservables. Solo sirven para representar el plano físico del estacionamiento.",
                            modifier = Modifier.padding(12.dp),
                            color = ParkosOrange,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isCreatingElement,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkosOrange
                ),
                onClick = {
                    onCreateElement(
                        selectedElementType,
                        label.takeIf { it.isNotBlank() },
                        description.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text(if (isCreatingElement) "Agregando..." else "Agregar elemento")
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isCreatingElement,
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
internal fun AdminLayoutElementDialog(
    element: ParkingLayoutElement,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember(element.id) {
        mutableStateOf(false)
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteConfirm = false
                }
            },
            title = {
                Text(
                    text = "Eliminar elemento",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Quieres eliminar ${layoutElementTypeToSpanish(element.elementType)} del plano?"
                )
            },
            confirmButton = {
                Button(
                    enabled = !isDeleting,
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
                    enabled = !isDeleting,
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
            if (!isDeleting) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = layoutElementTypeToSpanish(element.elementType),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Ubicación: fila ${element.rowIndex}, columna ${element.colIndex}",
                    color = ParkosMutedText,
                    style = MaterialTheme.typography.bodySmall
                )

                if (!element.label.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Etiqueta: ${element.label}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!element.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Descripción: ${element.description}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

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
                            text = "Esto solo elimina el elemento visual del plano. No afecta cajones ni reservaciones.",
                            color = Color(0xFF8E1B1B),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            enabled = !isDeleting,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC94A4A)
                            ),
                            onClick = {
                                showDeleteConfirm = true
                            }
                        ) {
                            Text("Eliminar elemento")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isDeleting,
                onClick = onDismiss
            ) {
                Text("Cerrar")
            }
        }
    )
}

internal fun layoutElementTypeToSpanish(type: String): String {
    return when (type) {
        "wall" -> "Muro"
        "pillar" -> "Columna"
        "barrier" -> "Barrera"
        "cabin" -> "Caseta"
        "entrance" -> "Entrada"
        "stairs" -> "Escalera"
        "reserved_area" -> "Área reservada"
        else -> type
    }
}