package com.parkos.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.parkos.app.ui.theme.White
import androidx.compose.ui.draw.shadow

@Composable
fun ParkosCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(
                    topStart = 36.dp,
                    topEnd = 36.dp
                )
            )
            .background(
                color = White,
                shape = RoundedCornerShape(
                    topStart = 36.dp,
                    topEnd = 36.dp
                )
            )
            .padding(24.dp)
    ) {
        content()
    }
}