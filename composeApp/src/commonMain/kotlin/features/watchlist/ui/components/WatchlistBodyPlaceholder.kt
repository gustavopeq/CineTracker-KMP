package features.watchlist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import common.ui.components.ComponentPlaceholder
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.POSTER_ASPECT_RATIO_MULTIPLY
import common.util.UiConstants.SMALL_MARGIN
import common.util.UiConstants.TEXT_PLACEHOLDER_CORNER_PERCENTAGE
import common.util.UiConstants.WATCHLIST_IMAGE_WIDTH

@Composable
fun WatchlistBodyPlaceholder() {
    val imageWidth = WATCHLIST_IMAGE_WIDTH.dp
    val imageHeight = imageWidth * POSTER_ASPECT_RATIO_MULTIPLY
    LazyColumn(
        contentPadding = PaddingValues(all = SMALL_MARGIN.dp),
    ) {
        items(5) {
            // Card row
            Row {
                // Image
                ComponentPlaceholder(
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageHeight)
                        .clip(
                            RoundedCornerShape(
                                topStart = CARD_ROUND_CORNER.dp,
                                bottomStart = CARD_ROUND_CORNER.dp,
                            ),
                        ),
                )
                // Content Description Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(imageHeight)
                        .padding(all = DEFAULT_PADDING.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Title
                        ComponentPlaceholder(
                            modifier = Modifier
                                .weight(1f)
                                .height(15.dp)
                                .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
                        )
                        Spacer(modifier = Modifier.width(DEFAULT_MARGIN.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // Rating
                    ComponentPlaceholder(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Content Type Tag
                    ComponentPlaceholder(
                        modifier = Modifier
                            .width(50.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(TEXT_PLACEHOLDER_CORNER_PERCENTAGE)),
                    )
                }
            }
            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))
        }
    }
}
