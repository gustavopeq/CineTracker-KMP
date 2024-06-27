package features.home.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import common.domain.models.util.MediaType
import di.koinViewModel

@Composable
fun Home(
    goToDetails: (Int, MediaType) -> Unit,
    goToWatchlist: () -> Unit,
    goToBrowse: () -> Unit,
    goToErrorScreen: () -> Unit,
) {
    Home()

//    Home(
//        viewModel = koinViewModel(),
//        mainViewModel = koinViewModel(),
//        goToDetails = goToDetails,
//        goToWatchlist = goToWatchlist,
//        goToBrowse = goToBrowse,
//        goToErrorScreen = goToErrorScreen
//    )
}

@Composable
private fun Home(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val trendingMultiList by viewModel.trendingMulti.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(trendingMultiList) {
            Text(text = it.name)
        }
    }
}

//
// @Composable
// private fun Home(
//    viewModel: HomeViewModel,
//    mainViewModel: MainViewModel,
//    goToDetails: (Int, MediaType) -> Unit,
//    goToWatchlist: () -> Unit,
//    goToBrowse: () -> Unit,
//    goToErrorScreen: () -> Unit
// ) {
//    val loadState by viewModel.loadState.collectAsState()
//    val trendingMultiList by viewModel.trendingMulti.collectAsState()
//    val myWatchlist by viewModel.myWatchlist.collectAsState()
//    val trendingPersonList by viewModel.trendingPerson.collectAsState()
//    val moviesComingSoonList by viewModel.moviesComingSoon.collectAsState()
//    val posterWidth = LocalConfiguration.current.screenWidthDp
//    val posterHeight = posterWidth * POSTER_ASPECT_RATIO_MULTIPLY
//
//    LaunchedEffect(Unit) {
//        mainViewModel.updateCurrentScreen(HomeScreen.route())
//        when (loadState) {
//            is DataLoadStatus.Success -> viewModel.onEvent(HomeEvent.ReloadWatchlist)
//            else -> viewModel.onEvent(HomeEvent.LoadHome)
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        when (loadState) {
//            is DataLoadStatus.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    ClassicLoadingIndicator()
//                }
//            }
//            is DataLoadStatus.Failed -> {
//                viewModel.onEvent(HomeEvent.OnError)
//                goToErrorScreen()
//            }
//            else -> {
//                if (trendingMultiList.isNotEmpty()) {
//                    val homePosterUrl = BASE_ORIGINAL_IMAGE_URL + trendingMultiList[0].posterPath
//                    FeaturedBackgroundImage(
//                        imageUrl = homePosterUrl,
//                        posterHeight = posterHeight
//                    )
//                    HomeBody(
//                        posterHeight,
//                        trendingMultiList,
//                        myWatchlist,
//                        trendingPersonList,
//                        moviesComingSoonList,
//                        goToDetails,
//                        goToWatchlist,
//                        goToBrowse
//                    )
//                }
//            }
//        }
//    }
// }
//
// @Composable
// private fun HomeBody(
//    posterHeight: Float,
//    trendingMultiList: List<GenericContent>,
//    myWatchlist: List<GenericContent>,
//    trendingPersonList: List<PersonDetails>,
//    moviesComingSoonList: List<GenericContent>,
//    goToDetails: (Int, MediaType) -> Unit,
//    goToWatchlist: () -> Unit,
//    goToBrowse: () -> Unit
// ) {
//    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
//    val featuredItem = trendingMultiList.firstOrNull()
//    val secondaryTrendingItems = trendingMultiList.drop(1)
//    val secondaryFeaturedItem = trendingMultiList.firstOrNull {
//        val expectedMediaType = when (featuredItem?.mediaType) {
//            MediaType.MOVIE -> it.mediaType == MediaType.SHOW
//            else -> it.mediaType == MediaType.MOVIE
//        }
//        expectedMediaType && !it.backdropPath.isNullOrEmpty()
//    }
//    val trendingPerson = trendingPersonList.firstOrNull {
//        it.knownForDepartment?.isNotEmpty() == true && it.knownFor.size >= 3
//    }
//    val bgOffset = posterHeight * HOME_BACKGROUND_OFFSET_PERCENT
//
//    LazyColumn {
//        item {
//            Spacer(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(bgOffset.dp)
//            )
//        }
//        item {
//            FeaturedInfo(
//                featuredContent = featuredItem,
//                goToDetails = goToDetails
//            )
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.primary)
//            ) {
//                TrendingCarousel(
//                    trendingItems = secondaryTrendingItems,
//                    currentScreenWidth = currentScreenWidth,
//                    goToDetails = goToDetails
//                )
//                WatchlistCarousel(
//                    watchlist = myWatchlist,
//                    currentScreenWidth = currentScreenWidth,
//                    goToDetails = goToDetails,
//                    goToWatchlist = goToWatchlist
//                )
//                SecondaryFeaturedInfo(
//                    featuredItem = secondaryFeaturedItem,
//                    goToDetails = goToDetails
//                )
//                ComingSoonCarousel(
//                    carouselHeaderRes = R.string.coming_soon_header,
//                    comingSoonList = moviesComingSoonList,
//                    currentScreenWidth = currentScreenWidth,
//                    goToDetails = goToDetails
//                )
//                PersonFeaturedInfo(
//                    trendingPerson = trendingPerson,
//                    goToDetails = goToDetails
//                )
//                HomeBrowseButton(
//                    goToBrowse = goToBrowse
//                )
//                Spacer(modifier = Modifier.height(HOME_BOTTOM_END_MARGIN.dp))
//            }
//        }
//    }
// }
//
// @Composable
// fun HomeBrowseButton(
//    goToBrowse: () -> Unit
// ) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = DEFAULT_MARGIN.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))
//        Text(
//            text = stringResource(id = R.string.home_bottom_screen_message),
//            style = MaterialTheme.typography.titleMedium,
//            color = MaterialTheme.colorScheme.onPrimary,
//            textAlign = TextAlign.Center
//        )
//        Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))
//        GenericButton(
//            buttonText = stringResource(id = R.string.home_discover_more_button),
//            onClick = goToBrowse
//        )
//    }
// }
