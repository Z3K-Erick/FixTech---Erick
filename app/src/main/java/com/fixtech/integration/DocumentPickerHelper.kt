package com.fixtech.integration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object DocumentPickerHelper {

    fun resolveDisplayName(context: Context, uri: Uri, fallbackName: String): String {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)?.takeIf { it.isNotBlank() }
                } else {
                    null
                }
            } ?: uri.lastPathSegment?.substringAfterLast("/")?.takeIf { it.isNotBlank() } ?: fallbackName
        } catch (_: Exception) {
            fallbackName
        }
    }
}

@Composable
fun rememberDocumentPickerLauncher(
    fallbackFileName: String = "certificado_tecnico.pdf",
    mimeTypes: Array<String> = arrayOf("application/pdf", "image/*"),
    onDocumentSelected: (fileName: String, uriString: String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            val fileName = DocumentPickerHelper.resolveDisplayName(context, uri, fallbackFileName)
            onDocumentSelected(fileName, uri.toString())
        }
    }

    return {
        try {
            launcher.launch(mimeTypes)
        } catch (_: Exception) {
            onDocumentSelected(fallbackFileName, "")
        }
    }
}
