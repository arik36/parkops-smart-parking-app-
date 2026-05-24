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

@Composable
internal fun HomeTab(
    modifier: Modifier = Modifier,
    role: String?,
    userFullName: String?,
    userEmail: String?,
    parkingLots: List<ParkingLot>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelectParkingLot: (ParkingLot) -> Unit
) {
    if (role == "admin" || role == "collaborator") {
        OrganizationHomeTab(
            modifier = modifier,
            role = role,
            parkingLots = parkingLots,
            isLoading = isLoading,
            error = error,
            onRetry = onRetry,
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
                HomeQuickActionsCard(
                    role = role,
                    parkingLots = parkingLots
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
                            ParkingLotResultsPanel(
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