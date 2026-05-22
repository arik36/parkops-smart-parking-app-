package com.parkos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parkos.app.ui.admin.AdminScreen
import com.parkos.app.ui.auth.AuthViewModel
import com.parkos.app.ui.auth.LoginScreen
import com.parkos.app.ui.auth.RegisterScreen
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

        composable("splash") {
            val viewModel: SplashViewModel = hiltViewModel()

            SplashScreen(
                viewModel = viewModel,
                onLogged = { role ->
                    val destination = when (role) {
                        "admin" -> "admin"
                        "collaborator" -> "map"
                        "consumer" -> "map"
                        else -> "login"
                    }

                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNotLogged = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val viewModel: AuthViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "admin" -> "admin"
                        "collaborator" -> "map"
                        "consumer" -> "map"
                        else -> "login"
                    }

                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            val viewModel: AuthViewModel = hiltViewModel()

            RegisterScreen(
                authViewModel = viewModel,
                onRegisterSuccess = { role ->
                    val destination = when (role) {
                        "admin" -> "admin"
                        "collaborator" -> "map"
                        "consumer" -> "map"
                        else -> "login"
                    }

                    navController.navigate(destination) {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

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

        composable("map") {
            TextScreen("Pantalla MAPA")
        }
    }
}