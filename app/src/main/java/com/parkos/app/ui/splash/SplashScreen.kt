package com.parkos.app.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onLogged: (String) -> Unit,
    onNotLogged: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()

        when (uiState) {

            is SplashUiState.Logged -> {

                val role =
                    (uiState as SplashUiState.Logged).role

                LaunchedEffect(Unit) {
                    onLogged(role)
                }
            }

            SplashUiState.NotLogged -> {

                LaunchedEffect(Unit) {
                    onNotLogged()
                }
            }

            else -> {}
        }
    }
}