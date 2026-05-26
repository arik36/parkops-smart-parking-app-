package com.parkos.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkos.app.R
import com.parkos.app.ui.components.*
import com.parkos.app.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onGoToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ParkosYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            ParkosCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.parkos_logo),
                        contentDescription = "Parkos Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "BIENVENID",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParkosOrange
                        )
                        Text(
                            text = "@",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Inicia sesión para comenzar",
                        fontSize = 16.sp,
                        color = ParkosGray,
                        fontFamily = PoppinsFamily
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ParkosTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        borderColor = InputBorder
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ParkosTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        borderColor = InputBorder
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ParkosCheckbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            text = "Recuérdame",
                            textFontSize = 11.sp,
                            fontFamily = PoppinsFamily
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "¿Olvidaste la contraseña?",
                            color = ParkosGray,
                            fontSize = 11.sp,
                            fontFamily = PoppinsFamily,
                            softWrap = false,
                            modifier = Modifier
                                .wrapContentWidth(Alignment.End)
                                .clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ParkosButton(
                        text = "Inicia Sesion",
                        onClick = { viewModel.login(email, password) },
                        buttonColor = ButtonBg
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Text(
                            text = "No tienes cuenta? ",
                            color = ParkosGray,
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily
                        )

                        Text(
                            text = "Regístrate",
                            color = ParkosOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily,
                            modifier = Modifier.clickable {
                                onGoToRegister()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (uiState) {
                        is LoginUiState.Loading -> {
                            CircularProgressIndicator(color = ParkosOrange)
                        }

                        is LoginUiState.Success -> {
                            val role = (uiState as LoginUiState.Success).userType

                            LaunchedEffect(role) {
                                onLoginSuccess(role)
                            }
                        }

                        is LoginUiState.Error -> {
                            Text(
                                text = (uiState as LoginUiState.Error).message,
                                color = Color(0xFF8E1B1B),
                                fontSize = 13.sp,
                                fontFamily = PoppinsFamily,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}