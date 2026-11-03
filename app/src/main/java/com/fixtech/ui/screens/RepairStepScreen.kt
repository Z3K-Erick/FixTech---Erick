package com.fixtech.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixtech.ui.components.AlertType
import com.fixtech.ui.components.FixTechBottomSheet
import com.fixtech.ui.theme.BackgroundDark
import com.fixtech.ui.theme.PrimaryBlue
import com.fixtech.ui.theme.SurfaceDark
import com.fixtech.ui.theme.TextPrimary
import com.fixtech.ui.theme.TextSecondary
import com.fixtech.ui.theme.WarningYellow

data class StepMock(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val warning: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairStepScreen(
    onNavigateBack: () -> Unit
) {
    val steps = listOf(
        StepMock(
            stepNumber = 1,
            title = "Apagar equipo y retirar alimentación",
            instruction = "Asegúrate de que la laptop o PC esté completamente apagada. Desconecta el cargador o cable de poder y presiona el botón de encendido durante 10 segundos para descargar energía residual.",
            warning = "Nunca manipules componentes internos con el equipo encendido o conectado a la corriente."
        ),
        StepMock(
            stepNumber = 2,
            title = "Retirar tapa posterior / panel lateral",
            instruction = "Con el destornillador Philips de precisión, retira cuidadosamente los tornillos de la carcasa. Utiliza la espátula plástica para destrabar las pestañas perimetrales sin rayar el chasis."
        ),
        StepMock(
            stepNumber = 3,
            title = "Desconectar la batería interna",
            instruction = "Antes de tocar la memoria RAM o placa madre, ubica el conector de la batería interna y desconéctalo tirando suavemente de la pestaña plástica.",
            warning = "El uso de herramientas metálicas directas sobre la placa madre puede ocasionar un cortocircuito."
        ),
        StepMock(
            stepNumber = 4,
            title = "Liberar módulo RAM anterior",
            instruction = "Presiona suavemente los clips metálicos laterales del zócalo SO-DIMM/DIMM hacia afuera. El módulo se elevará automáticamente en un ángulo aproximado de 30 grados para retirarlo con facilidad."
        ),
        StepMock(
            stepNumber = 5,
            title = "Instalar nueva RAM y verificar",
            instruction = "Alinea la muesca de la nueva tarjeta RAM con la llave del zócalo. Insértala en ángulo de 30 grados con firmeza y luego empújala hacia abajo hasta escuchar el clic de fijación de ambos clips."
        )
    )

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var showSheet by remember { mutableStateOf(false) }
    var currentAlertType by remember { mutableStateOf(AlertType.SUCCESS) }

    val currentStep = steps[currentStepIndex]
    val totalSteps = steps.size
    val progress = (currentStepIndex + 1).toFloat() / totalSteps.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paso ${currentStepIndex + 1} de $totalSteps",
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
                actions = {
                    IconButton(onClick = {
                        currentAlertType = AlertType.INFO
                        showSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerta del sistema",
                            tint = PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Barra de progreso lineal superior
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = PrimaryBlue,
                trackColor = SurfaceDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Visor visual del paso (simulación gráfica)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(SurfaceDark, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Diagrama visual del Paso ${currentStep.stepNumber}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título de la instrucción
            Text(
                text = currentStep.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción detallada del paso
            Text(
                text = currentStep.instruction,
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            // Tarjeta de advertencia si el paso lo requiere
            if (currentStep.warning != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningYellow.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentStep.warning,
                            fontSize = 13.sp,
                            color = WarningYellow,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón temporal para simular alerta vía BottomSheet
            OutlinedButton(
                onClick = {
                    currentAlertType = if (currentStepIndex == totalSteps - 1) AlertType.SUCCESS else AlertType.WARNING
                    showSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "🔔 Probar Alerta / BottomSheet",
                    fontSize = 13.sp,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botones de Navegación: Anterior / Siguiente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStepIndex > 0) {
                    OutlinedButton(
                        onClick = { currentStepIndex-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anterior",
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentStepIndex < totalSteps - 1) {
                            currentStepIndex++
                        } else {
                            currentAlertType = AlertType.SUCCESS
                            showSheet = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = if (currentStepIndex < totalSteps - 1) "Siguiente" else "Finalizar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal BottomSheet de Alertas
    if (showSheet) {
        val sheetTitle = when (currentAlertType) {
            AlertType.SUCCESS -> "¡Reparación Completada!"
            AlertType.WARNING -> "Advertencia de Seguridad"
            AlertType.ERROR -> "Error en el Procedimiento"
            AlertType.INFO -> "Información del Sistema"
        }

        val sheetDesc = when (currentAlertType) {
            AlertType.SUCCESS -> "Has completado exitosamente todos los pasos de la guía 'Cambio de Memoria RAM'. Tu equipo está listo para ensamblar."
            AlertType.WARNING -> "Recuerda no forzar la ranura de memoria. Si no encaja con facilidad, comprueba la orientación de la muesca."
            AlertType.ERROR -> "Se ha detectado un módulo incompatible o una inserción incompleta."
            AlertType.INFO -> "Puedes volver a revisar los pasos anteriores o pausar la guía en cualquier momento."
        }

        FixTechBottomSheet(
            title = sheetTitle,
            description = sheetDesc,
            alertType = currentAlertType,
            confirmButtonText = if (currentAlertType == AlertType.SUCCESS) "Volver al Inicio" else "Continuar",
            onDismissRequest = { showSheet = false },
            onConfirm = {
                showSheet = false
                if (currentAlertType == AlertType.SUCCESS) {
                    onNavigateBack()
                }
            }
        )
    }
}
