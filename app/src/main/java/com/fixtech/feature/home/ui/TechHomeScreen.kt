package com.fixtech.feature.home.ui

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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.components.FixTechSegmentedControl
import com.fixtech.core.components.FixTechTabs
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.BorderDark
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.feature.guide.viewmodel.GuideViewModel
import com.fixtech.feature.home.viewmodel.HomeViewModel
import com.fixtech.feature.home.viewmodel.UserDeviceItem
import com.fixtech.feature.profile.ui.TechProfileContent
import com.fixtech.feature.profile.viewmodel.ProfileViewModel

@Composable
fun TechHomeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToGuideDetail: () -> Unit = {},
    onNavigateToCreateGuide: () -> Unit = {},
    onNavigateToTechProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    guideViewModel: GuideViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val guideState by guideViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    val techDevices = homeState.catalogDevices.filter { it.authorId == "admin-current" }

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
                        onNavigateToGuideDetail()
                    }
                )

                1 -> TechGuidesTabContent(
                    onNavigateToCreateGuide = onNavigateToCreateGuide,
                    onNavigateToGuideDetail = onNavigateToGuideDetail
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
            val count = guidesCountForDevice(device.id)
            val isPc = device.category.contains("PC", ignoreCase = true)
            StructuralGuideCard(
                badgeText = if (isPc) "PC Escritorio" else "Laptop",
                title = "${device.name} ${device.model}",
                subtitle = "${device.processor} • ${device.ram} • $count ${if (count == 1) "Guía" else "Guías"}",
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
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BorderDark),
                    modifier = Modifier.align(Alignment.TopStart)
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
    onNavigateToCreateGuide: () -> Unit,
    onNavigateToGuideDetail: () -> Unit
) {
    // Pestaña "Guías" del técnico: Sin "Panel de Creación Técnica", solo tarjeta superior del maquetado y botón azul "Subir nueva guía" (con un solo signo +)
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToGuideDetail() },
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
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.Center)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryBlue.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Laptop",
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
                        text = "Mejorar RAM o SSD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lenovo LOQ 15IRH8 • Nivel Intermedio • 3 pasos",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
