package features.browse.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import common.domain.models.util.MediaType

@Composable
fun Browse(
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Browse")
    }
//    Browse(
//        viewModel = koinViewModel(),
//        mainViewModel = koinViewModel(),
//        goToDetails = goToDetails,
//        goToErrorScreen = goToErrorScreen
//    )
}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Composable
//private fun Browse(
//    viewModel: BrowseViewModel,
//    mainViewModel: MainViewModel,
//    goToDetails: (Int, MediaType) -> Unit,
//    goToErrorScreen: () -> Unit
//
//) {
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
//    val currentMediaTypeSelected by mainViewModel.currentMediaTypeSelected.collectAsState()
//    val pagerState = rememberPagerState(
//        initialPage = currentMediaTypeSelected.ordinal,
//        pageCount = { 2 }
//    )
//
//    val listOfMovies = viewModel.moviePager.collectAsLazyPagingItems()
//    val movieSortType by mainViewModel.movieSortType.collectAsState()
//
//    val listOfShows = viewModel.showPager.collectAsLazyPagingItems()
//    val showSortType by mainViewModel.showSortType.collectAsState()
//
//    LaunchedEffect(Unit) {
//        mainViewModel.updateCurrentScreen(BrowseScreen.route())
//    }
//
//    LaunchedEffect(pagerState.currentPage) {
//        when (pagerState.currentPage) {
//            0 -> mainViewModel.updateMediaType(MediaType.MOVIE)
//            1 -> mainViewModel.updateMediaType(MediaType.SHOW)
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier
//            .offset(y = (-BROWSE_SCAFFOLD_HEIGHT_OFFSET).dp)
//            .fillMaxSize()
//            .nestedScroll(scrollBehavior.nestedScrollConnection)
//            .then(
//                /* To account for the scaffold offset, we have to do this calculation to increase
//                the scaffold height */
//                Modifier.layout { measurable, constraints ->
//                    val fixedContentConstraints = constraints.copy(
//                        maxHeight = constraints.maxHeight +
//                            BROWSE_SCAFFOLD_HEIGHT_OFFSET.dp.roundToPx()
//                    )
//                    val placeable = measurable.measure(fixedContentConstraints)
//
//                    layout(
//                        width = placeable.width,
//                        height = placeable.height + BROWSE_SCAFFOLD_HEIGHT_OFFSET.dp.roundToPx()
//                    ) {
//                        placeable.placeRelative(
//                            x = 0,
//                            y = BROWSE_SCAFFOLD_HEIGHT_OFFSET.dp.roundToPx()
//                        )
//                    }
//                }
//            ),
//        topBar = {
//            CollapsingTabRow(
//                scrollBehavior = scrollBehavior,
//                viewModel = viewModel,
//                pagerState = pagerState
//            )
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            HorizontalPager(state = pagerState) { page ->
//                when (page) {
//                    0 -> {
//                        BrowseBody(
//                            viewModel = viewModel,
//                            mediaType = MediaType.MOVIE,
//                            pagingData = listOfMovies,
//                            sortTypeItem = movieSortType,
//                            goToDetails = goToDetails,
//                            goToErrorScreen = goToErrorScreen
//                        )
//                    }
//                    1 -> {
//                        BrowseBody(
//                            viewModel = viewModel,
//                            mediaType = MediaType.SHOW,
//                            pagingData = listOfShows,
//                            sortTypeItem = showSortType,
//                            goToDetails = goToDetails,
//                            goToErrorScreen = goToErrorScreen
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun BrowseBody(
//    viewModel: BrowseViewModel,
//    mediaType: MediaType,
//    pagingData: LazyPagingItems<GenericContent>,
//    sortTypeItem: SortTypeItem,
//    goToDetails: (Int, MediaType) -> Unit,
//    goToErrorScreen: () -> Unit
//) {
//    LaunchedEffect(sortTypeItem) {
//        viewModel.onEvent(BrowseEvent.UpdateSortType(sortTypeItem, mediaType))
//    }
//
//    val density = LocalDensity.current
//    val screenWidth = density.run { LocalConfiguration.current.screenWidthDp.dp.roundToPx() }
//    val spacing = density.run { DEFAULT_MARGIN.dp.roundToPx() }
//    val minCardSize = pxToDp(BROWSE_MIN_CARD_WIDTH, density)
//
//    val (numCardsPerRow, adjustedCardSize) = calculateCardsPerRow(
//        screenWidth,
//        dpToPx(minCardSize, density),
//        spacing,
//        density
//    )
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        when (pagingData.loadState.refresh) {
//            is LoadState.Loading -> {
//                BrowseBodyPlaceholder(
//                    numberOfCards = numCardsPerRow,
//                    cardWidth = adjustedCardSize
//                )
//            }
//            is LoadState.Error -> {
//                viewModel.onEvent(BrowseEvent.OnError)
//                goToErrorScreen()
//            }
//            else -> {
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(numCardsPerRow),
//                    modifier = Modifier.padding(horizontal = SMALL_MARGIN.dp)
//                ) {
//                    items(pagingData.itemCount) { index ->
//                        val content = pagingData[index]
//                        content?.let {
//                            DefaultContentCard(
//                                modifier = Modifier
//                                    .width(adjustedCardSize)
//                                    .padding(
//                                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
//                                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp
//                                    ),
//                                cardWidth = adjustedCardSize,
//                                imageUrl = content.posterPath,
//                                title = content.name,
//                                rating = content.rating,
//                                goToDetails = { goToDetails(content.id, content.mediaType) }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun BrowseBodyPlaceholder(
//    numberOfCards: Int,
//    cardWidth: Dp
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(numberOfCards),
//        modifier = Modifier.padding(horizontal = SMALL_MARGIN.dp)
//    ) {
//        items(numberOfCards * numberOfCards) {
//            Column(
//                modifier = Modifier
//                    .width(
//                        width = cardWidth
//                    )
//                    .padding(
//                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
//                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp
//                    )
//            ) {
//                ComponentPlaceholder(
//                    modifier = Modifier
//                        .width(cardWidth)
//                        .height(cardWidth * POSTER_ASPECT_RATIO_MULTIPLY)
//                        .clip(RoundCornerShapes.small)
//                )
//                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
//                ComponentPlaceholder(
//                    modifier = Modifier
//                        .width(cardWidth)
//                        .height(50.dp)
//                        .clip(RoundCornerShapes.extraSmall)
//                )
//            }
//        }
//    }
//}
