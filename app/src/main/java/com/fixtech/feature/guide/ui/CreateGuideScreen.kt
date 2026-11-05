package com.fixtech.feature.guide.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.ErrorRed
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.core.theme.WarningYellow
import com.fixtech.feature.guide.viewmodel.GuideViewModel
import com.fixtech.feature.guide.viewmodel.StepFormState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateGuideScreen(
    onNavigateBack: () -> Unit = {},
    onPublishGuide: () -> Unit = {},
    guideViewModel: GuideViewModel = viewModel()
) {
    val uiState by guideViewModel.uiState.collectAsState()

    val categories = listOf("Laptop", "PC Escritorio", "Componente")
    val difficulties = listOf("Básico", "Intermedio", "Avanzado")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = SurfaceDark
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
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector/Chips para "Categoría / Tipo de equipo"
            Text(
                text = "Categoría / Tipo de equipo",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == uiState.formSelectedCategory
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PrimaryBlue else SurfaceDark,
                        border = if (isSelected) null else BorderStroke(1.dp, TextSecondary.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { guideViewModel.onFormCategorySelected(category) }
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector/Chips para "Nivel de dificultad"
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
                            .clickable { guideViewModel.onFormDifficultySelected(difficulty) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PrimaryBlue else SurfaceDark,
                        border = if (isSelected) null else BorderStroke(1.dp, TextSecondary.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = difficulty,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
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
                shape = RoundedCornerShape(14.dp),
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
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(26.dp))

            // =========================================================================
            // SECCIÓN 2 - HERRAMIENTAS Y PORTADA
            // =========================================================================
            Text(
                text = "Herramientas y Portada",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo con botón + para agregar "Herramientas necesarias"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.formToolInput,
                    onValueChange = guideViewModel::onFormToolInputChanged,
                    label = { Text("Herramientas necesarias") },
                    placeholder = { Text("Agregar herramienta...", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { guideViewModel.addFormTool() },
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar herramienta",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chips estructurales de herramientas debajo
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.formToolsList.forEachIndexed { index, toolName ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = toolName,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(15.dp)
                                    .clickable { guideViewModel.removeFormTool(index) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Caja con borde punteado/delineado (height = 120.dp) con ícono de imagen/subida y texto "Subir imagen de portada"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(SurfaceDark.copy(alpha = 0.65f), shape = RoundedCornerShape(14.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = PrimaryBlue.copy(alpha = 0.6f),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(14.dp.toPx())
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
                        text = "Formato estructural JPG / PNG",
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
                    onDeleteStep = { guideViewModel.removeStepBlock(index) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón delineado "+ Agregar nuevo paso"
            OutlinedButton(
                onClick = { guideViewModel.addStepBlock() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PrimaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ Agregar nuevo paso",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Final: PrimaryBlue de ancho completo "Publicar guía" que regrese a TechHomeScreen
            Button(
                onClick = onPublishGuide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Publicar guía",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
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
    onDeleteStep: () -> Unit = {}
) {
    val alertLevels = listOf("Informativa", "Precaución", "Peligro eléctrico")

    val innerFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = BackgroundDark,
        unfocusedContainerColor = BackgroundDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BackgroundDark
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(onClick = onDeleteStep) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar paso",
                        tint = ErrorRed
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
                shape = RoundedCornerShape(12.dp),
                colors = innerFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // OutlinedTextField (multilínea, minLines = 3) para "Descripción detallada del paso"
            OutlinedTextField(
                value = stepState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Descripción detallada del paso") },
                placeholder = { Text("Explica claramente las instrucciones de este paso...", color = TextSecondary) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = innerFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Caja compacta para "Subir foto o video del paso"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(BackgroundDark, shape = RoundedCornerShape(12.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = TextSecondary.copy(alpha = 0.4f),
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            ),
                            cornerRadius = CornerRadius(12.dp.toPx())
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
                        modifier = Modifier.size(22.dp)
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

            // Switch o selector de "Incluir alerta de precaución"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningYellow,
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
                        checkedTrackColor = WarningYellow,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = BackgroundDark
                    )
                )
            }

            // Cuando está activo, despliega los chips de nivel de alerta y un campo para el "Texto de la advertencia"
            if (stepState.includeCautionAlert) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alertLevels.forEach { level ->
                        val isSelected = level == stepState.selectedAlertLevel
                        val activeColor = when (level) {
                            "Informativa" -> PrimaryBlue
                            "Precaución" -> WarningYellow
                            else -> ErrorRed
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) activeColor.copy(alpha = 0.22f) else BackgroundDark,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) activeColor else TextSecondary.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.clickable { onAlertLevelSelected(level) }
                        ) {
                            Text(
                                text = level,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) activeColor else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
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
                    shape = RoundedCornerShape(12.dp),
                    colors = innerFieldColors
                )
            }
        }
    }
}
