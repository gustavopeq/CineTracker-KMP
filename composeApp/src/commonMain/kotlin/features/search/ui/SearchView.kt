package features.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.SEARCH_CARDS_WIDTH
import common.domain.util.calculateCardsPerRow
import common.domain.util.dpToPx
import common.domain.util.pxToDp
import common.ui.components.ClassicLoadingIndicator
import common.util.PlatformUtils
import common.util.getScreenSizeInfo
import features.search.events.SearchEvent
import features.search.ui.components.NoResultsFound
import features.search.ui.components.SearchBar
import features.search.ui.components.SearchFiltersRow
import features.search.ui.components.SearchResultsGrid
import features.search.ui.components.SearchTypeFilterItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Search(
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    Search(
        viewModel = koinViewModel(),
        goToDetails = goToDetails,
        goToErrorScreen = goToErrorScreen,
    )
}

@Composable
private fun Search(
    viewModel: SearchViewModel,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchQuery by viewModel.searchQuery
    val searchTypeSelected by viewModel.searchFilterSelected
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val isDebounceActive = viewModel.searchDebounceJob?.isActive ?: false

    val onFilterTypeSelected: (SearchTypeFilterItem) -> Unit = {
        viewModel.onEvent(
            SearchEvent.FilterTypeSelected(it),
        )
    }

//    SetStatusBarColor()

    LaunchedEffect(Unit) {
        if (searchQuery.isNotEmpty() && searchResults.itemCount == 0) {
            viewModel.onEvent(SearchEvent.SearchQuery(searchQuery))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SearchBar(
            viewModel = viewModel,
        )
        if (searchQuery.isNotEmpty()) {
            SearchFiltersRow(
                searchTypeSelected = searchTypeSelected,
                onFilterTypeSelected = onFilterTypeSelected,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = PlatformUtils.isIOS,
                    onClick = {
                        keyboardController?.hide()
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ),
        ) {
            when {
                searchResults.loadState.refresh is LoadState.Loading && searchQuery.isNotEmpty() -> {
                    SearchLoadingIndicator()
                }

                searchResults.loadState.refresh is LoadState.Error -> {
                    viewModel.onEvent(SearchEvent.OnError)
                    goToErrorScreen()
                }

                else -> {
                    SearchBody(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        isDebounceActive = isDebounceActive,
                        keyboardController = keyboardController,
                        goToDetails = goToDetails,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBody(
    searchQuery: String,
    searchResults: LazyPagingItems<GenericContent>,
    isDebounceActive: Boolean,
    keyboardController: SoftwareKeyboardController?,
    goToDetails: (Int, MediaType) -> Unit,
) {
    val density = LocalDensity.current
    val screenWidth = density.run { getScreenSizeInfo().widthPx }
    val spacing = density.run { DEFAULT_PADDING.dp.roundToPx() }
    val minCardSize = pxToDp(SEARCH_CARDS_WIDTH, density)

    val (numCardsPerRow, adjustedCardSize) = calculateCardsPerRow(
        screenWidth,
        dpToPx(minCardSize, density),
        spacing,
        density,
    )

    if (searchResults.itemCount > 0) {
        SearchResultsGrid(
            numCardsPerRow = numCardsPerRow,
            searchResults = searchResults,
            adjustedCardSize = adjustedCardSize,
            keyboardController = keyboardController,
            goToDetails = goToDetails,
        )
    } else if (searchQuery.isNotEmpty() && !isDebounceActive) {
        NoResultsFound()
    }
}

@Composable
private fun SearchLoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        ClassicLoadingIndicator()
        Spacer(modifier = Modifier.weight(0.7f))
    }
}
