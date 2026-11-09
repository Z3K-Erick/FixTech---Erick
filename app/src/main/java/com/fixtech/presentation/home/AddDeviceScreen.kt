package com.fixtech.presentation.home

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.fixtech.core.ui.theme.AlertDangerBg
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.DangerRed
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.integration.rememberMediaPickerLauncher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    onNavigateBack: () -> Unit = {},
    onSaveDevice: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory)
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isEditing = uiState.editingDeviceId != null

    val launchDeviceImagePicker = rememberMediaPickerLauncher(
        fallbackFileName = "equipo_foto.jpg",
        mimeType = "image/*",
        onPermissionDenied = { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    ) { fileName, uriString ->
        homeViewModel.onNewDeviceImageSelected(fileName, uriString)
    }

    val ramOptions = listOf("8 GB", "16 GB", "32 GB")
    val storageOptions = listOf("256 GB SSD", "512 GB SSD", "1 TB SSD")
    val processorOptions = listOf("Intel Core", "AMD Ryzen", "Apple Silicon")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SurfaceDark,
        unfocusedContainerColor = SurfaceDark,
        errorContainerColor = SurfaceDark,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        errorTextColor = TextPrimary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        errorLabelColor = DangerRed,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BorderDark,
        errorBorderColor = DangerRed,
        errorCursorColor = DangerRed
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Dispositivo" else "Nuevo Dispositivo",
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AlertDangerBg),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.isDuplicateModelError) {
                                    "Este modelo ya está registrado"
                                } else {
                                    "Falta información obligatoria"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // 1. Nombre y Número de modelo
            Text(
                text = "Identificación del equipo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.newDeviceBrand,
                onValueChange = homeViewModel::onNewDeviceBrandChanged,
                label = { Text("Nombre *") },
                placeholder = { Text("Ej. Lenovo LOQ, Corsair One", color = TextSecondary) },
                isError = uiState.isBrandError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )

            if (uiState.brandErrorText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.brandErrorText ?: "",
                        fontSize = 12.sp,
                        color = DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.newDeviceModel,
                onValueChange = homeViewModel::onNewDeviceModelChanged,
                label = { Text("Número de modelo *") },
                placeholder = { Text("Ej. 15IRH8, i300", color = TextSecondary) },
                isError = uiState.isModelError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors
            )

            if (uiState.modelErrorText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.modelErrorText ?: "",
                        fontSize = 12.sp,
                        color = DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 2. Selección de tipo de equipo (Laptop / PC)
            Text(
                text = "Tipo de equipo *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            val isPcSelected = uiState.newDeviceType.equals("PC", ignoreCase = true) ||
                uiState.newDeviceType.contains("PC", ignoreCase = true)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeviceTypeCard(
                    title = "Laptop",
                    subtitle = "Portátil con batería",
                    icon = Icons.Default.LaptopMac,
                    isSelected = !isPcSelected,
                    onClick = { homeViewModel.onNewDeviceTypeSelected("Laptop") },
                    modifier = Modifier.weight(1f)
                )

                DeviceTypeCard(
                    title = "PC",
                    subtitle = "Torre o All-in-One",
                    icon = Icons.Default.DesktopWindows,
                    isSelected = isPcSelected,
                    onClick = { homeViewModel.onNewDeviceTypeSelected("PC") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 3. Imagen propia del equipo (usando MediaPickerHelper)
            Text(
                text = "Imagen del equipo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark, shape = RoundedCornerShape(10.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = BorderDark,
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                    .clickable { launchDeviceImagePicker() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.newDeviceImageUri.isNotBlank()) {
                    AsyncImage(
                        model = uiState.newDeviceImageUri,
                        contentDescription = "Fotografía del equipo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BackgroundDark.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, BorderDark),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Cambiar imagen",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Subir imagen del equipo",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subir imagen del equipo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Toca para tomar foto con cámara o elegir de la galería",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Selectores de componentes (Chips con RoundedCornerShape(8.dp), height(38.dp), Modifier.weight(1f))
            Text(
                text = "Componentes y Especificaciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComponentSelectorSection(
                label = "Memoria RAM",
                icon = Icons.Default.Memory,
                options = ramOptions,
                selectedOption = uiState.newDeviceRam,
                onOptionSelected = homeViewModel::onNewDeviceRamSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComponentSelectorSection(
                label = "Almacenamiento",
                icon = Icons.Default.Storage,
                options = storageOptions,
                selectedOption = uiState.newDeviceStorage,
                onOptionSelected = homeViewModel::onNewDeviceStorageSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComponentSelectorSection(
                label = "Procesador",
                icon = Icons.Default.Computer,
                options = processorOptions,
                selectedOption = uiState.newDeviceProcessor,
                onOptionSelected = homeViewModel::onNewDeviceProcessorSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Botón Principal "Guardar Dispositivo" / "Actualizar"
            Button(
                onClick = {
                    homeViewModel.saveDevice(
                        onSuccess = {
                            Toast.makeText(
                                context,
                                if (isEditing) "Cambios guardados correctamente" else "Dispositivo creado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            onSaveDevice()
                        }
                    )
                },
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
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Actualizar Dispositivo" else "Guardar Dispositivo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun DeviceTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.16f) else SurfaceDark
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else BorderDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PrimaryBlue else TextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryBlue else TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComponentSelectorSection(
    label: String,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable { onOptionSelected(option) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PrimaryBlue else SurfaceDark,
                    border = if (isSelected) null else BorderStroke(1.dp, BorderDark)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
