package com.fixtech.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.components.FixTechLogo
import com.fixtech.core.components.FixTechLogoVariant
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.ErrorRed
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.feature.auth.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onNavigateToTechRegister: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        errorContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        errorTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        errorLabelColor = ErrorRed,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = SurfaceDark,
        errorBorderColor = ErrorRed,
        errorCursorColor = ErrorRed
    )

    Scaffold(
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // FixTechLogo con variante llave delineada
            FixTechLogo(
                variant = FixTechLogoVariant.USER,
                size = 56.dp,
                showHalo = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Marca FixTech
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fix",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tech",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Crea tu cuenta para acceder a todas las guías",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre Completo
            OutlinedTextField(
                value = uiState.registerFullName,
                onValueChange = authViewModel::onRegisterFullNameChanged,
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej. Juan Pérez", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isRegisterFullNameError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Correo Electrónico
            OutlinedTextField(
                value = uiState.registerEmail,
                onValueChange = authViewModel::onRegisterEmailChanged,
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@fixtech.com", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isRegisterEmailError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = uiState.registerPassword,
                onValueChange = authViewModel::onRegisterPasswordChanged,
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isRegisterPasswordError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Registrarse
            Button(
                onClick = {
                    if (authViewModel.validateUserRegister()) {
                        onRegisterSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Regreso al Login
            TextButton(
                onClick = onNavigateToLogin
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    fontSize = 14.sp,
                    color = PrimaryBlue
                )
            }

            // Enlace sutil para Técnico
            TextButton(
                onClick = onNavigateToTechRegister
            ) {
                Text(
                    text = "¿Eres técnico? Regístrate aquí",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
