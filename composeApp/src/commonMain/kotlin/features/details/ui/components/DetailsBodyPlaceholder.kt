package features.details.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import common.ui.components.ComponentPlaceholder
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_MARGIN
import common.util.UiConstants.SECTION_PADDING
import common.util.UiConstants.SMALL_PADDING
import common.util.UiConstants.TEXT_PLACEHOLDER_CORNER_PERCENTAGE

@Composable
fun DetailBodyPlaceholder(posterHeight: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ComponentPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height((posterHeight * 0.85).dp),
        )
        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))
        TextLinePlaceholder(endPadding = LARGE_MARGIN)
        TextLinePlaceholder(endPadding = LARGE_MARGIN)
        TextLinePlaceholder(endPadding = LARGE_MARGIN)
        TextLinePlaceholder(endPadding = LARGE_MARGIN * 2)
        TextLinePlaceholder(endPadding = LARGE_MARGIN * 3)
        CategoriesPlaceholder()
        CategoriesPlaceholder()
        CategoriesPlaceholder()
        CategoriesPlaceholder()
    }
}

@Composable
private fun TextLinePlaceholder(
    endPadding: Int,
) {
    ComponentPlaceholder(
        modifier = Modifier
            .padding(start = DEFAULT_MARGIN.dp, end = endPadding.dp)
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
    )
    Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
}

@Composable
private fun CategoriesPlaceholder() {
    Spacer(modifier = Modifier.height(SECTION_PADDING.dp))
    ComponentPlaceholder(
        modifier = Modifier
            .padding(start = DEFAULT_MARGIN.dp)
            .width(150.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
    )
    Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
    ComponentPlaceholder(
        modifier = Modifier
            .padding(start = DEFAULT_MARGIN.dp)
            .width(90.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
    )
}
