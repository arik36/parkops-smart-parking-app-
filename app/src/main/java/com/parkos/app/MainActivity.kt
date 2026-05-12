package com.parkos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.parkos.app.ui.navigation.NavGraph
import com.parkos.app.ui.theme.ParkOsCleanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParkOsCleanTheme {
                NavGraph()
            }
        }
    }
}