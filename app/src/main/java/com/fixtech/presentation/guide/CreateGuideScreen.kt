package com.fixtech.presentation.guide

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fixtech.core.common.Constants
import com.fixtech.core.ui.theme.AlertDangerBg
import com.fixtech.core.ui.theme.AlertInfoBg
import com.fixtech.core.ui.theme.AlertWarningBg
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.DangerRed
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SuccessGreen
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.core.ui.theme.WarningAmber
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.integration.rememberMediaPickerLauncher
import com.fixtech.presentation.home.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGuideScreen(
    onNavigateBack: () -> Unit = {},
    onPublishGuide: () -> Unit = {},
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory),
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val context = LocalContext.current
    val uiState by guideViewModel.uiState.collectAsStateWithLifecycle()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val isEditing = uiState.editingGuideId != null

    // Solo dispositivos del técnico actual (Constants.DEFAULT_TECH_ID)
    val techDevices = remember(homeState.catalogDevices) {
        homeState.catalogDevices.filter { it.technicianId == Constants.DEFAULT_TECH_ID }
    }

    LaunchedEffect(techDevices, isEditing) {
        if (!isEditing && techDevices.isNotEmpty()) {
            val currentSelectedInTech = techDevices.find { it.id == uiState.formSelectedDeviceId }
            if (currentSelectedInTech == null) {
                val defaultDev = techDevices.first()
                val catLabel = if (defaultDev.type.contains("PC", ignoreCase = true)) "PC" else "Laptop"
                guideViewModel.onFormDeviceSelected(
                    deviceId = defaultDev.id,
                    category = catLabel,
                    modelSpec = "${defaultDev.name} ${defaultDev.model}"
                )
            }
        }
    }

    val launchCoverImagePicker = rememberMediaPickerLauncher(
        fallbackFileName = "portada_guia.jpg",
        mimeType = "image/*",
        onPermissionDenied = { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    ) { fileName, uriString ->
        guideViewModel.onFormCoverImageSelected(fileName, uriString)
    }

    val difficulties = listOf("Bajo", "Medio", "Alto")
    val quickSuggestedTools = listOf(
        "Destornillador Phillips #0",
        "Púa de apertura",
        "Pulsera antiestática",
        "Pasta térmica",
        "Alcohol isopropílico",
        "Compresor de aire"
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark,
        errorBorderColor = WarningAmber,
        errorLabelColor = WarningAmber,
        errorContainerColor = SurfaceDark,
        errorTextColor = TextPrimary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Guía" else "Nueva Guía",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Cuadro de "Falta información obligatoria"
            if (uiState.showMandatoryValidationBanner) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = WarningAmber.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Falta información obligatoria",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Completa el título de la guía, selecciona un dispositivo y agrega al menos un paso con título antes de guardar.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // SECCIÓN 1 - DATOS GENERALES
            // =========================================================================
            Text(
                text = "INFORMACIÓN GENERAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Título de la guía
            Text(
                text = "Título *",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Qué mantenimiento hace esta guía. Ej: Cambiar batería, Limpiar ventilador.",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.formGuideTitle,
                onValueChange = guideViewModel::onFormGuideTitleChanged,
                placeholder = { Text("ej. Cambiar Batería", color = TextSecondary) },
                isError = uiState.isGuideTitleError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )
            if (uiState.isGuideTitleError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Escribe el título de la guía.",
                    fontSize = 12.sp,
                    color = WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Dispositivo (Solo equipos del técnico actual; bloqueado al editar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dispositivo *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (isEditing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "No se puede cambiar al editar",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            if (!isEditing) {
                Text(
                    text = "Selecciona uno de tus equipos registrados para asociar esta guía.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (techDevices.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceDark,
                    border = BorderStroke(
                        1.dp,
                        if (uiState.isGuideDeviceError) WarningAmber else BorderDark
                    )
                ) {
                    Text(
                        text = "No tienes dispositivos registrados en Mis Equipos. Registra un dispositivo primero.",
                        fontSize = 12.sp,
                        color = if (uiState.isGuideDeviceError) WarningAmber else TextSecondary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .alpha(if (isEditing) 0.6f else 1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    techDevices.forEach { device ->
                        val isSelected = device.id == uiState.formSelectedDeviceId
                        val catLabel = if (device.type.contains("PC", ignoreCase = true)) "PC" else "Laptop"
                        Surface(
                            modifier = Modifier
                                .height(42.dp)
                                .clickable(enabled = !isEditing) {
                                    guideViewModel.onFormDeviceSelected(
                                        deviceId = device.id,
                                        category = catLabel,
                                        modelSpec = "${device.name} ${device.model}"
                                    )
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else SurfaceDark,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = when {
                                    isSelected -> PrimaryBlue
                                    uiState.isGuideDeviceError -> WarningAmber
                                    else -> BorderDark
                                }
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Text(
                                    text = "${device.name} ${device.model} (${catLabel})",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryBlue else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            if (uiState.isGuideDeviceError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Selecciona un dispositivo para esta guía.",
                    fontSize = 12.sp,
                    color = WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Dificultad: Bajo, Medio, Alto
            Text(
                text = "Dificultad",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Qué tan difícil es para el usuario: Bajo = fácil, Medio = algo de práctica, Alto = técnico.",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                difficulties.forEach { difficulty ->
                    val isSelected = difficulty.equals(uiState.formSelectedDifficulty, ignoreCase = true)
                    val levelColor = when (difficulty) {
                        "Bajo" -> SuccessGreen
                        "Alto" -> DangerRed
                        else -> WarningAmber
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clickable { guideViewModel.onFormDifficultySelected(difficulty) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) levelColor.copy(alpha = 0.14f) else SurfaceDark,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) levelColor else BorderDark
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) levelColor else TextSecondary.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = difficulty,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) levelColor else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            HorizontalDivider(color = BorderDark)
            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // SECCIÓN 2 - HERRAMIENTAS
            // =========================================================================
            Text(
                text = "HERRAMIENTAS NECESARIAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Lista lo que el usuario debe tener listo antes de empezar.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.formToolInput,
                    onValueChange = guideViewModel::onFormToolInputChanged,
                    placeholder = { Text("ej. Destornillador Phillips #0", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { guideViewModel.addFormTool() },
                    enabled = uiState.formToolInput.trim().isNotEmpty(),
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = SurfaceDark,
                        disabledContentColor = TextSecondary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar herramienta",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Sugerencias rápidas
            val remainingSuggestions = quickSuggestedTools.filter { !uiState.formToolsList.contains(it) }
            if (remainingSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    remainingSuggestions.forEach { suggestion ->
                        Surface(
                            modifier = Modifier.clickable { guideViewModel.addQuickTool(suggestion) },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, BorderDark)
                        ) {
                            Text(
                                text = "+ $suggestion",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.formToolsList.isEmpty()) {
                Text(
                    text = "Ninguna herramienta agregada. Usa las sugerencias de arriba o escribe la tuya.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.formToolsList.forEachIndexed { idx, toolName ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = toolName,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { guideViewModel.removeFormTool(idx) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Quitar herramienta",
                                        tint = DangerRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen de portada opcional
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark, shape = RoundedCornerShape(10.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = BorderDark,
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                    .clickable { launchCoverImagePicker() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.formCoverImageUri.isNotBlank()) {
                    AsyncImage(
                        model = uiState.formCoverImageUri,
                        contentDescription = "Vista previa de imagen de portada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Subir imagen de portada",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Subir imagen de portada (opcional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Cámara o galería · JPG / PNG",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            HorizontalDivider(color = BorderDark)
            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // SECCIÓN 3 - PASOS DEL TUTORIAL
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PASOS DEL TUTORIAL (${uiState.formSteps.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Instrucciones paso a paso que verá el usuario.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    modifier = Modifier.clickable { guideViewModel.addStepBlock() },
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Paso",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.formSteps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .drawBehind {
                            drawRoundRect(
                                color = if (uiState.isStepsError) WarningAmber else BorderDark,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                ),
                                cornerRadius = CornerRadius(10.dp.toPx())
                            )
                        }
                        .clickable { guideViewModel.addStepBlock() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Agrega el primer paso",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Cada paso tiene título, descripción, imagen y alerta opcional",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            } else {
                uiState.formSteps.forEachIndexed { index, stepState ->
                    StepFormCard(
                        stepNumber = index + 1,
                        isFirst = index == 0,
                        isLast = index == uiState.formSteps.lastIndex,
                        stepState = stepState,
                        onTitleChanged = { guideViewModel.onStepTitleChanged(index, it) },
                        onDescriptionChanged = { guideViewModel.onStepDescriptionChanged(index, it) },
                        onStepImageSelected = { fileName, uriString ->
                            guideViewModel.onStepImageSelected(index, fileName, uriString)
                        },
                        onPermissionDenied = { msg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onCautionToggled = { guideViewModel.onStepCautionToggled(index, it) },
                        onAlertLevelSelected = { guideViewModel.onStepAlertLevelSelected(index, it) },
                        onAlertTextChanged = { guideViewModel.onStepAlertTextChanged(index, it) },
                        onRequiresConfirmationToggled = { guideViewModel.onStepRequiresConfirmationToggled(index, it) },
                        onConfirmationTextChanged = { guideViewModel.onStepConfirmationTextChanged(index, it) },
                        onMoveUp = { guideViewModel.moveStepUp(index) },
                        onMoveDown = { guideViewModel.moveStepDown(index) },
                        onDeleteStep = { guideViewModel.removeStepBlock(index) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (uiState.isStepsError) {
                Text(
                    text = "Agrega al menos un paso y complétalo (cada paso necesita título).",
                    fontSize = 12.sp,
                    color = WarningAmber,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Botón Secundario "Agregar nuevo paso"
            OutlinedButton(
                onClick = { guideViewModel.addStepBlock() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderDark),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceDark,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregar nuevo paso",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Principal: "Guardar Guía" / "Actualizar Guía"
            Button(
                onClick = {
                    guideViewModel.publishGuide(
                        availableDevices = homeState.catalogDevices,
                        onSuccess = { wasUpdated ->
                            Toast.makeText(
                                context,
                                if (wasUpdated) "Guía actualizada correctamente" else "Guía publicada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            onPublishGuide()
                        }
                    )
                },
                enabled = !uiState.isSavingGuide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isSavingGuide) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Guardando...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val stepsSuffix = if (uiState.formSteps.isNotEmpty()) " (${uiState.formSteps.size} pasos)" else ""
                    Text(
                        text = (if (isEditing) "Actualizar Guía" else "Guardar Guía") + stepsSuffix,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StepFormCard(
    stepNumber: Int,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    stepState: StepFormState = StepFormState(),
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onStepImageSelected: (fileName: String, uriString: String) -> Unit = { _, _ -> },
    onPermissionDenied: ((String) -> Unit)? = null,
    onCautionToggled: (Boolean) -> Unit = {},
    onAlertLevelSelected: (String) -> Unit = {},
    onAlertTextChanged: (String) -> Unit = {},
    onRequiresConfirmationToggled: (Boolean) -> Unit = {},
    onConfirmationTextChanged: (String) -> Unit = {},
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    onDeleteStep: () -> Unit = {}
) {
    val alertLevels = listOf("Advertencia", "Peligro", "Información")
    val launchStepMediaPicker = rememberMediaPickerLauncher(
        fallbackFileName = "paso_$stepNumber.jpg",
        mimeType = "image/*",
        onPermissionDenied = onPermissionDenied
    ) { fileName, uriString ->
        onStepImageSelected(fileName, uriString)
    }

    val innerFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark,
        errorBorderColor = WarningAmber,
        errorLabelColor = WarningAmber,
        errorContainerColor = SurfaceDark,
        errorTextColor = TextPrimary
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(
            width = 1.dp,
            color = if (stepState.isTitleError) WarningAmber else BorderDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado "Paso #X" con botones de subir (↑), bajar (↓) y eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Paso #$stepNumber",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = !isFirst,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Subir paso",
                            tint = if (!isFirst) TextPrimary else TextSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onMoveDown,
                        enabled = !isLast,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Bajar paso",
                            tint = if (!isLast) TextPrimary else TextSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteStep,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar paso",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OutlinedTextField para "Título del paso"
            OutlinedTextField(
                value = stepState.title,
                onValueChange = onTitleChanged,
                label = { Text("Título del paso *") },
                placeholder = { Text("ej. Desconectar batería", color = TextSecondary) },
                isError = stepState.isTitleError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = innerFieldColors
            )
            if (stepState.isTitleError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El título del paso es obligatorio.",
                    fontSize = 11.sp,
                    color = WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // OutlinedTextField para "Descripción"
            OutlinedTextField(
                value = stepState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Descripción") },
                placeholder = { Text("Qué debe hacer el usuario en este paso...", color = TextSecondary) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = innerFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Imagen propia del paso (MediaPickerHelper + AsyncImage)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundDark, shape = RoundedCornerShape(10.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = BorderDark,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            ),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                    .clickable { launchStepMediaPicker() },
                contentAlignment = Alignment.Center
            ) {
                if (stepState.imageUri.isNotBlank()) {
                    AsyncImage(
                        model = stepState.imageUri,
                        contentDescription = "Imagen del paso $stepNumber",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Imagen del paso",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Imagen del paso (Cámara o Galería)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch de Alerta opcional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (stepState.includeCautionAlert) "Alerta activada" else "Agregar alerta opcional",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Aviso importante antes de continuar",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = stepState.includeCautionAlert,
                    onCheckedChange = onCautionToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WarningAmber,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = BackgroundDark
                    )
                )
            }

            // Configuración de la Alerta opcional ("Advertencia", "Peligro", "Información")
            if (stepState.includeCautionAlert) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tipo de alerta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    alertLevels.forEach { level ->
                        val isSelected = level.equals(stepState.selectedAlertLevel, ignoreCase = true)
                        val activeColor = when (level) {
                            "Información" -> PrimaryBlue
                            "Advertencia" -> WarningAmber
                            else -> DangerRed
                        }
                        val activeBg = when (level) {
                            "Información" -> AlertInfoBg
                            "Advertencia" -> AlertWarningBg
                            else -> AlertDangerBg
                        }
                        val alertIcon = when (level) {
                            "Información" -> Icons.Default.Info
                            "Advertencia" -> Icons.Default.Warning
                            else -> Icons.Default.Bolt
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAlertLevelSelected(level) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) activeBg else BackgroundDark,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) activeColor else BorderDark
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = alertIcon,
                                    contentDescription = level,
                                    tint = activeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = level,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) activeColor else TextPrimary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stepState.alertText,
                    onValueChange = onAlertTextChanged,
                    label = { Text("Texto de la alerta") },
                    placeholder = { Text("ej. No toques los componentes sin desconectar la batería.", color = TextSecondary) },
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = innerFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Switch para activar el botón/checkbox de confirmación opcional
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = BackgroundDark,
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Confirmación del usuario (opcional)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "El usuario debe marcar una casilla antes de avanzar",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = stepState.requiresConfirmation,
                            onCheckedChange = onRequiresConfirmationToggled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceDark
                            )
                        )
                    }
                }

                if (stepState.requiresConfirmation) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = stepState.confirmationText,
                        onValueChange = onConfirmationTextChanged,
                        label = { Text("Texto del checkbox de confirmación") },
                        placeholder = { Text("ej. He desconectado la batería", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = innerFieldColors
                    )
                }
            }
        }
    }
}
