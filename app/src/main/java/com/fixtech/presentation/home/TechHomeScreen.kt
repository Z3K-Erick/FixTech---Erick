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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fixtech.core.ui.components.FixTechSegmentedControl
import com.fixtech.core.ui.components.FixTechTabs
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.DangerRed
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.presentation.guide.GuideItem
import com.fixtech.presentation.guide.GuideViewModel
import com.fixtech.presentation.profile.ProfileViewModel
import com.fixtech.presentation.profile.TechProfileContent

@Composable
fun TechHomeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToGuideDetail: (String) -> Unit = {},
    onNavigateToCreateGuide: () -> Unit = {},
    onNavigateToTechProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory),
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory),
    profileViewModel: ProfileViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val guideState by guideViewModel.uiState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val techDevices = homeState.catalogDevices.filter { it.authorId == "admin-current" }
    val techGuides = guideState.guides.filter { it.authorId == "admin-current" }

    Scaffold(
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Barra superior: Flecha atrás a la izquierda + "FixTech Admin" (SIN ícono derecho de cerrar sesión)
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

            // FixTechSegmentedControl con las 3 pestañas exactas del técnico: "Mis Equipos" | "Guías" | "Perfil"
            FixTechSegmentedControl(
                items = FixTechTabs.TechTabs,
                selectedIndex = homeState.techSelectedTab,
                onItemSelected = { index ->
                    homeViewModel.onTechTabSelected(index)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (homeState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            if (homeState.errorMessage != null) {
                Text(
                    text = homeState.errorMessage ?: "",
                    fontSize = 13.sp,
                    color = DangerRed,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            when (homeState.techSelectedTab) {
                0 -> TechEquipmentTabContent(
                    searchQuery = homeState.techSearchQuery,
                    onSearchQueryChanged = homeViewModel::onTechSearchQueryChanged,
                    selectedFilter = homeState.techSelectedFilter,
                    onFilterSelected = homeViewModel::onTechFilterSelected,
                    devices = techDevices,
                    guidesCountForDevice = { devId -> guideState.guides.count { it.deviceId == devId } },
                    onNavigateToAddDevice = onNavigateToAddDevice,
                    onSelectDevice = { device ->
                        homeViewModel.selectDevice(device.id)
                        guideViewModel.setActiveDevice(device.id)
                        val firstGuideForDevice = guideState.guides.find { it.deviceId == device.id }
                        if (firstGuideForDevice != null) {
                            guideViewModel.selectGuide(firstGuideForDevice.id, device.id)
                            onNavigateToGuideDetail(firstGuideForDevice.id)
                        } else {
                            onNavigateToGuideDetail(device.id)
                        }
                    }
                )

                1 -> TechGuidesTabContent(
                    guides = techGuides,
                    onNavigateToCreateGuide = onNavigateToCreateGuide,
                    onNavigateToGuideDetail = { guide ->
                        homeViewModel.selectDevice(guide.deviceId)
                        guideViewModel.setActiveDevice(guide.deviceId)
                        guideViewModel.selectGuide(guide.id, guide.deviceId)
                        onNavigateToGuideDetail(guide.id)
                    }
                )

                2 -> TechProfileContent(
                    uiState = profileState,
                    onToggleNotifications = profileViewModel::toggleTechNotifications,
                    onUploadCertificate = profileViewModel::uploadCertificate,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun TechEquipmentTabContent(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    devices: List<UserDeviceItem>,
    guidesCountForDevice: (String) -> Int,
    onNavigateToAddDevice: () -> Unit,
    onSelectDevice: (UserDeviceItem) -> Unit
) {
    val filters = listOf("Todos", "Laptop", "PC Escritorio")

    val filteredDevices = devices.filter { dev ->
        val matchesFilter = when (selectedFilter) {
            "Laptop" -> dev.category.equals("Laptop", ignoreCase = true)
            "PC Escritorio" -> dev.category.contains("PC", ignoreCase = true)
            else -> true
        }
        val q = searchQuery.trim()
        val matchesSearch = q.isEmpty() ||
            dev.name.contains(q, ignoreCase = true) ||
            dev.model.contains(q, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Buscador superior con shape = RoundedCornerShape(10.dp)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
                Text(
                    text = "Buscar en mis equipos o guías...",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = TextSecondary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderDark
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Chips / Selectores con RoundedCornerShape(8.dp), height(38.dp) y Modifier.weight(1f)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable { onFilterSelected(filter) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PrimaryBlue else SurfaceDark,
                    border = if (isSelected) null else BorderStroke(1.dp, BorderDark)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
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

        // Botón para que el Técnico registre un nuevo dispositivo (AddDeviceScreen)
        Button(
            onClick = onNavigateToAddDevice,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agregar Dispositivo",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredDevices.forEach { device ->
            val count = maxOf(device.guidesCount, guidesCountForDevice(device.id))
            val isPc = device.category.contains("PC", ignoreCase = true)
            StructuralGuideCard(
                badgeText = if (isPc) "PC Escritorio" else "Laptop",
                title = "${device.name} ${device.model}",
                subtitle = "${device.processor} • ${device.ram} • $count ${if (count == 1) "Guía" else "Guías"}",
                imageUrl = device.imageUrl,
                isPc = isPc,
                onClick = { onSelectDevice(device) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun StructuralGuideCard(
    badgeText: String,
    title: String,
    subtitle: String,
    imageUrl: String = "",
    isPc: Boolean = false,
    onClick: () -> Unit
) {
    val icon = if (isPc) Icons.Default.DesktopWindows else Icons.Default.LaptopMac
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BackgroundDark.copy(alpha = 0.65f))
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.Center)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TechGuidesTabContent(
    guides: List<GuideItem>,
    onNavigateToCreateGuide: () -> Unit,
    onNavigateToGuideDetail: (GuideItem) -> Unit
) {
    // Pestaña "Guías" del técnico: Sin "Panel de Creación Técnica", tarjetas de guías creadas y botón azul "Subir nueva guía"
    Column(modifier = Modifier.fillMaxWidth()) {
        guides.forEach { guide ->
            val difficultyText = when (guide.difficulty) {
                "Fácil", "Bajo", "Básico" -> "Nivel Básico"
                "Difícil", "Alto", "Avanzado" -> "Nivel Avanzado"
                else -> "Nivel Intermedio"
            }
            val modelDisplay = guide.modelSpec.ifBlank { guide.category }
            val stepsCount = guide.steps.size

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToGuideDetail(guide) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
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
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(44.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryBlue.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, BorderDark),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = guide.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = guide.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$modelDisplay • $difficultyText • $stepsCount ${if (stepsCount == 1) "paso" else "pasos"}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón azul "Subir nueva guía" (con un solo signo +)
        Button(
            onClick = onNavigateToCreateGuide,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Subir nueva guía",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
