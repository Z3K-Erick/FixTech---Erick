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
fun TechLoginScreen(
    onNavigateToTechHome: () -> Unit = {},
    onNavigateToTechProfile: () -> Unit = {},
    onNavigateToTechRegister: () -> Unit = {},
    onNavigateToUserLogin: () -> Unit = {},
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
            // FixTechLogo con tuerca hexagonal delineada (Técnico)
            FixTechLogo(
                variant = FixTechLogoVariant.TECH,
                size = 56.dp,
                showHalo = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Título "FixTech Técnico"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FixTech ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Técnico",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresa tus credenciales de colaborador",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Campo Correo Electrónico
            OutlinedTextField(
                value = uiState.techEmail,
                onValueChange = authViewModel::onTechEmailChanged,
                label = { Text("Correo Electrónico") },
                placeholder = { Text("tecnico@fixtech.com", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isTechEmailError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = uiState.techPassword,
                onValueChange = authViewModel::onTechPasswordChanged,
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isTechPasswordError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Ingresar
            Button(
                onClick = {
                    if (authViewModel.validateTechLogin()) {
                        onNavigateToTechHome()
                        onNavigateToTechProfile()
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
                    text = "Ingresar al Panel Técnico",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace hacia Registro de Técnico
            TextButton(
                onClick = onNavigateToTechRegister
            ) {
                Text(
                    text = "¿No tienes cuenta de técnico? Regístrate",
                    fontSize = 14.sp,
                    color = PrimaryBlue
                )
            }

            // Enlace de regreso a Usuario Normal
            TextButton(
                onClick = onNavigateToUserLogin
            ) {
                Text(
                    text = "¿Eres usuario normal? Inicia sesión aquí",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
