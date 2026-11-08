package com.fixtech.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixtech.core.theme.BackgroundDark
import com.fixtech.core.theme.PrimaryBlue
import com.fixtech.core.theme.TextPrimary
import com.fixtech.core.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500L)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // Halo difuminado superior-central del maquetado (bg-primary/15 blur-3xl)
        Canvas(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    radius = size.minDimension / 2f
                ),
                radius = size.minDimension / 2f
            )
        }

        // Bloque central: Logo SVG del maquetado + título "FixTech"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .background(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(116.dp)) {
                    val scale = size.width / 124f

                    // Pantalla de laptop blanca
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(17f * scale, 24f * scale),
                        size = Size(90f * scale, 62f * scale),
                        cornerRadius = CornerRadius(8f * scale, 8f * scale)
                    )
                    // Interior oscuro de la pantalla
                    drawRoundRect(
                        color = BackgroundDark,
                        topLeft = Offset(23f * scale, 30f * scale),
                        size = Size(78f * scale, 50f * scale),
                        cornerRadius = CornerRadius(4f * scale, 4f * scale)
                    )
                    // Base de la laptop
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(16f * scale, 91f * scale),
                        size = Size(92f * scale, 9f * scale),
                        cornerRadius = CornerRadius(4.5f * scale, 4.5f * scale)
                    )
                    // Bisagra
                    drawRoundRect(
                        color = Color(0xFFCBD5E1),
                        topLeft = Offset(54f * scale, 87f * scale),
                        size = Size(16f * scale, 4f * scale),
                        cornerRadius = CornerRadius(2f * scale, 2f * scale)
                    )

                    // Botón Play circular azul
                    drawCircle(
                        color = PrimaryBlue,
                        radius = 14f * scale,
                        center = Offset(46f * scale, 55f * scale)
                    )
                    val playPath = Path().apply {
                        moveTo(42f * scale, 48.5f * scale)
                        lineTo(54f * scale, 55f * scale)
                        lineTo(42f * scale, 61.5f * scale)
                        close()
                    }
                    drawPath(path = playPath, color = Color.White)

                    // Destornillador inclinado a 35 grados
                    withTransform({
                        rotate(degrees = 35f, pivot = Offset(80f * scale, 56f * scale))
                    }) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(74f * scale, 26f * scale),
                            size = Size(12f * scale, 26f * scale),
                            cornerRadius = CornerRadius(6f * scale, 6f * scale)
                        )
                        drawRect(
                            color = PrimaryBlue,
                            topLeft = Offset(74f * scale, 35f * scale),
                            size = Size(12f * scale, 3f * scale)
                        )
                        drawRect(
                            color = PrimaryBlue,
                            topLeft = Offset(74f * scale, 41f * scale),
                            size = Size(12f * scale, 3f * scale)
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(79f * scale, 52f * scale),
                            size = Size(2.5f * scale, 22f * scale),
                            cornerRadius = CornerRadius(1.2f * scale, 1.2f * scale)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Fix",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Tech",
                    color = PrimaryBlue,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Pie de pantalla del maquetado: punto azul + "Mantenimiento tecnológico, paso a paso"
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(PrimaryBlue, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mantenimiento tecnológico, paso a paso",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
