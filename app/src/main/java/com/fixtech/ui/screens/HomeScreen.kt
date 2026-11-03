package com.fixtech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixtech.ui.theme.BackgroundDark
import com.fixtech.ui.theme.PrimaryBlue
import com.fixtech.ui.theme.SurfaceDark
import com.fixtech.ui.theme.TextPrimary
import com.fixtech.ui.theme.TextSecondary

data class DispositivoMock(
    val nombre: String,
    val tipo: String,
    val guiasDisponibles: Int,
    val estado: String
)

@Composable
fun HomeScreen() {
    val dispositivos = listOf(
        DispositivoMock("Laptop Lenovo ThinkPad T14", "Laptop", 5, "Mantenimiento Preventivo"),
        DispositivoMock("PC Gamer Custom RGB", "PC de Escritorio", 8, "Limpieza y Pasta Térmica"),
        DispositivoMock("Laptop Dell XPS 15", "Laptop", 4, "Cambio de Batería"),
        DispositivoMock("Workstation HP Z4", "PC de Escritorio", 6, "Actualización de Componentes")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "FixTech",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Dispositivos y Guías de Mantenimiento",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Selecciona un equipo para revisar las guías paso a paso.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(dispositivos) { dispositivo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceDark
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dispositivo.nombre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = dispositivo.tipo,
                                fontSize = 12.sp,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = dispositivo.estado,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${dispositivo.guiasDisponibles} guías disponibles",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}
