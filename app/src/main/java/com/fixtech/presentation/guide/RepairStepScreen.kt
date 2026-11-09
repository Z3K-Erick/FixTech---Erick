package com.fixtech.presentation.guide

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fixtech.core.ui.theme.AlertDangerBg
import com.fixtech.core.ui.theme.AlertInfoBg
import com.fixtech.core.ui.theme.AlertWarningBg
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.DangerRed
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.core.ui.theme.WarningAmber
import com.fixtech.di.FixTechViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairStepScreen(
    guideIdArg: String? = null,
    onNavigateBack: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onNextStep: () -> Unit = {},
    onFinishGuide: () -> Unit = {},
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val uiState by guideViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(guideIdArg) {
        if (!guideIdArg.isNullOrBlank() && guideIdArg != uiState.selectedGuideId) {
            guideViewModel.selectGuide(guideIdArg)
        }
    }

    val isFirstStep = uiState.currentStep <= 1
    val isLastStep = uiState.currentStep >= uiState.totalSteps
    val requiresLock = uiState.stepHasAlert && uiState.stepRequiresConfirmation
    val canProceed = !requiresLock || uiState.isStepAlertConfirmed

    val (alertBg, alertAccent, alertIcon) = when (uiState.stepAlertLevel) {
        "Informativa" -> Triple(AlertInfoBg, PrimaryBlue, Icons.Default.Info)
        "Peligro eléctrico" -> Triple(AlertDangerBg, DangerRed, Icons.Default.Bolt)
        else -> Triple(AlertWarningBg, WarningAmber, Icons.Default.Warning)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            // Barra de progreso real según el paso actual (Paso X de Y)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Paso ${uiState.currentStep} de ${uiState.totalSteps}",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = SurfaceDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor de imagen del paso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.stepImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = uiState.stepImageUrl,
                        contentDescription = uiState.stepTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Imagen del procedimiento • Paso ${uiState.currentStep}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título del paso
            Text(
                text = uiState.stepTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Descripción del paso
            Text(
                text = uiState.stepInstruction,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Caja de alerta semántica del paso (con o sin Checkbox obligatorio de confirmación)
            if (uiState.stepHasAlert && uiState.cautionDescription.isNotBlank()) {
                val boxBgColor = if (requiresLock && uiState.isStepAlertConfirmed) {
                    SurfaceDark
                } else {
                    alertBg
                }
                val boxBorderColor = if (requiresLock && uiState.isStepAlertConfirmed) {
                    BorderDark
                } else {
                    alertAccent.copy(alpha = 0.75f)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = boxBgColor),
                    border = BorderStroke(1.dp, boxBorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = alertIcon,
                                contentDescription = uiState.cautionTitle,
                                tint = if (requiresLock && uiState.isStepAlertConfirmed) TextSecondary else alertAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.cautionTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (requiresLock && uiState.isStepAlertConfirmed) TextSecondary else alertAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.cautionDescription,
                                    fontSize = 13.sp,
                                    color = if (requiresLock && uiState.isStepAlertConfirmed) {
                                        TextSecondary
                                    } else {
                                        TextPrimary.copy(alpha = 0.92f)
                                    },
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Contenedor redondeado clickeable con Checkbox y texto de confirmación cuando requiresConfirmation == true
                        if (uiState.stepRequiresConfirmation) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { guideViewModel.toggleStepAlertConfirmation() },
                                shape = RoundedCornerShape(10.dp),
                                color = BackgroundDark,
                                border = BorderStroke(
                                    1.dp,
                                    if (uiState.isStepAlertConfirmed) PrimaryBlue else BorderDark
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = uiState.isStepAlertConfirmed,
                                        onCheckedChange = { checked ->
                                            guideViewModel.setStepAlertConfirmation(checked)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = PrimaryBlue,
                                            uncheckedColor = TextSecondary,
                                            checkmarkColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uiState.stepConfirmationText.ifBlank { "Batería desconectada" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (uiState.isStepAlertConfirmed) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Barra de botones inferior con regla estricta de bloqueo por Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFirstStep) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderDark),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceDark,
                            contentColor = TextPrimary
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Anterior",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            guideViewModel.previousStep()
                            onPreviousStep()
                        },
                        modifier = Modifier
                            .weight(0.42f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderDark),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceDark,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anterior",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                val buttonLabel = when {
                    !canProceed -> "Confirma para continuar"
                    isLastStep -> "Finalizar"
                    else -> "Siguiente"
                }

                Button(
                    onClick = {
                        if (canProceed) {
                            if (isLastStep) {
                                guideViewModel.resetSteps()
                                onFinishGuide()
                            } else {
                                guideViewModel.nextStep()
                                onNextStep()
                            }
                        }
                    },
                    enabled = canProceed,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF151C2C),
                        disabledContentColor = Color(0xFF64748B)
                    )
                ) {
                    Text(
                        text = buttonLabel,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    if (canProceed) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (isLastStep) Icons.Default.Check else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
