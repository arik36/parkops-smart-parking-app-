package com.parkos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.parkos.app.ui.admin.AdminScreen
import com.parkos.app.ui.auth.AuthViewModel
import com.parkos.app.ui.auth.LoginScreen
import com.parkos.app.ui.common.TextScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {

            val viewModel: AuthViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->

                    when (role) {
                        "admin" -> navController.navigate("admin")
                        "collaborator" -> navController.navigate("map")
                        "consumer" -> navController.navigate("map")
                    }
                }
            )
        }

        composable("admin") {
            AdminScreen()
        }

        composable("map") {
            TextScreen("Pantalla MAPA")
        }
    }
}