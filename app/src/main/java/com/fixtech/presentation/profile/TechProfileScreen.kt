package com.fixtech.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.ui.components.FixTechSegmentedControl
import com.fixtech.core.ui.components.FixTechTabs
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.DangerBorder
import com.fixtech.core.ui.theme.DangerRed
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SuccessGreen
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.core.ui.theme.WarningAmber
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.domain.model.Certificate
import com.fixtech.integration.BiometricAuthHelper
import com.fixtech.integration.CertificateDocumentHelper
import com.fixtech.integration.rememberCertificateDocumentLauncher

@Composable
fun TechProfileScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior: Botón de flecha atrás a la izquierda + "FixTech Admin"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "FixTech ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Admin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Segmented Control ("Mis Equipos", "Guías", "Perfil") con "Perfil" seleccionado
            FixTechSegmentedControl(
                items = FixTechTabs.TechTabs,
                selectedIndex = 2,
                onItemSelected = { index ->
                    if (index == 0 || index == 1) {
                        onNavigateToHome()
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            TechProfileContent(
                uiState = uiState,
                onToggleNotifications = profileViewModel::toggleTechNotifications,
                onUploadCertificate = profileViewModel::uploadCertificate,
                onAddCertificate = { title, institution, fileName, localFilePath, mimeType, onSuccess ->
                    profileViewModel.addTechCertificate(
                        title = title,
                        institution = institution,
                        fileName = fileName,
                        localFilePath = localFilePath,
                        mimeType = mimeType,
                        onSuccess = onSuccess
                    )
                },
                onDeleteCertificate = profileViewModel::deleteTechCertificate,
                onDismissCertNotice = profileViewModel::dismissCertNotice,
                onLogout = onLogout
            )
        }
    }
}

