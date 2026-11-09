package com.fixtech.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.ui.theme.AlertInfoBg
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SuccessGreen
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.presentation.guide.GuideViewModel

private data class SymptomItem(
    val option: DiagnosticOption,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(
    onNavigateBack: () -> Unit = {},
    onOpenRecommendedGuide: (String) -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory),
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val symptoms = listOf(
        SymptomItem(
            option = DiagnosticOption(
                id = "symptom_thermal",
                title = "Sobrecalentamiento y ruido en ventiladores",
                description = "El equipo se calienta en exceso, hace mucho ruido o se apaga repentinamente.",
                recommendedGuideId = "guide_cleaning"
            ),
            icon = Icons.Default.Thermostat
        ),
        SymptomItem(
            option = DiagnosticOption(
                id = "symptom_slow",
                title = "Lentitud al abrir programas o falta de memoria",
                description = "El sistema tarda mucho en iniciar, se congela con multitarea o requiere más almacenamiento.",
                recommendedGuideId = "guide_ram"
            ),
            icon = Icons.Default.Memory
        ),
        SymptomItem(
            option = DiagnosticOption(
                id = "symptom_battery",
                title = "Poca duración de batería o apagado sin cargador",
                description = "La carga disminuye rápidamente o el portátil solo funciona conectado a la corriente.",
                recommendedGuideId = "guide_battery"
            ),
            icon = Icons.Default.BatteryAlert
        ),
        SymptomItem(
            option = DiagnosticOption(
                id = "symptom_gpu",
                title = "Fallo de señal de video o ruidos en tarjeta gráfica",
                description = "Parpadeo en pantalla, pérdida de señal en monitor o acumulación de polvo en GPU.",
                recommendedGuideId = "guide_gpu"
            ),
            icon = Icons.Default.DesktopWindows
        )
    )

    val detailScenarios = listOf(
        DiagnosticOption(
            id = "detail_load",
            title = "Durante tareas exigentes o uso continuo",
            description = "El síntoma se intensifica al ejecutar software pesado, videojuegos o tras varias horas de uso.",
            recommendedGuideId = ""
        ),
        DiagnosticOption(
            id = "detail_boot",
            title = "Desde el encendido del equipo",
            description = "El problema aparece inmediatamente al presionar el botón de encendido o en reposo.",
            recommendedGuideId = ""
        ),
        DiagnosticOption(
            id = "detail_maintenance",
            title = "Más de 6 meses sin mantenimiento preventivo",
            description = "El equipo no ha sido abierto para limpieza interna ni revisión de componentes recientemente.",
            recommendedGuideId = ""
        )
    )

    val selectedSymptom = symptoms.find { it.option.id == uiState.selectedSymptomId } ?: symptoms.first()
    val selectedDetail = detailScenarios.find { it.id == uiState.selectedDetailId } ?: detailScenarios.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diagnóstico Interactivo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.diagnosticStep > 1) {
                                homeViewModel.previousDiagnosticStep()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
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
            // Indicador de paso del diagnóstico (1 a 3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asistente de diagnóstico",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Paso ${uiState.diagnosticStep} de 3",
                    fontSize = 12.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { uiState.diagnosticStep / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = SurfaceDark
            )

            Spacer(modifier = Modifier.height(22.dp))

            when (uiState.diagnosticStep) {
                1 -> {
                    Text(
                        text = "¿Qué síntoma presenta tu equipo?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Selecciona la opción que mejor describa el problema actual de tu laptop o PC.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    symptoms.forEach { item ->
                        val isSelected = item.option.id == uiState.selectedSymptomId
                        DiagnosticOptionCard(
                            title = item.option.title,
                            description = item.option.description,
                            icon = item.icon,
                            isSelected = isSelected,
                            onClick = { homeViewModel.onSymptomSelected(item.option.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { homeViewModel.nextDiagnosticStep() },
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
                            text = "Continuar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                2 -> {
                    Text(
                        text = "¿En qué situación ocurre el fallo?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Indica la frecuencia o condición en la que has notado este comportamiento.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    detailScenarios.forEach { detail ->
                        val isSelected = detail.id == uiState.selectedDetailId
                        DiagnosticOptionCard(
                            title = detail.title,
                            description = detail.description,
                            icon = Icons.Default.Build,
                            isSelected = isSelected,
                            onClick = { homeViewModel.onDetailSelected(detail.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { homeViewModel.previousDiagnosticStep() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderDark),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Text(
                                text = "Anterior",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = { homeViewModel.nextDiagnosticStep() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Continuar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                else -> {
                    // Paso 3: Resultado del diagnóstico
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreen.copy(alpha = 0.16f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Diagnóstico completado",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SuccessGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Causa identificada:",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedSymptom.option.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Condición reportada: ${selectedDetail.title}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Caja informativa semántica con la recomendación
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = AlertInfoBg),
                        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Solución recomendada",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Sigue nuestra guía paso a paso verificada para resolver este síntoma con las herramientas adecuadas y alertas de seguridad.",
                                    fontSize = 12.sp,
                                    color = TextPrimary.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón principal para abrir GuideDetailScreen
                    Button(
                        onClick = {
                            guideViewModel.selectGuide(selectedSymptom.option.recommendedGuideId)
                            onOpenRecommendedGuide(selectedSymptom.option.recommendedGuideId)
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
                            text = "Ver guía de reparación",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { homeViewModel.resetDiagnostic() },
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
                        Text(
                            text = "Realizar otro diagnóstico",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.12f) else SurfaceDark
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else BackgroundDark,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryBlue else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryBlue,
                    unselectedColor = TextSecondary
                )
            )
        }
    }
}
