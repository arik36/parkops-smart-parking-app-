package com.parkos.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun NotificationsTab(
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
                    text = "Los consumidores reservan cajones normales. Los colaboradores reservan cajones staff. El administrador solo visualiza."
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
                color = ParkosMutedText
            )
        }
    }
}