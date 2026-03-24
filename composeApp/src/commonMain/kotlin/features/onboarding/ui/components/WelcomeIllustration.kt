package features.onboarding.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryGreyColor_55
import common.ui.theme.PrimaryYellowColor

private const val ILLUSTRATION_SIZE = 200

@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(ILLUSTRATION_SIZE.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cardWidth = canvasWidth * 0.4f
        val cardHeight = canvasHeight * 0.55f
        val cornerRadius = CornerRadius(12f, 12f)
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f

        // Back card (rotated left)
        rotate(degrees = -15f, pivot = Offset(centerX, centerY)) {
            drawRoundRect(
                color = PrimaryGreyColor_55,
                topLeft = Offset(centerX - cardWidth / 2f, centerY - cardHeight / 2f),
                size = Size(cardWidth, cardHeight),
                cornerRadius = cornerRadius
            )
        }

        // Middle card (rotated right)
        rotate(degrees = 8f, pivot = Offset(centerX, centerY)) {
            drawRoundRect(
                color = MainBarGreyColor,
                topLeft = Offset(centerX - cardWidth / 2f, centerY - cardHeight / 2f),
                size = Size(cardWidth, cardHeight),
                cornerRadius = cornerRadius
            )
        }

        // Front card (centered)
        drawRoundRect(
            color = PrimaryYellowColor,
            topLeft = Offset(centerX - cardWidth / 2f, centerY - cardHeight / 2f),
            size = Size(cardWidth, cardHeight),
            cornerRadius = cornerRadius
        )

        // Accent circle (top-right)
        drawCircle(
            color = PrimaryYellowColor.copy(alpha = 0.3f),
            radius = canvasWidth * 0.12f,
            center = Offset(centerX + cardWidth * 0.55f, centerY - cardHeight * 0.35f)
        )
    }
}
