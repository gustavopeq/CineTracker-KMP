package features.search.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import app.cash.paging.compose.LazyPagingItems
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.search_error_description_message
import cinetracker_kmp.composeapp.generated.resources.search_error_title_message
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.rememberNestedScrollConnection
import common.ui.components.card.ImageContentCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultsGrid(
    numCardsPerRow: Int,
    searchResults: LazyPagingItems<GenericContent>,
    adjustedCardSize: Dp,
    keyboardController: SoftwareKeyboardController?,
    goToDetails: (Int, MediaType) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(
                rememberNestedScrollConnection {
                    keyboardController?.hide()
                },
            ),
        columns = GridCells.Fixed(numCardsPerRow),
        horizontalArrangement = Arrangement.Center,
    ) {
        items(searchResults.itemCount) { index ->
            val item = searchResults[index]
            item?.let {
                ImageContentCard(
                    item = item,
                    adjustedCardSize = adjustedCardSize,
                    goToDetails = goToDetails,
                )
            }
        }
    }
}

@Composable
fun NoResultsFound() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        Text(
            text = stringResource(resource = Res.string.search_error_title_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(resource = Res.string.search_error_description_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.surface,
        )
        Spacer(modifier = Modifier.weight(0.7f))
    }
}
