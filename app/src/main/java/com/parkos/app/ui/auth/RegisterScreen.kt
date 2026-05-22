package com.parkos.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var orgId by remember { mutableStateOf("") }
    var showOrganizationsDialog by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()
    val organizationIds by authViewModel.organizationIds.collectAsState()
    val organizationError by authViewModel.organizationError.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val role = (uiState as LoginUiState.Success).userType
            onRegisterSuccess(role)
        }
    }

    if (showOrganizationsDialog) {
        OrganizationIdsDialog(
            organizationIds = organizationIds,
            organizationError = organizationError,
            onLoad = {
                authViewModel.loadOrganizationIds()
            },
            onSelect = { selectedId ->
                orgId = selectedId
                showOrganizationsDialog = false
            },
            onClose = {
                showOrganizationsDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear una nueva cuenta",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenid@! Porfavor añade tus datos",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = orgId,
                onValueChange = { orgId = it },
                modifier = Modifier.weight(1f),
                label = { Text("ID (si aplica)") },
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = {
                    showOrganizationsDialog = true
                    authViewModel.loadOrganizationIds()
                }
            ) {
                Text("Ver IDs")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState is LoginUiState.Error) {
            Text(
                text = (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                authViewModel.register(
                    fullName = fullName,
                    email = email,
                    password = password,
                    orgId = orgId.takeIf { it.isNotBlank() }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Ya tienes una cuenta? Inicia sesion")
        }
    }
}

@Composable
private fun OrganizationIdsDialog(
    organizationIds: List<String>,
    organizationError: String?,
    onLoad: () -> Unit,
    onSelect: (String) -> Unit,
    onClose: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoad()
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("IDs de organizaciones")
        },
        text = {
            Column {
                Text(
                    text = "Selecciona un ID para usarlo en el registro.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (organizationError != null) {
                    Text(
                        text = organizationError,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (organizationIds.isEmpty()) {
                    Text("No hay organizaciones disponibles.")
                } else {
                    Column {
                        organizationIds.forEach { id ->
                            Text(
                                text = id,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(id)
                                    }
                                    .padding(vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Divider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Cerrar")
            }
        }
    )
}