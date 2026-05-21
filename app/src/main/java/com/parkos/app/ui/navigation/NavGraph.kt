package com.parkos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.parkos.app.ui.admin.AdminScreen
import com.parkos.app.ui.auth.AuthViewModel
import com.parkos.app.ui.auth.LoginScreen
import com.parkos.app.ui.common.TextScreen
import com.parkos.app.ui.splash.SplashScreen
import com.parkos.app.ui.splash.SplashViewModel

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // SPLASH
        composable("splash") {

            val viewModel: SplashViewModel = hiltViewModel()

            SplashScreen(
                viewModel = viewModel,

                onLogged = { role ->

                    when (role) {

                        "admin" -> {
                            navController.navigate("admin") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }

                        "collaborator" -> {
                            navController.navigate("map") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }

                        else -> {
                            navController.navigate("map") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                },

                onNotLogged = {

                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // LOGIN
        composable("login") {

            val viewModel: AuthViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->

                    when (role) {

                        "admin" -> {
                            navController.navigate("admin") {
                                popUpTo("login") { inclusive = true }
                            }
                        }

                        "collaborator" -> {
                            navController.navigate("map") {
                                popUpTo("login") { inclusive = true }
                            }
                        }

                        "consumer" -> {
                            navController.navigate("map") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ADMIN
        composable("admin") {

            val viewModel: AuthViewModel = hiltViewModel()

            AdminScreen(

                onLogout = {

                    viewModel.logout()

                    navController.navigate("login") {

                        popUpTo(0)
                    }
                }
            )
        }

        // MAP
        composable("map") {
            TextScreen("Pantalla MAPA")
        }
    }
}