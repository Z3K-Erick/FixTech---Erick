package com.fixtech.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.fixtech.core.ui.theme.WarningAmber
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.presentation.guide.GuideItem
import com.fixtech.presentation.guide.GuideViewModel
import com.fixtech.presentation.profile.ProfileViewModel
import com.fixtech.presentation.profile.UserProfileContent

@Composable
fun HomeScreen(
    onNavigateToDeviceGuides: (String) -> Unit = {},
    onNavigateToGuideDetail: (String) -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToDiagnostic: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory),
    guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory),
    profileViewModel: ProfileViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val guideState by guideViewModel.uiState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // "Mis Equipos" proviene de la consulta Room ordenada por savedAt DESC
    val savedCatalogDevices = uiState.mySavedDevices

    val filteredMyDevices = savedCatalogDevices.filter { device ->
        val q = uiState.deviceSearchQuery.trim()
        q.isEmpty() ||
            device.name.contains(q, ignoreCase = true) ||
            device.model.contains(q, ignoreCase = true)
    }

    val filteredExploreDevices = uiState.catalogDevices.filter { device ->
        val q = uiState.exploreSearchQuery.trim()
        q.isEmpty() ||
            device.name.contains(q, ignoreCase = true) ||
            device.model.contains(q, ignoreCase = true)
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
            // Header superior con el texto "FixTech"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fix",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tech",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FixTechSegmentedControl con las 3 pestañas exactas del usuario: "Mis Equipos" | "Explorar" | "Perfil"
            FixTechSegmentedControl(
                items = FixTechTabs.UserTabs,
                selectedIndex = uiState.selectedTab,
                onItemSelected = { index ->
                    homeViewModel.onTabSelected(index)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    fontSize = 13.sp,
                    color = DangerRed,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            when (uiState.selectedTab) {
                0 -> UserDevicesListSection(
                    isMyDevicesTab = true,
                    searchQuery = uiState.deviceSearchQuery,
                    onSearchQueryChanged = homeViewModel::onDeviceSearchQueryChanged,
                    devices = filteredMyDevices,
                    hasAnySavedDevices = savedCatalogDevices.isNotEmpty(),
                    savedDeviceIds = uiState.savedDeviceIds,
                    guides = guideState.guides,
                    onToggleSave = homeViewModel::toggleSaveDevice,
                    onSelectDevice = { device ->
                        homeViewModel.selectDevice(device.id)
                        guideViewModel.setActiveDevice(device.id)
                        onNavigateToDeviceGuides(device.id)
                    }
                )

                1 -> UserDevicesListSection(
                    isMyDevicesTab = false,
                    searchQuery = uiState.exploreSearchQuery,
                    onSearchQueryChanged = homeViewModel::onExploreSearchQueryChanged,
                    devices = filteredExploreDevices,
                    hasAnySavedDevices = true,
                    savedDeviceIds = uiState.savedDeviceIds,
                    guides = guideState.guides,
                    onToggleSave = homeViewModel::toggleSaveDevice,
                    onSelectDevice = { device ->
                        homeViewModel.selectDevice(device.id)
                        guideViewModel.setActiveDevice(device.id)
                        onNavigateToDeviceGuides(device.id)
                    }
                )

                2 -> UserProfileContent(
                    uiState = profileState,
                    onToggleNotifications = profileViewModel::toggleUserNotifications,
                    onLogout = {
                        profileViewModel.clearSession()
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun UserDevicesListSection(
    isMyDevicesTab: Boolean,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    devices: List<UserDeviceItem>,
    hasAnySavedDevices: Boolean,
    savedDeviceIds: Set<String>,
    guides: List<GuideItem>,
    onToggleSave: (String) -> Unit,
    onSelectDevice: (UserDeviceItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Buscador superior en tiempo real por nombre o modelo
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
                Text(
                    text = "Buscar dispositivo...",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar dispositivo",
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

        Spacer(modifier = Modifier.height(18.dp))

        if (devices.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = PrimaryBlue.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LaptopMac,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val emptyMessage = if (isMyDevicesTab && !hasAnySavedDevices) {
                        "No tienes equipos guardados. Explora y usa el marcador para guardarlos."
                    } else {
                        "No se encontraron dispositivos."
                    }

                    Text(
                        text = emptyMessage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            devices.forEach { device ->
                val guidesCount = maxOf(device.guidesCount, guides.count { it.deviceId == device.id })
                val isSaved = device.isBookmarked || device.id in savedDeviceIds
                CatalogDeviceCard(
                    device = device,
                    guidesCount = guidesCount,
                    isSaved = isSaved,
                    onCardClick = { onSelectDevice(device) },
                    onToggleBookmark = { onToggleSave(device.id) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun CatalogDeviceCard(
    device: UserDeviceItem,
    guidesCount: Int,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val isPc = device.category.equals("PC", ignoreCase = true) ||
        device.category.contains("PC", ignoreCase = true)
    val typeBadgeLabel = if (isPc) "PC" else "Laptop"
    val icon = if (isPc) Icons.Default.DesktopWindows else Icons.Default.LaptopMac
    val guidesLabel = "$guidesCount ${if (guidesCount == 1) "guía" else "guías"}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Foto/placeholder superior con etiqueta de tipo (Laptop o PC) e insignia de "X guías"
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }

                // Etiqueta de tipo (Laptop o PC)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = typeBadgeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Insignia con "X guías"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceDark.copy(alpha = 0.92f),
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
                                .background(WarningAmber, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = guidesLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Contenido inferior con nombre, modelo, "Agregado por [Nombre del Técnico]", Bookmark icon y flecha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${device.model} · $typeBadgeLabel",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Agregado por ${device.authorName}",
                        fontSize = 12.sp,
                        color = TextSecondary.copy(alpha = 0.75f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Quitar de Mis Equipos" else "Guardar en Mis Equipos",
                            tint = if (isSaved) PrimaryBlue else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ver guías del dispositivo",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
