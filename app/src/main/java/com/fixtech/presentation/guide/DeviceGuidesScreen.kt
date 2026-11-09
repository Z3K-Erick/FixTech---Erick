package com.fixtech.presentation.guide

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
import com.fixtech.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceGuidesScreen(
    deviceIdArg: String? = null,
    onNavigateBack: () -> Unit = {},
    onSelectGuide: (String) -> Unit = {},
    onViewTechProfile: (String) -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory),
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val guideState by guideViewModel.uiState.collectAsStateWithLifecycle()

    val resolvedDeviceId = deviceIdArg?.takeIf { it.isNotBlank() } ?: homeState.selectedDeviceId

    LaunchedEffect(resolvedDeviceId) {
        homeViewModel.selectDevice(resolvedDeviceId)
        guideViewModel.setActiveDevice(resolvedDeviceId)
    }

    val selectedDevice = homeState.catalogDevices.find { it.id == resolvedDeviceId }
        ?: homeState.catalogDevices.first()

    // Guías pertenecientes al dispositivo obtenidas de Room (ordenadas por Playlist Priority: favoritas primero por pinnedAt DESC)
    val deviceGuides = if (guideState.deviceGuides.isNotEmpty() && guideState.activeDeviceId == selectedDevice.id) {
        guideState.deviceGuides
    } else {
        guideState.guides.filter { guide ->
            guide.deviceId == selectedDevice.id
        }
    }

    val isPc = selectedDevice.category.contains("PC", ignoreCase = true)
    val typeLabel = if (isPc) "PC" else "Laptop"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedDevice.name,
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
            if (guideState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            if (guideState.errorMessage != null) {
                Text(
                    text = guideState.errorMessage ?: "",
                    fontSize = 13.sp,
                    color = DangerRed,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Encabezado con los datos del equipo seleccionado y botón/enlace interactivo "Ver perfil del técnico"
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBlue.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedDevice.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = selectedDevice.imageUrl,
                                    contentDescription = selectedDevice.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = if (isPc) Icons.Default.DesktopWindows else Icons.Default.LaptopMac,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedDevice.name} ${selectedDevice.model}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$typeLabel • Agregado por ${selectedDevice.authorName}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryBlue.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.clickable {
                            homeViewModel.selectPublicTech(selectedDevice.authorId)
                            onViewTechProfile(selectedDevice.authorId)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ver perfil del técnico",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SELECCIONA UN MANTENIMIENTO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (deviceGuides.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Text(
                        text = "No hay guías disponibles para este equipo aún.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                deviceGuides.forEach { guide ->
                    DeviceGuideTaskCard(
                        guide = guide,
                        onSelectGuide = {
                            guideViewModel.selectGuide(guide.id, selectedDevice.id)
                            onSelectGuide(guide.id)
                        },
                        onToggleFavorite = {
                            guideViewModel.toggleFavoriteGuide(
                                guideId = guide.id,
                                deviceId = selectedDevice.id
                            )
                        },
                        onViewTech = {
                            homeViewModel.selectPublicTech(guide.authorId)
                            onViewTechProfile(guide.authorId)
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun DeviceGuideTaskCard(
    guide: GuideItem,
    onSelectGuide: () -> Unit,
    onToggleFavorite: () -> Unit,
    onViewTech: () -> Unit
) {
    val (difficultyLabel, difficultyColor) = when (guide.difficulty) {
        "Fácil", "Bajo", "Básico" -> "Bajo" to SuccessGreen
        "Difícil", "Alto", "Avanzado" -> "Alto" to DangerRed
        else -> "Medio" to WarningAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectGuide() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Foto / Placeholder de la guía
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(BackgroundDark.copy(alpha = 0.65f))
            ) {
                if (guide.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = guide.imageUrl,
                        contentDescription = guide.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }

                // Semáforo / Badge de dificultad (Fácil en verde, Media en amarillo/naranja, Difícil en rojo)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = difficultyColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(difficultyColor, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = difficultyLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = difficultyColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(BackgroundDark, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = guide.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(difficultyColor, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dificultad $difficultyLabel",
                                    fontSize = 12.sp,
                                    color = difficultyColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${guide.steps.size} pasos · ${guide.tools.size} herramientas",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Por ${guide.author}",
                                fontSize = 12.sp,
                                color = TextSecondary.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Ícono de Estrella (Favorito)
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (guide.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (guide.isFavorite) "Quitar de favoritos" else "Marcar como favorita",
                            tint = if (guide.isFavorite) WarningAmber else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón / enlace interactivo "Ver perfil del técnico"
                Row(
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .clickable { onViewTech() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ver perfil del técnico",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}
