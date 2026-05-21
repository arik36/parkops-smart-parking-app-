package com.parkos.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.parkos.app.ui.theme.PoppinsFamily
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(ParkosYellow)) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            // Pequeña franja naranja superior (40dp)
            Spacer(modifier = Modifier.height(40.dp))

            // Tarjeta blanca que ocupa el resto de la pantalla
            ParkosCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // ocupatodo el espacio restante hasta abajo
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.parkos_logo),
                        contentDescription = "Parkos Logo",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Título BIENVENID@
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

                    // Subtítulo (con Poppins)
                    Text(
                        text = "Inicia sesión para comenzar",
                        fontSize = 16.sp,
                        color = ParkosGray,
                        fontFamily = PoppinsFamily
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Email
                    ParkosTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        borderColor = InputBorder
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    ParkosTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        borderColor = InputBorder
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila: Checkbox + Olvidaste contraseña (ambos con Poppins y 14sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox "Recuérdame"
                        ParkosCheckbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            text = "Recuérdame",
                            textFontSize = 11.sp,
                            fontFamily = PoppinsFamily
                        )

                        // Espacio flexible que empuja el texto hacia la derecha
                        Spacer(modifier = Modifier.weight(1f))

                        // Texto "¿Olvidaste la contraseña?" sin romper y alineado a la derecha
                        Text(
                            text = "¿Olvidaste la contraseña?",
                            color = ParkosGray,
                            fontSize = 11.sp,
                            fontFamily = PoppinsFamily,
                            softWrap = false,  // ← Evita que se divida en dos líneas
                            modifier = Modifier.wrapContentWidth(Alignment.End)
                                .clickable{}
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Iniciar Sesión
                    ParkosButton(
                        text = "Inicia Sesion",
                        onClick = { viewModel.login(email, password) },
                        buttonColor = ButtonBg
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Registro (dentro de la tarjeta, debajo del botón)
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

                                // navegar a register
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Estados Loading / Error / Success
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
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                fontFamily = PoppinsFamily
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}