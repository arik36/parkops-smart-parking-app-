package com.parkos.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val ParkosColorScheme = lightColorScheme(

    primary = ParkosOrange,
    secondary = ParkosYellow,

    background = ParkosBeige,
    surface = White,

    onPrimary = White,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun ParkOsCleanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ParkosColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}