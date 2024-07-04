package common.util

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.floor

fun calculateCardsPerRow(
    screenWidth: Int,
    minCardSize: Int,
    spacing: Int,
    density: Density,
): Pair<Int, Dp> {
    // Calculate the number of cards that can fit in a row
    val numCardsPerRow =
        floor(
            (screenWidth + spacing).toFloat() /
                (minCardSize),
        ).coerceAtLeast(1f).toInt()

    // Calculate the adjusted size for each card
    val totalSpacing = (numCardsPerRow - 1) * spacing
    val adjustedCardSize = density.run { ((screenWidth - totalSpacing) / numCardsPerRow).toDp() }

    return Pair(numCardsPerRow, adjustedCardSize)
}
