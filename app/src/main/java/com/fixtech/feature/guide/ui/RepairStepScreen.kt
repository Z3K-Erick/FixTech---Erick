package com.fixtech.feature.guide.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.core.theme.WarningYellow
import com.fixtech.feature.guide.viewmodel.GuideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairStepScreen(
    onNavigateBack: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onNextStep: () -> Unit = {},
    onFinishGuide: () -> Unit = {},
    guideViewModel: GuideViewModel = viewModel()
) {
    val uiState by guideViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paso ${uiState.currentStep} de ${uiState.totalSteps}",
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
            // LinearProgressIndicator que avanza según el paso (1 a 4)
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

            // Placeholder de imagen de 200.dp para la foto del paso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SurfaceDark, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Imagen estructural • Paso ${uiState.currentStep}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título estructural del paso
            Text(
                text = "Paso ${uiState.currentStep}: ${uiState.stepTitle}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Texto descriptivo estructural
            Text(
                text = uiState.stepInstruction,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Caja de Alerta/Precaución del paso (según Figma)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1C1B)),
                border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Precaución",
                        tint = WarningYellow,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.cautionTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.cautionDescription,
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fila inferior de botones según el paso actual (1 a 4)
            when (uiState.currentStep) {
                1 -> {
                    // En el Paso 1: Un solo botón ancho PrimaryBlue "Siguiente paso ->"
                    Button(
                        onClick = {
                            guideViewModel.nextStep()
                            onNextStep()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Siguiente paso ->",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                2, 3 -> {
                    // En los Pasos 2 y 3: Dos botones en fila: "<- Anterior" (fondo SurfaceDark) y "Siguiente ->" (PrimaryBlue)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                guideViewModel.previousStep()
                                onPreviousStep()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Text(
                                text = "<- Anterior",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }

                        Button(
                            onClick = {
                                guideViewModel.nextStep()
                                onNextStep()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Siguiente ->",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                else -> {
                    // En el Paso 4 (último): Botón "<- Anterior" y botón PrimaryBlue "Finalizar guía"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                guideViewModel.previousStep()
                                onPreviousStep()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Text(
                                text = "<- Anterior",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }

                        Button(
                            onClick = {
                                guideViewModel.resetSteps()
                                onFinishGuide()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Finalizar guía",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
