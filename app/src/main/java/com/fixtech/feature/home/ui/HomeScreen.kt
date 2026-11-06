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
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fixtech.core.components.FixTechSegmentedControl
import com.fixtech.core.components.FixTechTabs
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.SuccessGreen
import com.fixtech.core.theme.SurfaceDark
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import com.fixtech.core.theme.WarningYellow
import com.fixtech.feature.home.viewmodel.HomeViewModel
import com.fixtech.feature.profile.ui.UserProfileContent
import com.fixtech.feature.profile.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    onNavigateToGuideDetail: () -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
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

            when (uiState.selectedTab) {
                0 -> UserMyDevicesTabContent(
                    searchQuery = uiState.deviceSearchQuery,
                    onSearchQueryChanged = homeViewModel::onDeviceSearchQueryChanged,
                    onNavigateToAddDevice = onNavigateToAddDevice,
                    onNavigateToGuideDetail = onNavigateToGuideDetail
                )

                1 -> UserExploreTabContent(
                    selectedCategory = uiState.exploreSelectedCategory,
                    onCategorySelected = homeViewModel::onExploreCategorySelected,
                    searchQuery = uiState.exploreSearchQuery,
                    onSearchQueryChanged = homeViewModel::onExploreSearchQueryChanged,
                    onNavigateToGuideDetail = onNavigateToGuideDetail
                )

                2 -> UserProfileContent(
                    uiState = profileState,
                    onToggleNotifications = profileViewModel::toggleUserNotifications,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun UserMyDevicesTabContent(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToGuideDetail: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Barra de búsqueda superior ("Buscar equipo...")
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
                Text(
                    text = "Buscar equipo...",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar equipo",
                    tint = TextSecondary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = SurfaceDark
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tarjeta estructural 1: Laptop
        UserDeviceCard(
            badgeText = "Laptop",
            title = "Equipo Portátil Registrado",
            subtitle = "Configuración estructural de hardware • Diagnóstico listo",
            icon = Icons.Default.LaptopMac,
            onClick = onNavigateToGuideDetail
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tarjeta estructural 2: PC
        UserDeviceCard(
            badgeText = "PC",
            title = "Estación de Escritorio",
            subtitle = "Componentes registrados • Mantenimiento preventivo",
            icon = Icons.Default.DesktopWindows,
            onClick = onNavigateToGuideDetail
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón "Agregar equipo" que navega a add_device
        Button(
            onClick = onNavigateToAddDevice,
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
                text = "Agregar equipo",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun UserDeviceCard(
    badgeText: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
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
            // Imagen placeholder superior de 140.dp con badge en la esquina ("Laptop" / "PC")
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
private fun UserExploreTabContent(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onNavigateToGuideDetail: () -> Unit
) {
    if (selectedCategory == null) {
        // Vista de Categorías (2 tarjetas grandes con imagen placeholder: Categoría Laptop y Categoría PC)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Categorías de Hardware",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecciona una categoría para explorar sus guías paso a paso",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExploreCategoryCard(
                badgeText = "Laptop",
                title = "Categoría Laptop",
                subtitle = "Guías de mantenimiento, limpieza y actualización de portátiles",
                icon = Icons.Default.LaptopMac,
                onClick = { onCategorySelected("Laptop") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExploreCategoryCard(
                badgeText = "PC",
                title = "Categoría PC",
                subtitle = "Diagnóstico, ensamble y reemplazo de componentes de escritorio",
                icon = Icons.Default.DesktopWindows,
                onClick = { onCategorySelected("PC") }
            )
        }
    } else {
        // Lista de Guías de la categoría seleccionada
        Column(modifier = Modifier.fillMaxWidth()) {
            // Encabezado para alternar de vuelta a Categorías
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.25f)),
                    modifier = Modifier.clickable { onCategorySelected(null) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a categorías",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Categorías",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Categoría: $selectedCategory",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buscador superior de guías
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Buscar guías en $selectedCategory...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar guías",
                        tint = TextSecondary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de guías con imagen, etiqueta de dificultad, título estructural y clic para ir a guide_detail
            ExploreGuideCard(
                categoryBadge = selectedCategory,
                difficultyBadge = "Básico",
                difficultyColor = SuccessGreen,
                title = "Mantenimiento Preventivo",
                subtitle = "Procedimiento estructural paso a paso • 30 min",
                onClick = onNavigateToGuideDetail
            )

            Spacer(modifier = Modifier.height(14.dp))

            ExploreGuideCard(
                categoryBadge = selectedCategory,
                difficultyBadge = "Intermedio",
                difficultyColor = WarningYellow,
                title = "Actualización de Componentes",
                subtitle = "Inspección e instalación segura de hardware • 45 min",
                onClick = onNavigateToGuideDetail
            )
        }
    }
}

@Composable
private fun ExploreCategoryCard(
    badgeText: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BackgroundDark.copy(alpha = 0.65f))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryBlue.copy(alpha = 0.55f),
                    modifier = Modifier
                        .size(52.dp)
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
                    fontSize = 17.sp,
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
private fun ExploreGuideCard(
    categoryBadge: String,
    difficultyBadge: String,
    difficultyColor: Color,
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

                // Etiqueta de categoría a la izquierda y etiqueta de dificultad a la derecha
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = categoryBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = difficultyColor.copy(alpha = 0.2f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = difficultyBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = difficultyColor,
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
