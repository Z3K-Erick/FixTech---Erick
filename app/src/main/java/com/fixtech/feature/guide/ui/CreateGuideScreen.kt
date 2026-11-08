package com.fixtech.feature.guide.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.theme.AlertDangerBg
import com.fixtech.core.theme.AlertInfoBg
import com.fixtech.core.theme.AlertWarningBg
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.BorderDark
import com.fixtech.core.theme.DangerRed
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.core.theme.WarningAmber
import com.fixtech.feature.guide.viewmodel.GuideViewModel
import com.fixtech.feature.guide.viewmodel.StepFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGuideScreen(
    onNavigateBack: () -> Unit = {},
    onPublishGuide: () -> Unit = {},
    guideViewModel: GuideViewModel = viewModel()
) {
    val uiState by guideViewModel.uiState.collectAsState()

    val categories = listOf("Laptop", "PC Escritorio")
    val difficulties = listOf("Básico", "Intermedio", "Avanzado")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear nueva guía",
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
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // =========================================================================
            // SECCIÓN 1 - INFORMACIÓN GENERAL
            // =========================================================================
            Text(
                text = "Información General",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // OutlinedTextField para "Título de la guía"
            OutlinedTextField(
                value = uiState.formGuideTitle,
                onValueChange = guideViewModel::onFormGuideTitleChanged,
                label = { Text("Título de la guía") },
                placeholder = { Text("Ingresa el título del procedimiento", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector/Chips para "Categoría / Tipo de equipo" (Laptop, PC Escritorio)
            Text(
                text = "Categoría / Tipo de equipo",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == uiState.formSelectedCategory
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clickable { guideViewModel.onFormCategorySelected(category) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryBlue else SurfaceDark,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderDark)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector/Chips para "Nivel de dificultad" (Básico, Intermedio, Avanzado)
            Text(
                text = "Nivel de dificultad",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                difficulties.forEach { difficulty ->
                    val isSelected = difficulty == uiState.formSelectedDifficulty
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clickable { guideViewModel.onFormDifficultySelected(difficulty) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryBlue else SurfaceDark,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderDark)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = difficulty,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OutlinedTextField para "Tiempo estimado (ej. 30 min)" y "Modelo / Especificación"
            OutlinedTextField(
                value = uiState.formEstimatedTime,
                onValueChange = guideViewModel::onFormEstimatedTimeChanged,
                label = { Text("Tiempo estimado (ej. 30 min)") },
                placeholder = { Text("Tiempo estimado", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.formModelSpec,
                onValueChange = guideViewModel::onFormModelSpecChanged,
                label = { Text("Modelo / Especificación") },
                placeholder = { Text("Modelo o serie aplicable", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(26.dp))

            // =========================================================================
            // SECCIÓN 2 - HERRAMIENTAS Y PORTADA (SIN chips falsos "Herramienta #1" ni "Herramienta #2")
            // =========================================================================
            Text(
                text = "Herramientas y Portada",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Herramientas necesarias",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.formToolInput,
                    onValueChange = guideViewModel::onFormToolInputChanged,
                    placeholder = { Text("Ej. Destornillador Phillips #0", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { guideViewModel.addFormTool() },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
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

            if (uiState.formToolsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
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
                                    modifier = Modifier.size(24.dp)
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

            Spacer(modifier = Modifier.height(18.dp))

            // Caja con borde punteado/delineado (height = 120.dp) para "Subir imagen de portada"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Subir imagen de portada",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Subir imagen de portada",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Formato JPG / PNG",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // =========================================================================
            // SECCIÓN 3 - BLOQUES DINÁMICOS DE PASOS
            // =========================================================================
            Text(
                text = "Pasos del Procedimiento (${uiState.formStepCount})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            uiState.formSteps.forEachIndexed { index, stepState ->
                StepFormCard(
                    stepNumber = index + 1,
                    stepState = stepState,
                    onTitleChanged = { guideViewModel.onStepTitleChanged(index, it) },
                    onDescriptionChanged = { guideViewModel.onStepDescriptionChanged(index, it) },
                    onCautionToggled = { guideViewModel.onStepCautionToggled(index, it) },
                    onAlertLevelSelected = { guideViewModel.onStepAlertLevelSelected(index, it) },
                    onAlertTextChanged = { guideViewModel.onStepAlertTextChanged(index, it) },
                    onRequiresConfirmationToggled = { guideViewModel.onStepRequiresConfirmationToggled(index, it) },
                    onConfirmationTextChanged = { guideViewModel.onStepConfirmationTextChanged(index, it) },
                    onDeleteStep = { guideViewModel.removeStepBlock(index) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón Secundario / Delineado "Agregar nuevo paso" (sin doble signo + +)
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

            // Botón Principal: "Publicar guía"
            Button(
                onClick = {
                    guideViewModel.publishGuide()
                    onPublishGuide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Publicar guía",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StepFormCard(
    stepNumber: Int,
    stepState: StepFormState = StepFormState(),
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onCautionToggled: (Boolean) -> Unit = {},
    onAlertLevelSelected: (String) -> Unit = {},
    onAlertTextChanged: (String) -> Unit = {},
    onRequiresConfirmationToggled: (Boolean) -> Unit = {},
    onConfirmationTextChanged: (String) -> Unit = {},
    onDeleteStep: () -> Unit = {}
) {
    val alertLevels = listOf("Informativa", "Precaución", "Peligro eléctrico")

    val innerFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark
    )

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
            // Encabezado "Paso #X" con ícono de eliminar
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

                IconButton(onClick = onDeleteStep) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar paso",
                        tint = DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OutlinedTextField para "Título del paso"
            OutlinedTextField(
                value = stepState.title,
                onValueChange = onTitleChanged,
                label = { Text("Título del paso") },
                placeholder = { Text("Título de la acción...", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = innerFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // OutlinedTextField para "Descripción detallada del paso"
            OutlinedTextField(
                value = stepState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Descripción detallada del paso") },
                placeholder = { Text("Explica claramente las instrucciones de este paso...", color = TextSecondary) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = innerFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Caja compacta para "Subir foto o video del paso"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
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
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Subir foto o video del paso",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Subir foto o video del paso",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch de "Incluir alerta de precaución"
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
                    Text(
                        text = "Incluir alerta de precaución",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
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

            // 3 opciones de nivel de alerta ("Informativa", "Precaución", "Peligro eléctrico")
            // en un Row con Modifier.weight(1f) cada una, RoundedCornerShape(8.dp), fontSize = 11.sp, maxLines = 1
            if (stepState.includeCautionAlert) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    alertLevels.forEach { level ->
                        val isSelected = level == stepState.selectedAlertLevel
                        val activeColor = when (level) {
                            "Informativa" -> PrimaryBlue
                            "Precaución" -> WarningAmber
                            else -> DangerRed
                        }
                        val activeBg = when (level) {
                            "Informativa" -> AlertInfoBg
                            "Precaución" -> AlertWarningBg
                            else -> AlertDangerBg
                        }
                        val alertIcon = when (level) {
                            "Informativa" -> Icons.Default.Info
                            "Precaución" -> Icons.Default.Warning
                            else -> Icons.Default.Bolt
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAlertLevelSelected(level) },
                            shape = RoundedCornerShape(8.dp),
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
                    label = { Text("Texto de la advertencia") },
                    placeholder = { Text("Escribe la advertencia o precaución del paso...", color = TextSecondary) },
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = innerFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Checkbox obligatorio para bloquear avance hasta que el usuario confirme
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRequiresConfirmationToggled(!stepState.requiresConfirmation) },
                    shape = RoundedCornerShape(10.dp),
                    color = BackgroundDark,
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = stepState.requiresConfirmation,
                            onCheckedChange = onRequiresConfirmationToggled,
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryBlue,
                                uncheckedColor = TextSecondary,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bloquear avance hasta que el usuario confirme (Checkbox obligatorio)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }

                if (stepState.requiresConfirmation) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = stepState.confirmationText,
                        onValueChange = onConfirmationTextChanged,
                        label = { Text("Texto del checkbox de confirmación") },
                        placeholder = { Text("Ej. Batería desconectada", color = TextSecondary) },
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
