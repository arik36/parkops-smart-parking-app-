package com.parkos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parkos.app.ui.auth.AuthViewModel
import com.parkos.app.ui.auth.LoginScreen
import com.parkos.app.ui.auth.RegisterScreen
import com.parkos.app.ui.map.MapScreen
import com.parkos.app.ui.map.ParkingViewModel
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
                onLogged = {
                    navController.navigate("map") {
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
                onLoginSuccess = {
                    navController.navigate("map") {
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
                onRegisterSuccess = {
                    navController.navigate("map") {
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

        composable("map") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val parkingViewModel: ParkingViewModel = hiltViewModel()

            MapScreen(
                viewModel = parkingViewModel,
                onLogout = {
                    authViewModel.logout()

                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}