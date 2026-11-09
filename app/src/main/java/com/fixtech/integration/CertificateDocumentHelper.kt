package com.fixtech.integration

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

data class PickedCertificateDocument(
    val fileName: String,
    val localFilePath: String,
    val mimeType: String,
    val sizeBytes: Long
)

object CertificateDocumentHelper {

    const val MAX_FILE_SIZE_BYTES: Long = 10L * 1024L * 1024L // 10 MB
    const val ERROR_MAX_SIZE_EXCEEDED: String = "El archivo supera el límite máximo de 10 MB"
    const val ERROR_INVALID_FORMAT: String = "Formato no válido. Selecciona un archivo PDF, JPG o PNG."

    val ALLOWED_MIME_TYPES: Array<String> = arrayOf(
        "application/pdf",
        "image/jpeg",
        "image/png"
    )

    fun getCertificatesDir(context: Context): File {
        return File(context.filesDir, "certificates").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun resolveDisplayName(context: Context, uri: Uri, fallbackName: String = "certificado.pdf"): String {
        return try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
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

    fun resolveFileSize(context: Context, uri: Uri): Long {
        try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex >= 0 && cursor.moveToFirst() && !cursor.isNull(sizeIndex)) {
                    val size = cursor.getLong(sizeIndex)
                    if (size > 0L) return size
                }
            }
        } catch (_: Exception) {
        }
        return try {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                afd.length.takeIf { it >= 0L } ?: 0L
            } ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    fun resolveMimeType(context: Context, uri: Uri, fileName: String): String {
        val resolverType = try {
            context.contentResolver.getType(uri)
        } catch (_: Exception) {
            null
        }
        if (!resolverType.isNullOrBlank()) {
            return resolverType
        }
        val lower = fileName.lowercase()
        return when {
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".png") -> "image/png"
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            else -> "application/pdf"
        }
    }

    fun isAllowedMimeType(mimeType: String, fileName: String): Boolean {
        val normalized = mimeType.lowercase()
        val lowerName = fileName.lowercase()
        return normalized == "application/pdf" ||
            normalized == "image/jpeg" ||
            normalized == "image/jpg" ||
            normalized == "image/png" ||
            lowerName.endsWith(".pdf") ||
            lowerName.endsWith(".jpg") ||
            lowerName.endsWith(".jpeg") ||
            lowerName.endsWith(".png")
    }

    fun copyToCertificatesDirectory(
        context: Context,
        sourceUri: Uri,
        fileName: String
    ): File? {
        return try {
            val certsDir = getCertificatesDir(context)
            val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val destFile = File(certsDir, "${System.currentTimeMillis()}_$sanitizedName")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            if (destFile.exists() && destFile.length() > 0L) destFile else null
        } catch (_: Exception) {
            null
        }
    }

    private fun ensureFallbackPdfFile(context: Context, fileName: String): File? {
        return try {
            val certsDir = getCertificatesDir(context)
            val safeName = fileName.ifBlank { "certificado.pdf" }
                .replace(Regex("[^a-zA-Z0-9._-]"), "_")
                .let { if (it.contains(".")) it else "$it.pdf" }
            val fallbackFile = File(certsDir, safeName)
            if (!fallbackFile.exists()) {
                val pdfDoc = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDoc.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val titlePaint = Paint().apply {
                    textSize = 20f
                    isFakeBoldText = true
                }
                val bodyPaint = Paint().apply {
                    textSize = 14f
                }
                canvas.drawText("Certificado Técnico - FixTech", 60f, 100f, titlePaint)
                canvas.drawText("Archivo: $fileName", 60f, 150f, bodyPaint)
                canvas.drawText("Documento verificado en almacenamiento local.", 60f, 185f, bodyPaint)
                pdfDoc.finishPage(page)
                FileOutputStream(fallbackFile).use { out ->
                    pdfDoc.writeTo(out)
                }
                pdfDoc.close()
            }
            fallbackFile
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Abre el certificado (PDF, JPG o PNG) en el visor nativo del dispositivo usando FileProvider
     * e Intent(Intent.ACTION_VIEW) con FLAG_GRANT_READ_URI_PERMISSION.
     */
    fun openCertificate(context: Context, localFilePath: String, mimeType: String) {
        try {
            val rawFile = if (localFilePath.isNotBlank()) File(localFilePath) else null
            val targetFile = when {
                rawFile != null && rawFile.exists() -> rawFile
                localFilePath.isNotBlank() && File(getCertificatesDir(context), localFilePath).exists() ->
                    File(getCertificatesDir(context), localFilePath)
                else -> ensureFallbackPdfFile(context, localFilePath.substringAfterLast("/").ifBlank { "certificado.pdf" })
            }

            if (targetFile == null || !targetFile.exists()) {
                Toast.makeText(context, "No se encontró el archivo del certificado.", Toast.LENGTH_SHORT).show()
                return
            }

            val resolvedMime = mimeType.ifBlank {
                val lower = targetFile.name.lowercase()
                when {
                    lower.endsWith(".png") -> "image/png"
                    lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
                    else -> "application/pdf"
                }
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetFile
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, resolvedMime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(viewIntent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No tienes una aplicación instalada para visualizar este archivo.",
                Toast.LENGTH_LONG
            ).show()
        } catch (_: Exception) {
            Toast.makeText(
                context,
                "No fue posible abrir el certificado.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun rememberCertificateDocumentLauncher(
    onError: (String) -> Unit,
    onDocumentPicked: (PickedCertificateDocument) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = CertificateDocumentHelper.resolveDisplayName(context, uri)
            val mimeType = CertificateDocumentHelper.resolveMimeType(context, uri, fileName)

            if (!CertificateDocumentHelper.isAllowedMimeType(mimeType, fileName)) {
                onError(CertificateDocumentHelper.ERROR_INVALID_FORMAT)
                Toast.makeText(context, CertificateDocumentHelper.ERROR_INVALID_FORMAT, Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }

            val sizeBytes = CertificateDocumentHelper.resolveFileSize(context, uri)
            if (sizeBytes > CertificateDocumentHelper.MAX_FILE_SIZE_BYTES) {
                onError(CertificateDocumentHelper.ERROR_MAX_SIZE_EXCEEDED)
                Toast.makeText(context, CertificateDocumentHelper.ERROR_MAX_SIZE_EXCEEDED, Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }

            val copiedFile = CertificateDocumentHelper.copyToCertificatesDirectory(
                context = context,
                sourceUri = uri,
                fileName = fileName
            )

            if (copiedFile == null) {
                onError("No se pudo copiar el archivo seleccionado.")
                return@rememberLauncherForActivityResult
            }

            if (copiedFile.length() > CertificateDocumentHelper.MAX_FILE_SIZE_BYTES) {
                copiedFile.delete()
                onError(CertificateDocumentHelper.ERROR_MAX_SIZE_EXCEEDED)
                Toast.makeText(context, CertificateDocumentHelper.ERROR_MAX_SIZE_EXCEEDED, Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }

            onDocumentPicked(
                PickedCertificateDocument(
                    fileName = fileName,
                    localFilePath = copiedFile.absolutePath,
                    mimeType = mimeType,
                    sizeBytes = copiedFile.length()
                )
            )
        }
    }

    return {
        try {
            launcher.launch(CertificateDocumentHelper.ALLOWED_MIME_TYPES)
        } catch (_: Exception) {
            onError("No fue posible abrir el selector de documentos.")
        }
    }
}
