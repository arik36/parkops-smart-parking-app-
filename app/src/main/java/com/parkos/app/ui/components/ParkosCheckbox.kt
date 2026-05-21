package com.parkos.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkos.app.ui.theme.ParkosGray
import com.parkos.app.ui.theme.ParkosOrange

@Composable
fun ParkosCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    textFontSize: TextUnit = 14.sp,
    fontFamily: FontFamily? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = ParkosOrange)
        )
        Text(
            text = text,
            color = ParkosGray,
            fontSize = textFontSize,
            fontFamily = fontFamily,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}