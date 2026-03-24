package features.onboarding.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryGreyColor_55
import common.ui.theme.PrimaryYellowColor

private const val ILLUSTRATION_SIZE = 200
private const val BAR_COUNT = 4

@Composable
fun ListsIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(ILLUSTRATION_SIZE.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth * 0.65f
        val barHeight = canvasHeight * 0.09f
        val barSpacing = barHeight * 0.7f
        val totalHeight = BAR_COUNT * barHeight + (BAR_COUNT - 1) * barSpacing
        val startX = (canvasWidth - barWidth) / 2f
        val startY = (canvasHeight - totalHeight) / 2f
        val cornerRadius = CornerRadius(8f, 8f)
        val checkSize = canvasWidth * 0.05f

        for (i in 0 until BAR_COUNT) {
            val y = startY + i * (barHeight + barSpacing)
            val color = when (i) {
                0 -> PrimaryYellowColor
                1 -> MainBarGreyColor
                else -> PrimaryGreyColor_55
            }
            val barWidthMultiplier = when (i) {
                0 -> 1f
                1 -> 0.85f
                2 -> 0.75f
                else -> 0.6f
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(startX, y),
                size = Size(barWidth * barWidthMultiplier, barHeight),
                cornerRadius = cornerRadius
            )

            // Checkmark for first two bars
            if (i < 2) {
                val checkX = startX + barWidth * barWidthMultiplier + canvasWidth * 0.04f
                val checkY = y + barHeight / 2f
                val checkColor = if (i == 0) PrimaryYellowColor else MainBarGreyColor
                val checkPath = Path().apply {
                    moveTo(checkX, checkY)
                    lineTo(checkX + checkSize * 0.5f, checkY + checkSize * 0.5f)
                    lineTo(checkX + checkSize * 1.5f, checkY - checkSize * 0.5f)
                }
                drawPath(
                    path = checkPath,
                    color = checkColor,
                    style = Stroke(
                        width = 4f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Accent circle (bottom-right)
        drawCircle(
            color = PrimaryYellowColor.copy(alpha = 0.2f),
            radius = canvasWidth * 0.1f,
            center = Offset(canvasWidth * 0.82f, canvasHeight * 0.75f)
        )
    }
}
