package features.onboarding.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryGreyColor_55
import common.ui.theme.PrimaryYellowColor

private const val ILLUSTRATION_SIZE = 200
private const val GRID_COLS = 3
private const val GRID_ROWS = 3

@Composable
fun DiscoverIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(ILLUSTRATION_SIZE.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val gridSize = canvasWidth * 0.65f
        val gridStartX = (canvasWidth - gridSize) / 2f
        val gridStartY = (canvasHeight - gridSize) / 2f
        val cellPadding = gridSize * 0.06f
        val cellWidth = (gridSize - cellPadding * (GRID_COLS + 1)) / GRID_COLS
        val cellHeight = (gridSize - cellPadding * (GRID_ROWS + 1)) / GRID_ROWS
        val cornerRadius = CornerRadius(6f, 6f)

        // Draw grid of cards
        for (row in 0 until GRID_ROWS) {
            for (col in 0 until GRID_COLS) {
                val x = gridStartX + cellPadding + col * (cellWidth + cellPadding)
                val y = gridStartY + cellPadding + row * (cellHeight + cellPadding)
                val color = if (row == 0 && col == 1) {
                    PrimaryYellowColor.copy(alpha = 0.6f)
                } else {
                    if ((row + col) % 2 == 0) MainBarGreyColor else PrimaryGreyColor_55
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(cellWidth, cellHeight),
                    cornerRadius = cornerRadius
                )
            }
        }

        // Search lens circle
        val lensCenter = Offset(canvasWidth * 0.72f, canvasHeight * 0.28f)
        val lensRadius = canvasWidth * 0.13f
        drawCircle(
            color = PrimaryYellowColor,
            radius = lensRadius,
            center = lensCenter,
            style = Stroke(width = 5f)
        )

        // Search lens handle
        val handleLength = canvasWidth * 0.08f
        val handleStart = Offset(
            lensCenter.x + lensRadius * 0.7f,
            lensCenter.y + lensRadius * 0.7f
        )
        val handleEnd = Offset(
            handleStart.x + handleLength * 0.7f,
            handleStart.y + handleLength * 0.7f
        )
        drawLine(
            color = PrimaryYellowColor,
            start = handleStart,
            end = handleEnd,
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )
    }
}
