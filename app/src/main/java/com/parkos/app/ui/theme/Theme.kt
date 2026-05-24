package com.parkos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
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

private val ParkosTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = PoppinsFamily),
        displayMedium = displayMedium.copy(fontFamily = PoppinsFamily),
        displaySmall = displaySmall.copy(fontFamily = PoppinsFamily),

        headlineLarge = headlineLarge.copy(fontFamily = PoppinsFamily),
        headlineMedium = headlineMedium.copy(fontFamily = PoppinsFamily),
        headlineSmall = headlineSmall.copy(fontFamily = PoppinsFamily),

        titleLarge = titleLarge.copy(fontFamily = PoppinsFamily),
        titleMedium = titleMedium.copy(fontFamily = PoppinsFamily),
        titleSmall = titleSmall.copy(fontFamily = PoppinsFamily),

        bodyLarge = bodyLarge.copy(fontFamily = PoppinsFamily),
        bodyMedium = bodyMedium.copy(fontFamily = PoppinsFamily),
        bodySmall = bodySmall.copy(fontFamily = PoppinsFamily),

        labelLarge = labelLarge.copy(fontFamily = PoppinsFamily),
        labelMedium = labelMedium.copy(fontFamily = PoppinsFamily),
        labelSmall = labelSmall.copy(fontFamily = PoppinsFamily)
    )
}

@Composable
fun ParkOsCleanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ParkosColorScheme,
        typography = ParkosTypography,
        shapes = Shapes,
        content = content
    )
}