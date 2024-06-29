package features.home.ui.components.carousel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.UiConstants.CAROUSEL_CARDS_WIDTH
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.ui.components.card.ImageContentCard
import org.jetbrains.compose.resources.StringResource

@Composable
fun ComingSoonCarousel(
    carouselHeaderRes: StringResource,
    comingSoonList: List<GenericContent>,
    currentScreenWidth: Float,
    goToDetails: (Int, MediaType) -> Unit,
) {
    if (comingSoonList.isNotEmpty()) {
        ClassicCarousel(
            carouselHeaderRes = carouselHeaderRes,
            itemList = comingSoonList,
            currentScreenWidth = currentScreenWidth,
            goToDetails = goToDetails,
        ) { item, goToDetails ->
            ImageContentCard(
                modifier = Modifier.padding(
                    top = DEFAULT_PADDING.dp,
                    bottom = DEFAULT_PADDING.dp,
                    end = DEFAULT_PADDING.dp,
                ),
                item = item,
                adjustedCardSize = CAROUSEL_CARDS_WIDTH.dp,
                goToDetails = goToDetails,
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
    }
}
