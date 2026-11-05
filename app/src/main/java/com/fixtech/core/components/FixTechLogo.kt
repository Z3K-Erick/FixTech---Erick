package com.fixtech.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fixtech.core.theme.PrimaryBlue
import kotlin.math.cos
import kotlin.math.sin

enum class FixTechLogoVariant {
    USER,
    TECH
}

@Composable
fun FixTechLogo(
    modifier: Modifier = Modifier,
    variant: FixTechLogoVariant = FixTechLogoVariant.USER,
    size: Dp = 56.dp,
    showHalo: Boolean = true
) {
    val haloSize = size + 20.dp
    val haloCorner = 22.dp * (haloSize / 76.dp)
    val innerCorner = 16.dp * (size / 56.dp)

    Box(
        modifier = modifier
            .size(if (showHalo) haloSize else size)
            .then(
                if (showHalo) {
                    Modifier.background(
                        color = PrimaryBlue.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(haloCorner)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(innerCorner)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.58f)) {
                val strokeWidth = this.size.minDimension * 0.085f
                val stroke = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )

                val cx = this.size.width / 2f
                val cy = this.size.height / 2f
                val r = this.size.minDimension * 0.44f

                when (variant) {
                    FixTechLogoVariant.TECH -> {
                        // Tuerca hexagonal delineada con círculo central
                        val hexPath = Path().apply {
                            for (i in 0 until 6) {
                                val angle = (i * 60.0) * (Math.PI / 180.0)
                                val x = cx + r * cos(angle).toFloat()
                                val y = cy + r * sin(angle).toFloat()
                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                            }
                            close()
                        }
                        drawPath(path = hexPath, color = Color.White, style = stroke)
                        drawCircle(
                            color = Color.White,
                            radius = r * 0.44f,
                            center = Offset(cx, cy),
                            style = stroke
                        )
                    }

                    FixTechLogoVariant.USER -> {
                        // Llave inglesa delineada inclinada hacia la derecha (/)
                        withTransform({
                            rotate(degrees = 45f, pivot = Offset(cx, cy))
                        }) {
                            val path = Path().apply {
                                val topY = cy - r * 0.85f
                                val bottomY = cy + r * 0.85f
                                val headRadius = r * 0.48f
                                val headCenterY = cy - r * 0.38f
                                val handleWidth = r * 0.22f
                                val ringRadius = r * 0.28f
                                val ringCenterY = cy + r * 0.58f
                                val jawOpeningWidth = r * 0.34f
                                val jawDepth = r * 0.38f

                                moveTo(cx - handleWidth / 2f, ringCenterY - ringRadius * 0.6f)
                                lineTo(cx - handleWidth / 2f, headCenterY + headRadius * 0.5f)
                                cubicTo(
                                    cx - headRadius, headCenterY + headRadius * 0.4f,
                                    cx - headRadius, headCenterY - headRadius * 0.4f,
                                    cx - jawOpeningWidth / 2f - r * 0.12f, topY
                                )
                                lineTo(cx - jawOpeningWidth / 2f, topY)
                                lineTo(cx - jawOpeningWidth / 2f, headCenterY - jawDepth * 0.2f)
                                quadraticTo(
                                    cx, headCenterY + jawDepth * 0.4f,
                                    cx + jawOpeningWidth / 2f, headCenterY - jawDepth * 0.2f
                                )
                                lineTo(cx + jawOpeningWidth / 2f, topY)
                                lineTo(cx + jawOpeningWidth / 2f + r * 0.12f, topY)
                                cubicTo(
                                    cx + headRadius, headCenterY - headRadius * 0.4f,
                                    cx + headRadius, headCenterY + headRadius * 0.4f,
                                    cx + handleWidth / 2f, headCenterY + headRadius * 0.5f
                                )
                                lineTo(cx + handleWidth / 2f, ringCenterY - ringRadius * 0.6f)
                                cubicTo(
                                    cx + ringRadius, ringCenterY,
                                    cx + ringRadius, bottomY,
                                    cx, bottomY
                                )
                                cubicTo(
                                    cx - ringRadius, bottomY,
                                    cx - ringRadius, ringCenterY,
                                    cx - handleWidth / 2f, ringCenterY - ringRadius * 0.6f
                                )
                                close()
                            }
                            drawPath(path = path, color = Color.White, style = stroke)

                            drawCircle(
                                color = Color.White,
                                radius = r * 0.13f,
                                center = Offset(cx, cy + r * 0.58f),
                                style = stroke
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FixTechLogo(
    modifier: Modifier = Modifier,
    isTech: Boolean = false,
    size: Dp = 56.dp,
    showHalo: Boolean = true
) {
    FixTechLogo(
        modifier = modifier,
        variant = if (isTech) FixTechLogoVariant.TECH else FixTechLogoVariant.USER,
        size = size,
        showHalo = showHalo
    )
}
