package com.parkos.app.ui.map

import androidx.compose.ui.graphics.vector.ImageVector

internal data class BottomTab(
    val label: String,
    val icon: ImageVector
)

internal data class AdminCreateSpotTarget(
    val floorId: String,
    val rowIndex: Int,
    val colIndex: Int
)