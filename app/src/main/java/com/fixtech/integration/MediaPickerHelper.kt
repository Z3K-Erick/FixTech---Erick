package com.fixtech.integration

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.core.ui.theme.BorderDark
import com.fixtech.core.ui.theme.PrimaryBlue
import com.fixtech.core.ui.theme.SurfaceDark
import com.fixtech.core.ui.theme.TextPrimary
import com.fixtech.core.ui.theme.TextSecondary
import com.fixtech.core.ui.theme.WarningAmber
import java.io.File
import java.io.FileOutputStream

object MediaPickerHelper {

    const val CAMERA_PERMISSION_DENIED_MESSAGE =
        "Se requiere el permiso de cámara para tomar fotografías de los pasos o equipos, pero aún puedes elegir imágenes desde tu galería."

    fun createPersistentImageFile(context: Context, prefix: String = "fixtech_img"): File {
        val mediaDir = File(context.filesDir, "media").apply {
            if (!exists()) mkdirs()
        }
        val sanitizedPrefix = prefix.substringBeforeLast(".").replace(Regex("[^a-zA-Z0-9_-]"), "_")
        return File(mediaDir, "${sanitizedPrefix}_${System.currentTimeMillis()}.jpg")
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun copyUriToInternalStorage(context: Context, sourceUri: Uri, fallbackFileName: String): Pair<String, String> {
        val displayName = CertificateDocumentHelper.resolveDisplayName(context, sourceUri, fallbackFileName)
        return try {
            val destFile = createPersistentImageFile(context, displayName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            val persistentUri = getUriForFile(context, destFile)
            displayName to persistentUri.toString()
        } catch (_: Exception) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    sourceUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            displayName to sourceUri.toString()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMediaPickerLauncher(
    fallbackFileName: String = "procedimiento_captura.jpg",
    mimeType: String = "image/*",
    onPermissionDenied: ((String) -> Unit)? = null,
    onMediaSelected: (fileName: String, uriString: String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraFileName by remember { mutableStateOf(fallbackFileName) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        val capturedUri = pendingCameraUri
        if (isSuccess && capturedUri != null) {
            onMediaSelected(pendingCameraFileName, capturedUri.toString())
        }
    }

    val startCameraCapture = {
        try {
            val photoFile = MediaPickerHelper.createPersistentImageFile(context, fallbackFileName)
            val contentUri = MediaPickerHelper.getUriForFile(context, photoFile)
            pendingCameraFileName = photoFile.name
            pendingCameraUri = contentUri
            takePictureLauncher.launch(contentUri)
        } catch (_: Exception) {
            onPermissionDenied?.invoke("No fue posible inicializar la cámara en este dispositivo.")
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCameraCapture()
        } else {
            onPermissionDenied?.invoke(MediaPickerHelper.CAMERA_PERMISSION_DENIED_MESSAGE)
            showPermissionRationaleDialog = true
        }
    }

    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val (fileName, persistentUriString) = MediaPickerHelper.copyUriToInternalStorage(
                context = context,
                sourceUri = uri,
                fallbackFileName = fallbackFileName
            )
            if (persistentUriString.isNotBlank()) {
                onMediaSelected(fileName, persistentUriString)
            }
        }
    }

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceDark,
            contentColor = TextPrimary,
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
            ) {
                Text(
                    text = "Seleccionar imagen",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Elige cómo deseas agregar la fotografía",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Opción 1: Tomar foto con la cámara
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                startCameraCapture()
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = BackgroundDark,
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(PrimaryBlue.copy(alpha = 0.16f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Tomar foto con la cámara",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tomar foto con la cámara",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Capturar fotografía directamente ahora",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opción 2: Seleccionar de la galería
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            pickVisualMediaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = BackgroundDark,
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(PrimaryBlue.copy(alpha = 0.16f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Elegir de la galería",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Elegir de la galería",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Elegir una imagen guardada en tu dispositivo",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPermissionRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionRationaleDialog = false },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Permiso de cámara requerido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = MediaPickerHelper.CAMERA_PERMISSION_DENIED_MESSAGE,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationaleDialog = false
                        pickVisualMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Elegir de la galería",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPermissionRationaleDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderDark),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text(
                        text = "Cerrar",
                        fontSize = 12.sp
                    )
                }
            }
        )
    }

    return {
        showBottomSheet = true
    }
}
