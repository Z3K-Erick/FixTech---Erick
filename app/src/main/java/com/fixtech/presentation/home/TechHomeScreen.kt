package com.fixtech.presentation.home

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.fixtech.core.ui.components.FixTechSegmentedControl
import com.fixtech.core.ui.components.FixTechTabs
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
import com.fixtech.domain.model.Device
import com.fixtech.domain.model.Guide
import com.fixtech.presentation.guide.GuideItem
import com.fixtech.presentation.guide.GuideViewModel
import com.fixtech.presentation.profile.ProfileViewModel
import com.fixtech.presentation.profile.TechProfileContent

@Composable
fun TechHomeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToDeviceGuides: (String) -> Unit = {},
    onNavigateToGuideDetail: (String) -> Unit = {},
    onNavigateToCreateGuide: () -> Unit = {},
    onNavigateToTechProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory),
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory),
    profileViewModel: ProfileViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val context = LocalContext.current
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val guideState by guideViewModel.uiState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // Únicamente los dispositivos registrados por el técnico actual
    val techDevices = remember(homeState.catalogDevices) {
        homeState.catalogDevices.filter { it.technicianId == Constants.DEFAULT_TECH_ID }
    }

    var devicePendingDeletion by remember { mutableStateOf<Device?>(null) }
    var guidePendingDeletion by remember { mutableStateOf<Guide?>(null) }

    // Diálogo de confirmación para eliminar equipo y sus guías en cascada en Room
    devicePendingDeletion?.let { targetDevice ->
        val associatedGuidesCount = guideState.guides.count { it.deviceId == targetDevice.id }
        AlertDialog(
            onDismissRequest = { devicePendingDeletion = null },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "¿Eliminar este dispositivo?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Se eliminará «${targetDevice.name} ${targetDevice.model}» de Room junto con ${if (associatedGuidesCount == 1) "su 1 guía publicada" else "sus $associatedGuidesCount guías publicadas"} en cascada. Esta acción no se puede deshacer.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.deleteTechDevice(targetDevice.id) {
                            Toast.makeText(
                                context,
                                "Dispositivo y sus guías eliminados",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        devicePendingDeletion = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { devicePendingDeletion = null },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderDark),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BackgroundDark,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // Diálogo de confirmación para eliminar una guía en Room
    guidePendingDeletion?.let { targetGuide ->
        AlertDialog(
            onDismissRequest = { guidePendingDeletion = null },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "¿Eliminar esta guía?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "La guía «${targetGuide.title}» y todos sus pasos se eliminarán permanentemente de Room. No podrás deshacer esta acción.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        guideViewModel.deleteGuide(targetGuide.id) {
                            Toast.makeText(
                                context,
                                "Guía «${targetGuide.title}» eliminada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        guidePendingDeletion = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { guidePendingDeletion = null },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderDark),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BackgroundDark,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

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
            // Barra superior: Flecha atrás a la izquierda + "FixTech Admin"
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

            // FixTechSegmentedControl con las 3 pestañas del técnico: "Mis Equipos" | "Guías" | "Perfil"
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
                    allGuides = guideState.guides,
                    expandedDeviceId = homeState.expandedTechDeviceId,
                    onToggleExpandDevice = homeViewModel::toggleExpandTechDevice,
                    onAddNewDevice = {
                        homeViewModel.startNewDevice()
                        onNavigateToAddDevice()
                    },
                    onEditDevice = { device ->
                        homeViewModel.startEditDevice(device)
                        onNavigateToAddDevice()
                    },
                    onRequestDeleteDevice = { device ->
                        devicePendingDeletion = device
                    },
                    onAddGuideForDevice = { device ->
                        guideViewModel.startNewGuide(
                            preselectedDevice = device,
                            availableTechDevices = techDevices
                        )
                        onNavigateToCreateGuide()
                    },
                    onEditGuide = { guide ->
                        guideViewModel.startEditGuide(
                            guide = guide,
                            availableTechDevices = techDevices
                        )
                        onNavigateToCreateGuide()
                    },
                    onRequestDeleteGuide = { guide ->
                        guidePendingDeletion = guide
                    },
                    onOpenGuideDetail = { guide ->
                        homeViewModel.selectDevice(guide.deviceId)
                        guideViewModel.setActiveDevice(guide.deviceId)
                        guideViewModel.selectGuide(guide.id, guide.deviceId)
                        onNavigateToGuideDetail(guide.id)
                    }
                )

                1 -> TechAllPublishedDevicesTabContent(
                    allDevices = homeState.catalogDevices,
                    allGuides = guideState.guides,
                    onSelectDeviceForGuides = { device ->
                        homeViewModel.selectDevice(device.id)
                        guideViewModel.setActiveDevice(device.id)
                        onNavigateToDeviceGuides(device.id)
                    }
                )

                2 -> TechProfileContent(
                    uiState = profileState,
                    onToggleNotifications = profileViewModel::toggleTechNotifications,
                    onUploadCertificate = profileViewModel::uploadCertificate,
                    onAddCertificate = { title, institution, fileName, localFilePath, mimeType, onSuccess ->
                        profileViewModel.addTechCertificate(
                            title = title,
                            institution = institution,
                            fileName = fileName,
                            localFilePath = localFilePath,
                            mimeType = mimeType,
                            onSuccess = onSuccess
                        )
                    },
                    onDeleteCertificate = profileViewModel::deleteTechCertificate,
                    onDismissCertNotice = profileViewModel::dismissCertNotice,
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
    allGuides: List<GuideItem>,
    expandedDeviceId: String?,
    onToggleExpandDevice: (String) -> Unit,
    onAddNewDevice: () -> Unit,
    onEditDevice: (UserDeviceItem) -> Unit,
    onRequestDeleteDevice: (UserDeviceItem) -> Unit,
    onAddGuideForDevice: (UserDeviceItem) -> Unit,
    onEditGuide: (GuideItem) -> Unit,
    onRequestDeleteGuide: (GuideItem) -> Unit,
    onOpenGuideDetail: (GuideItem) -> Unit
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
            dev.model.contains(q, ignoreCase = true) ||
            allGuides.any { g -> g.deviceId == dev.id && g.title.contains(q, ignoreCase = true) }
        matchesFilter && matchesSearch
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Buscador superior
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

        // Chips de filtro
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
                    shape = RoundedCornerShape(10.dp),
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

        // Botón para Agregar Dispositivo
        Button(
            onClick = onAddNewDevice,
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

        if (filteredDevices.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Text(
                    text = "No tienes dispositivos registrados en esta vista.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            filteredDevices.forEach { device ->
                val deviceGuides = allGuides.filter { it.deviceId == device.id }
                val isExpanded = expandedDeviceId == device.id
                val isPc = device.category.contains("PC", ignoreCase = true)

                TechDeviceAccordionCard(
                    device = device,
                    isPc = isPc,
                    deviceGuides = deviceGuides,
                    isExpanded = isExpanded,
                    onToggleExpand = { onToggleExpandDevice(device.id) },
                    onEditDevice = { onEditDevice(device) },
                    onDeleteDevice = { onRequestDeleteDevice(device) },
                    onAddGuide = { onAddGuideForDevice(device) },
                    onEditGuide = onEditGuide,
                    onDeleteGuide = onRequestDeleteGuide,
                    onOpenGuideDetail = onOpenGuideDetail
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun TechDeviceAccordionCard(
    device: UserDeviceItem,
    isPc: Boolean,
    deviceGuides: List<GuideItem>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEditDevice: () -> Unit,
    onDeleteDevice: () -> Unit,
    onAddGuide: () -> Unit,
    onEditGuide: (GuideItem) -> Unit,
    onDeleteGuide: (GuideItem) -> Unit,
    onOpenGuideDetail: (GuideItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabecera del equipo con miniatura, nombre, modelo/tipo y botones Editar, Eliminar y Acordeón
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (device.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = device.imageUrl,
                            contentDescription = device.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = if (isPc) Icons.Default.DesktopWindows else Icons.Default.LaptopMac,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${device.model} · ${if (isPc) "PC" else "Laptop"} · ${deviceGuides.size} ${if (deviceGuides.size == 1) "guía" else "guías"}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEditDevice,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar dispositivo",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteDevice,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar dispositivo",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Contraer guías" else "Desplegar guías",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Contenido desplegado (Acordeón): Lista de sus guías, botón "Agregar Guía", y Editar/Eliminar por guía
            if (isExpanded) {
                HorizontalDivider(color = BorderDark)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDark.copy(alpha = 0.45f))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GUÍAS (${deviceGuides.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.8.sp
                        )

                        Surface(
                            modifier = Modifier.clickable { onAddGuide() },
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryBlue.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Agregar Guía",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (deviceGuides.isEmpty()) {
                        Text(
                            text = "Sin guías para este equipo.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            deviceGuides.forEach { guide ->
                                val (diffLabel, diffColor) = when (guide.difficulty) {
                                    "Bajo", "Básico", "Fácil" -> "Bajo" to SuccessGreen
                                    "Alto", "Avanzado", "Difícil" -> "Alto" to DangerRed
                                    else -> "Medio" to WarningAmber
                                }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenGuideDetail(guide) },
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = guide.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(diffColor)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = diffLabel,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = diffColor
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "• ${guide.steps.size} ${if (guide.steps.size == 1) "paso" else "pasos"}",
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onEditGuide(guide) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar guía",
                                                tint = TextPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteGuide(guide) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Eliminar guía",
                                                tint = DangerRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TechAllPublishedDevicesTabContent(
    allDevices: List<UserDeviceItem>,
    allGuides: List<GuideItem>,
    onSelectDeviceForGuides: (UserDeviceItem) -> Unit
) {
    // Muestra los equipos de TODOS los técnicos que tienen guías publicadas
    val devicesWithPublishedGuides = allDevices.mapNotNull { device ->
        val count = allGuides.count { it.deviceId == device.id }
        if (count > 0) device to count else null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Toca un equipo para ver sus guías disponibles, igual que lo vería un usuario.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        if (devicesWithPublishedGuides.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Text(
                    text = "No hay equipos con guías publicadas disponibles.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            devicesWithPublishedGuides.forEach { (device, guidesCount) ->
                val isPc = device.category.contains("PC", ignoreCase = true)
                val typeLabel = if (isPc) "PC" else "Laptop"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDeviceForGuides(device) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(BackgroundDark.copy(alpha = 0.65f))
                        ) {
                            if (device.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = device.imageUrl,
                                    contentDescription = device.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = if (isPc) Icons.Default.DesktopWindows else Icons.Default.LaptopMac,
                                    contentDescription = null,
                                    tint = TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .size(44.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BackgroundDark.copy(alpha = 0.88f),
                                border = BorderStroke(1.dp, BorderDark),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(WarningAmber)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$guidesCount ${if (guidesCount == 1) "Guía" else "Guías"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${device.model} · $typeLabel",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Agregado por ${device.authorName}",
                                    fontSize = 11.sp,
                                    color = TextSecondary.copy(alpha = 0.75f)
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Ver guías del equipo",
                                tint = TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}