@Composable
fun TechProfileContent(
    uiState: ProfileUiState,
    onToggleNotifications: (Boolean) -> Unit,
    onUploadCertificate: (String) -> Unit = {},
    onAddCertificate: (
        title: String,
        institution: String,
        fileName: String,
        localFilePath: String,
        mimeType: String,
        onSuccess: () -> Unit
    ) -> Unit = { _, _, fileName, _, _, onSuccess ->
        onUploadCertificate(fileName)
        onSuccess()
    },
    onDeleteCertificate: (String) -> Unit = {},
    onDismissCertNotice: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isBiometricEnabled by remember {
        mutableStateOf(BiometricAuthHelper.isBiometricEnabled(context, isTech = true))
    }
    var showCertificatesModal by remember { mutableStateOf(false) }

    if (showCertificatesModal) {
        TechCertificatesModal(
            techName = uiState.techName,
            techInitial = uiState.techInitial,
            certificates = uiState.techCertificates,
            isUploading = uiState.isUploadingCertificate,
            onDismiss = { showCertificatesModal = false },
            onAddCertificate = { title, institution, fileName, localFilePath, mimeType ->
                onAddCertificate(title, institution, fileName, localFilePath, mimeType) {
                    Toast.makeText(
                        context,
                        "Documento subido correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDeleteCertificate = { cert ->
                onDeleteCertificate(cert.id)
                Toast.makeText(
                    context,
                    "Certificado «${cert.title}» eliminado",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onOpenCertificate = { cert ->
                if (cert.localFilePath.isNotBlank()) {
                    CertificateDocumentHelper.openCertificate(
                        context = context,
                        localFilePath = cert.localFilePath,
                        mimeType = cert.mimeType
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Documento: ${cert.fileName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 13.sp,
                color = DangerRed,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // 1) Avatar circular, Nombre real, Correo real y etiqueta "Técnico Administrador"
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(PrimaryBlue.copy(alpha = 0.2f), shape = CircleShape)
                .border(2.dp, PrimaryBlue.copy(alpha = 0.45f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.techInitial,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = uiState.techName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        if (uiState.techEmail.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.techEmail,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Text(
                text = uiState.techBadge,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2) Fila de los 3 contadores reales desde Room: "Mis Equipos", "Mis Guías", "Certificados"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.techMyDevicesCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mis Equipos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.techMyGuidesCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mis Guías",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showCertificatesModal = true },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.techCertificatesCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Certificados",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // 3) Sección "PREFERENCIAS" -> Tarjeta "Notificaciones" y "Acceso biométrico"
        Text(
            text = "PREFERENCIAS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notificaciones",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Avisos de aprobaciones y actividad",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = uiState.techNotificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = BackgroundDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tarjeta de Acceso Biométrico
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Acceso biométrico",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Acceso biométrico",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Iniciar sesión con huella digital",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = { enable ->
                        if (enable) {
                            if (!BiometricAuthHelper.canAuthenticate(context)) {
                                val errorMsg = BiometricAuthHelper.getHardwareErrorMessage(context)
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                isBiometricEnabled = false
                            } else {
                                val activity = BiometricAuthHelper.findFragmentActivity(context)
                                if (activity != null) {
                                    BiometricAuthHelper.authenticate(
                                        activity = activity,
                                        title = "Acceso biométrico",
                                        subtitle = "Confirma tu huella para activar el acceso",
                                        onSuccess = {
                                            BiometricAuthHelper.setBiometricEnabled(context, isTech = true, enabled = true)
                                            isBiometricEnabled = true
                                            Toast.makeText(context, "Acceso biométrico activado", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { error ->
                                            isBiometricEnabled = false
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "No se pudo iniciar el servicio biométrico.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            BiometricAuthHelper.setBiometricEnabled(context, isTech = true, enabled = false)
                            isBiometricEnabled = false
                            Toast.makeText(context, "Acceso biométrico desactivado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = BackgroundDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4) Sección "CERTIFICADOS"
        Text(
            text = "CERTIFICADOS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Sin certificados: muestra el aviso "No has subido certificados" con botones "Subir certificado" y "Ahora no"
        if (uiState.techCertificates.isEmpty() && !uiState.isCertNoticeDismissed) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No has subido certificados",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Es opcional. Puedes agregar equipos y publicar guías sin subir certificados.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.padding(start = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCertificatesModal = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Subir certificado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = onDismissCertNotice,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderDark),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BackgroundDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Text(
                                text = "Ahora no",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Tarjeta principal de acceso a "Mis certificados"
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Certificado o comprobante",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.techCertificates.isEmpty()) {
                            "Sube un documento que valide tus conocimientos. Opcional."
                        } else {
                            "${uiState.techCertificates.size} ${if (uiState.techCertificates.size == 1) "documento subido" else "documentos subidos"}."
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { showCertificatesModal = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (uiState.techCertificates.isEmpty()) "Subir archivo" else "Mis certificados",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // Vista rápida de los certificados subidos en el perfil
        if (uiState.techCertificates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            uiState.techCertificates.forEach { cert ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            if (cert.localFilePath.isNotBlank()) {
                                CertificateDocumentHelper.openCertificate(
                                    context = context,
                                    localFilePath = cert.localFilePath,
                                    mimeType = cert.mimeType
                                )
                            } else {
                                showCertificatesModal = true
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(BackgroundDark, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cert.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${cert.institution.ifBlank { "Sin institución" }} · ${cert.fileName}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = {
                                onDeleteCertificate(cert.id)
                                Toast.makeText(
                                    context,
                                    "Certificado «${cert.title}» eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Eliminar certificado",
                                tint = DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5) Botón "Cerrar Sesión": Va hasta el FINAL ABSOLUTO de la pantalla
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, DangerBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceDark,
                contentColor = DangerRed
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar Sesión",
                tint = DangerRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar Sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DangerRed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TechCertificatesModal(
    techName: String,
    techInitial: String,
    certificates: List<Certificate>,
    isUploading: Boolean,
    onDismiss: () -> Unit,
    onAddCertificate: (
        title: String,
        institution: String,
        fileName: String,
        localFilePath: String,
        mimeType: String
    ) -> Unit,
    onDeleteCertificate: (Certificate) -> Unit,
    onOpenCertificate: (Certificate) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var institutionInput by remember { mutableStateOf("") }
    var pickedFileName by remember { mutableStateOf("") }
    var pickedLocalPath by remember { mutableStateOf("") }
    var pickedMimeType by remember { mutableStateOf("application/pdf") }
    var fileValidationError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf(false) }

    val launchCertPicker = rememberCertificateDocumentLauncher(
        onError = { errorMsg ->
            pickedFileName = ""
            pickedLocalPath = ""
            fileValidationError = errorMsg
        },
        onDocumentPicked = { result ->
            pickedFileName = result.fileName
            pickedLocalPath = result.localFilePath
            pickedMimeType = result.mimeType
            fileValidationError = null
        }
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = BackgroundDark,
        unfocusedContainerColor = BackgroundDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark,
        errorBorderColor = WarningAmber,
        errorLabelColor = WarningAmber,
        errorContainerColor = BackgroundDark,
        errorTextColor = TextPrimary
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Mis certificados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Cabecera del perfil del técnico
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = techInitial,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = techName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tu perfil de técnico",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BackgroundDark,
                            border = BorderStroke(1.dp, BorderDark)
                        ) {
                            Text(
                                text = "Opcional",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Comparte certificados o comprobantes de tus conocimientos. Los documentos que subas se mostrarán en tu perfil público.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Lista de Tus Certificados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TUS CERTIFICADOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${certificates.size} ${if (certificates.size == 1) "documento" else "documentos"}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (certificates.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Aún no has subido certificados",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Puedes agregar equipos y publicar guías sin subir certificados.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    certificates.forEach { cert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onOpenCertificate(cert) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(BackgroundDark, RoundedCornerShape(8.dp))
                                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cert.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${cert.institution.ifBlank { "Sin institución" }} · ${cert.fileName}",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenCertificate(cert) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Abrir certificado",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteCertificate(cert) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Eliminar certificado",
                                        tint = DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Formulario Agregar documento
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Agregar documento",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Título del documento *",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = {
                                titleInput = it
                                if (it.trim().isNotEmpty()) titleError = false
                            },
                            placeholder = { Text("ej. Mantenimiento de equipos", color = TextSecondary) },
                            isError = titleError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors
                        )
                        if (titleError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "El título del certificado es obligatorio.",
                                fontSize = 11.sp,
                                color = WarningAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Institución emisora",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Opcional",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = institutionInput,
                            onValueChange = { institutionInput = it },
                            placeholder = { Text("Nombre de la institución", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Documento *",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BackgroundDark)
                                .drawBehind {
                                    drawRoundRect(
                                        color = if (fileValidationError != null) WarningAmber else BorderDark,
                                        style = Stroke(
                                            width = 1.5.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                        ),
                                        cornerRadius = CornerRadius(10.dp.toPx())
                                    )
                                }
                                .clickable { launchCertPicker() },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = if (pickedFileName.isNotBlank()) Icons.Default.Description else Icons.Default.CloudUpload,
                                    contentDescription = "Seleccionar archivo",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (pickedFileName.isNotBlank()) pickedFileName else "Seleccionar archivo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "PDF, JPG o PNG · Máximo 10 MB",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (fileValidationError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = fileValidationError ?: "",
                                fontSize = 12.sp,
                                color = DangerRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                val canSave = titleInput.trim().isNotEmpty() && pickedFileName.isNotBlank() && !isUploading

                Button(
                    onClick = {
                        if (titleInput.trim().isEmpty()) {
                            titleError = true
                            return@Button
                        }
                        if (pickedFileName.isBlank()) {
                            fileValidationError = "Selecciona un archivo PDF, JPG o PNG de hasta 10 MB"
                            return@Button
                        }
                        onAddCertificate(
                            titleInput.trim(),
                            institutionInput.trim(),
                            pickedFileName,
                            pickedLocalPath,
                            pickedMimeType
                        )
                        titleInput = ""
                        institutionInput = ""
                        pickedFileName = ""
                        pickedLocalPath = ""
                        fileValidationError = null
                        titleError = false
                    },
                    enabled = canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = SurfaceDark,
                        disabledContentColor = TextSecondary
                    )
                ) {
                    Text(
                        text = if (isUploading) "Guardando..." else "Guardar documento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Omitir y volver al perfil",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
