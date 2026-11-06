package com.fixtech.feature.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.components.FixTechSegmentedControl
import com.fixtech.core.components.FixTechTabs
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.feature.home.viewmodel.HomeViewModel
import com.fixtech.feature.profile.ui.TechProfileContent
import com.fixtech.feature.profile.viewmodel.ProfileViewModel

@Composable
fun TechHomeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToGuideDetail: () -> Unit = {},
    onNavigateToCreateGuide: () -> Unit = {},
    onNavigateToTechProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

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
            // Barra superior: Flecha de regresar + FixTech Admin
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FixTechSegmentedControl compacto con las 3 pestañas exactas: "Mis Equipos" | "Guías" | "Perfil"
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
                    onNavigateToGuideDetail = onNavigateToGuideDetail
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
    onNavigateToGuideDetail: () -> Unit
) {
    val filters = listOf("Todos", "Laptop", "PC Escritorio")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Buscador superior redondeado con ícono de lupa
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
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = SurfaceDark
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtros horizontales tipo chip ("Todos", "Laptop", "PC Escritorio")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryBlue else SurfaceDark,
                    border = if (isSelected) null else BorderStroke(1.dp, TextSecondary.copy(alpha = 0.25f)),
                    modifier = Modifier.clickable { onFilterSelected(filter) }
                ) {
                    Text(
                        text = filter,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta estructural 1
        StructuralGuideCard(
            badgeText = "Laptop",
            title = "Mantenimiento Preventivo",
            subtitle = "Guía estructural de hardware • Paso a paso",
            onClick = onNavigateToGuideDetail
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tarjeta estructural 2
        StructuralGuideCard(
            badgeText = "PC Escritorio",
            title = "Diagnóstico de Hardware",
            subtitle = "Inspección y revisión de componentes base",
            onClick = onNavigateToGuideDetail
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StructuralGuideCard(
    badgeText: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Placeholder de imagen superior con badge en esquina
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BackgroundDark.copy(alpha = 0.65f))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
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
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tarjeta limpia con contenedor de imagen/banner superior (placeholder SurfaceDark de 140.dp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToGuideDetail() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(SurfaceDark)
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
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryBlue.copy(alpha = 0.2f),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Guía Técnica",
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
                        .background(BackgroundDark.copy(alpha = 0.35f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mantenimiento Preventivo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Guía estructural publicada • Paso a paso",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón limpio con un solo signo + que navegue a create_guide
        Button(
            onClick = onNavigateToCreateGuide,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Crear nueva guía",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
