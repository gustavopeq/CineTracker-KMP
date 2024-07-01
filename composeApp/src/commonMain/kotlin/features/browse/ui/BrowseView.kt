package features.browse.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.SortTypeItem
import common.domain.util.UiConstants.BROWSE_CARD_PADDING_HORIZONTAL
import common.domain.util.UiConstants.BROWSE_CARD_PADDING_VERTICAL
import common.domain.util.UiConstants.BROWSE_MIN_CARD_WIDTH
import common.domain.util.UiConstants.BROWSE_SCAFFOLD_EXTRA_HEIGHT
import common.domain.util.UiConstants.BROWSE_SCAFFOLD_HEIGHT_OFFSET
import common.domain.util.UiConstants.BROWSE_SCAFFOLD_HEIGHT_OFFSET_IOS
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.POSTER_ASPECT_RATIO_MULTIPLY
import common.domain.util.UiConstants.SMALL_MARGIN
import common.domain.util.UiConstants.SMALL_PADDING
import common.domain.util.calculateCardsPerRow
import common.domain.util.dpToPx
import common.domain.util.pxToDp
import common.ui.components.ComponentPlaceholder
import common.ui.components.card.DefaultContentCard
import common.ui.theme.RoundCornerShapes
import common.util.PlatformUtils
import common.util.getScreenSizeInfo
import features.browse.events.BrowseEvent
import features.browse.ui.components.CollapsingTabRow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Browse(
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    Browse(
        viewModel = koinViewModel(),
//        mainViewModel = koinViewModel(),
        goToDetails = goToDetails,
        goToErrorScreen = goToErrorScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Browse(
    viewModel: BrowseViewModel,
//    mainViewModel: MainViewModel,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,

) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scaffoldOffset = if (PlatformUtils.isIOS) {
        BROWSE_SCAFFOLD_HEIGHT_OFFSET_IOS
    } else {
        BROWSE_SCAFFOLD_HEIGHT_OFFSET
    }
    val layoutDirection = LocalLayoutDirection.current

//    val currentMediaTypeSelected by mainViewModel.currentMediaTypeSelected.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0, // currentMediaTypeSelected.ordinal,
        pageCount = { 2 },
    )

    val listOfMovies = viewModel.moviePager.collectAsLazyPagingItems()
//    val movieSortType by mainViewModel.movieSortType.collectAsState()

    val listOfShows = viewModel.showPager.collectAsLazyPagingItems()
//    val showSortType by mainViewModel.showSortType.collectAsState()

    LaunchedEffect(Unit) {
//        mainViewModel.updateCurrentScreen(BrowseScreen.route())
    }

//    LaunchedEffect(pagerState.currentPage) {
//        when (pagerState.currentPage) {
//            0 -> mainViewModel.updateMediaType(MediaType.MOVIE)
//            1 -> mainViewModel.updateMediaType(MediaType.SHOW)
//        }
//    }

    Scaffold(
        modifier = Modifier
            .offset(y = (-scaffoldOffset).dp)
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .then(
                /* To account for the scaffold offset, we have to do this calculation to increase
                the scaffold height */
                Modifier.layout { measurable, constraints ->
                    val fixedContentConstraints = constraints.copy(
                        maxHeight = constraints.maxHeight + scaffoldOffset.dp.roundToPx() +
                            BROWSE_SCAFFOLD_EXTRA_HEIGHT,
                    )
                    val placeable = measurable.measure(fixedContentConstraints)

                    layout(
                        width = placeable.width,
                        height = placeable.height + scaffoldOffset.dp.roundToPx() +
                            BROWSE_SCAFFOLD_EXTRA_HEIGHT,
                    ) {
                        placeable.placeRelative(
                            x = 0,
                            y = scaffoldOffset.dp.roundToPx() + BROWSE_SCAFFOLD_EXTRA_HEIGHT,
                        )
                    }
                },
            ),
        topBar = {
            CollapsingTabRow(
                scrollBehavior = scrollBehavior,
                viewModel = viewModel,
                pagerState = pagerState,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding() * 0.95f,
                bottom = innerPadding.calculateBottomPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection),
            ),
        ) {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> {
                        BrowseBody(
                            viewModel = viewModel,
                            mediaType = MediaType.MOVIE,
                            pagingData = listOfMovies,
                            sortTypeItem = SortTypeItem.NowPlaying, // movieSortType,
                            goToDetails = goToDetails,
                            goToErrorScreen = goToErrorScreen,
                        )
                    }
                    1 -> {
                        BrowseBody(
                            viewModel = viewModel,
                            mediaType = MediaType.SHOW,
                            pagingData = listOfShows,
                            sortTypeItem = SortTypeItem.AiringToday, // showSortType,
                            goToDetails = goToDetails,
                            goToErrorScreen = goToErrorScreen,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowseBody(
    viewModel: BrowseViewModel,
    mediaType: MediaType,
    pagingData: LazyPagingItems<GenericContent>,
    sortTypeItem: SortTypeItem,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    LaunchedEffect(sortTypeItem) {
        viewModel.onEvent(BrowseEvent.UpdateSortType(sortTypeItem, mediaType))
    }

    val density = LocalDensity.current
    val screenWidth = density.run { getScreenSizeInfo().widthPx }
    val spacing = density.run { DEFAULT_MARGIN.dp.roundToPx() }
    val minCardSize = pxToDp(BROWSE_MIN_CARD_WIDTH, density)

    val (numCardsPerRow, adjustedCardSize) = calculateCardsPerRow(
        screenWidth,
        dpToPx(minCardSize, density),
        spacing,
        density,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (pagingData.loadState.refresh) {
            is LoadState.Loading -> {
                BrowseBodyPlaceholder(
                    numberOfCards = numCardsPerRow,
                    cardWidth = adjustedCardSize,
                )
            }
            is LoadState.Error -> {
                viewModel.onEvent(BrowseEvent.OnError)
                goToErrorScreen()
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(numCardsPerRow),
                    modifier = Modifier.padding(horizontal = SMALL_MARGIN.dp),
                ) {
                    items(pagingData.itemCount) { index ->
                        val content = pagingData[index]
                        content?.let {
                            DefaultContentCard(
                                modifier = Modifier
                                    .width(adjustedCardSize)
                                    .padding(
                                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
                                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp,
                                    ),
                                cardWidth = adjustedCardSize,
                                imageUrl = content.posterPath,
                                title = content.name,
                                rating = content.rating,
                                goToDetails = { goToDetails(content.id, content.mediaType) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowseBodyPlaceholder(
    numberOfCards: Int,
    cardWidth: Dp,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(numberOfCards),
        modifier = Modifier.padding(horizontal = SMALL_MARGIN.dp),
    ) {
        items(numberOfCards * numberOfCards) {
            Column(
                modifier = Modifier
                    .width(
                        width = cardWidth,
                    )
                    .padding(
                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp,
                    ),
            ) {
                ComponentPlaceholder(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardWidth * POSTER_ASPECT_RATIO_MULTIPLY)
                        .clip(RoundCornerShapes.small),
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                ComponentPlaceholder(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(50.dp)
                        .clip(RoundCornerShapes.extraSmall),
                )
            }
        }
    }
}
