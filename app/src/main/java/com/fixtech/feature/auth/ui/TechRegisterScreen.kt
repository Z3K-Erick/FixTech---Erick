package com.fixtech.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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

@Suppress("DEPRECATION")
@Composable
fun TechRegisterScreen(
    onNavigateToTechLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onNavigateToUserRegister: () -> Unit = {},
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

            // Título: "Registro de Técnico FixTech"
            Text(
                text = "Registro de Técnico FixTech",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Completa tus datos para formar parte de la red de técnicos",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. Campo Nombre completo
            OutlinedTextField(
                value = uiState.techFullName,
                onValueChange = authViewModel::onTechFullNameChanged,
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej. Ing. Roberto Gómez", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isTechFullNameError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Campo Correo electrónico
            OutlinedTextField(
                value = uiState.techRegisterEmail,
                onValueChange = authViewModel::onTechRegisterEmailChanged,
                label = { Text("Correo electrónico") },
                placeholder = { Text("tecnico@tallerfixtech.com", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isTechRegisterEmailError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Campo Código de verificación de taller con botón cuadrado azul a su derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.workshopCode,
                    onValueChange = authViewModel::onWorkshopCodeChanged,
                    label = { Text("Código de verificación de taller") },
                    placeholder = { Text("Ej. FIX-8821", color = TextSecondary) },
                    singleLine = true,
                    isError = uiState.isWorkshopCodeError,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Botón cuadrado azul a su derecha con ícono de enviar Icons.Default.Send
                Button(
                    onClick = { authViewModel.sendWorkshopVerificationCode() },
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar código",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Campo Contraseña
            OutlinedTextField(
                value = uiState.techRegisterPassword,
                onValueChange = authViewModel::onTechRegisterPasswordChanged,
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••", color = TextSecondary) },
                singleLine = true,
                isError = uiState.isTechRegisterPasswordError,
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
                    if (authViewModel.validateTechRegister()) {
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
                    text = "Registrarse como Técnico",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Regreso al Login Técnico
            TextButton(
                onClick = onNavigateToTechLogin
            ) {
                Text(
                    text = "¿Ya tienes cuenta de técnico? Inicia sesión",
                    fontSize = 14.sp,
                    color = PrimaryBlue
                )
            }

            // Enlace hacia Registro de Usuario
            TextButton(
                onClick = onNavigateToUserRegister
            ) {
                Text(
                    text = "¿Eres usuario normal? Regístrate aquí",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
