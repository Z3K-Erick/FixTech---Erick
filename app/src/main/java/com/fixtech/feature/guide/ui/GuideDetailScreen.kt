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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun GuideDetailScreen(
    onNavigateBack: () -> Unit,
    onStartRepair: () -> Unit,
    guideViewModel: GuideViewModel = viewModel()
) {
    val uiState by guideViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de la guía",
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
            // Box superior de 200.dp como placeholder de la foto del hardware con badge "Laptop" en la esquina
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SurfaceDark, shape = RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.35f),
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = uiState.categoryBadge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Título estructural de la guía
            Text(
                text = uiState.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = uiState.description,
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fila de metadatos (Tiempo estimado, Dificultad, Autor técnico)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetadataChipCard(
                    icon = Icons.Default.Schedule,
                    label = uiState.estimatedTimeLabel,
                    modifier = Modifier.weight(1f)
                )
                MetadataChipCard(
                    icon = Icons.Default.Speed,
                    label = uiState.difficultyLabel,
                    modifier = Modifier.weight(1f)
                )
                MetadataChipCard(
                    icon = Icons.Default.Person,
                    label = uiState.authorLabel,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección "Herramientas necesarias"
            Text(
                text = "Herramientas necesarias",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Contenedores tipo chip/fila con ícono de herramienta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StructuralToolChip(
                    label = "Herramienta #1",
                    modifier = Modifier.weight(1f)
                )
                StructuralToolChip(
                    label = "Herramienta #2",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección "Requisitos previos / Precauciones"
            Text(
                text = "Requisitos previos / Precauciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Caja con fondo oscuro y borde izquierdo/contorno de advertencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1C1B)),
                border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = WarningYellow,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Requisitos previos y precauciones",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verifique las condiciones de seguridad del área de trabajo y desconecte la fuente de energía antes de iniciar.",
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón inferior (PrimaryBlue, ancho completo): "Iniciar guía paso a paso" -> navega a repair_step
            Button(
                onClick = {
                    guideViewModel.resetSteps()
                    onStartRepair()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Iniciar guía paso a paso",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MetadataChipCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StructuralToolChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}
