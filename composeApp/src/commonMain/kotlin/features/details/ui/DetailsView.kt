package features.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavBackStackEntry
import common.domain.models.content.DetailedContent
import common.domain.models.content.GenericContent
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import common.domain.util.Constants.BASE_ORIGINAL_IMAGE_URL
import common.domain.util.UiConstants.BACKGROUND_INDEX
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.DETAILS_TITLE_IMAGE_OFFSET_PERCENT
import common.domain.util.UiConstants.POSTER_ASPECT_RATIO
import common.domain.util.UiConstants.POSTER_ASPECT_RATIO_MULTIPLY
import common.domain.util.UiConstants.SECTION_PADDING
import common.ui.MainViewModel
import common.ui.components.NetworkImage
import common.ui.components.popup.ClassicSnackbar
import common.util.getScreenSizeInfo
import features.details.DetailsScreen
import features.details.DetailsScreen.ARG_CONTENT_ID
import features.details.DetailsScreen.ARG_MEDIA_TYPE
import features.details.events.DetailsEvents
import features.details.ui.components.CastCarousel
import features.details.ui.components.DetailBodyPlaceholder
import features.details.ui.components.DetailsDescriptionBody
import features.details.ui.components.DetailsDescriptionHeader
import features.details.ui.components.DetailsTopBar
import features.details.ui.components.moreoptions.MoreOptionsTab
import features.details.ui.components.moreoptions.PersonMoreOptionsTab
import features.details.ui.components.showall.ShowAllContentList
import features.details.util.mapValueToRange
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Details(
    navBackStackEntry: NavBackStackEntry,
    onBackPress: () -> Unit,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    val contentId = navBackStackEntry.arguments?.getInt(ARG_CONTENT_ID) ?: -1
    val mediaType = MediaType.getType(navBackStackEntry.arguments?.getString(ARG_MEDIA_TYPE))

    Box(modifier = Modifier.fillMaxSize()) {
        Details(
            viewModel = koinViewModel { parametersOf(contentId, mediaType) },
            mainViewModel = koinViewModel(),
            onBackPress = onBackPress,
            goToDetails = goToDetails,
            goToErrorScreen = goToErrorScreen,
        )
    }
}

@Composable
private fun Details(
    viewModel: DetailsViewModel,
    mainViewModel: MainViewModel,
    onBackPress: () -> Unit,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    val contentDetails by viewModel.contentDetails.collectAsState()
    val contentInListStatus by viewModel.contentInListStatus.collectAsState()
    val loadState by viewModel.loadState.collectAsState()
    val detailsFailedLoading by viewModel.detailsFailedLoading
//    val snackbarState by viewModel.snackbarState
    val snackbarHostState = remember { SnackbarHostState() }
    val posterWidth = getScreenSizeInfo().widthDp.value
    val posterHeight = posterWidth * POSTER_ASPECT_RATIO_MULTIPLY
    val personContentList by viewModel.personCredits.collectAsState()
    var showAllScreen by remember { mutableStateOf(false) }
    var showAllMediaType by remember { mutableStateOf(MediaType.MOVIE) }

    var currentTitlePosY by rememberSaveable { mutableFloatStateOf(0f) }
    var initialTitlePosY by rememberSaveable { mutableStateOf<Float?>(null) }

    // Other Lists Panel
    var showOtherListsPanel by remember { mutableStateOf(false) }
    val updateShowOtherListsPanel: (Boolean) -> Unit = {
        showOtherListsPanel = it
    }

    val updateTitlePosition: (Float) -> Unit = { newPosition ->
        if (initialTitlePosY == null) {
            initialTitlePosY = newPosition
            currentTitlePosY = newPosition
        } else {
            currentTitlePosY = newPosition
        }
    }

    val onToggleWatchlist: (Int) -> Unit = { listId ->
        contentDetails?.let {
            viewModel.onEvent(
                DetailsEvents.ToggleContentFromList(
                    listId = listId,
                ),
            )
        }
    }

    val updateShowAllFlag: (Boolean, MediaType) -> Unit = { flag, mediaType ->
        showAllMediaType = mediaType
        showAllScreen = flag
    }

    LaunchedEffect(Unit) {
        mainViewModel.updateCurrentScreen(DetailsScreen.route())

        if (detailsFailedLoading) {
            viewModel.onEvent(
                DetailsEvents.FetchDetails,
            )
        }
    }

//    LaunchedEffect(snackbarState.displaySnackbar.value) {
//        if (snackbarState.displaySnackbar.value && !showOtherListsPanel) {
//            val itemAdded = snackbarState.addedItem
//            val listName = DefaultLists.getListById(snackbarState.listId)
//            listName?.let {
//                val listLocalizedName = context.resources.getString(getListLocalizedName(listName))
//                val message = if (itemAdded) {
//                    context.resources.getString(
//                        R.string.snackbar_item_added_in_list,
//                        listLocalizedName,
//                    )
//                } else {
//                    context.resources.getString(
//                        R.string.snackbar_item_removed_from_list,
//                        listLocalizedName,
//                    )
//                }
//                snackbarHostState.showSnackbar(message)
//                viewModel.onEvent(DetailsEvents.OnSnackbarDismiss)
//            }
//        }
//    }

    if (loadState is DataLoadStatus.Success && showAllScreen) {
        ShowAllContentList(
            showAllMediaType = showAllMediaType,
            contentList = personContentList,
            goToDetails = goToDetails,
            onBackBtnPress = { updateShowAllFlag(false, MediaType.UNKNOWN) },
        )
    } else {
        DetailsTopBar(
            contentTitle = contentDetails?.name.orEmpty(),
            currentHeaderPosY = currentTitlePosY,
            initialHeaderPosY = initialTitlePosY,
            showWatchlistButton = contentDetails?.mediaType != MediaType.PERSON,
            contentInWatchlistStatus = contentInListStatus,
            onBackBtnPress = onBackPress,
            toggleWatchlist = onToggleWatchlist,
            showOtherListsPanel = updateShowOtherListsPanel,
        )

        ClassicSnackbar(
            snackbarHostState = snackbarHostState,
        ) {
            when (loadState) {
                is DataLoadStatus.Loading -> {
                    DetailBodyPlaceholder(posterHeight)
                }

                is DataLoadStatus.Success -> {
                    DetailsBody(
                        posterHeight = posterHeight,
                        viewModel = viewModel,
                        contentDetails = contentDetails,
                        personContentList = personContentList,
                        initialTitlePosY = initialTitlePosY,
                        currentTitlePosY = currentTitlePosY,
                        updateTitlePosition = updateTitlePosition,
                        goToDetails = goToDetails,
                        updateShowAllFlag = updateShowAllFlag,
                    )
                }

                else -> {
                    viewModel.onEvent(DetailsEvents.OnError)
                    goToErrorScreen()
                }
            }
        }

//        if (showOtherListsPanel) {
//            OtherListsBottomSheet(
//                allLists = viewModel.getAllLists(),
//                contentInListStatus = contentInListStatus,
//                onToggleList = onToggleWatchlist,
//                onClosePanel = {
//                    updateShowOtherListsPanel(false)
//                },
//            )
//        }
    }
}

@Composable
private fun DetailsBody(
    posterHeight: Float,
    viewModel: DetailsViewModel,
    contentDetails: DetailedContent?,
    personContentList: List<GenericContent>,
    currentTitlePosY: Float,
    initialTitlePosY: Float?,
    updateTitlePosition: (Float) -> Unit,
    goToDetails: (Int, MediaType) -> Unit,
    updateShowAllFlag: (Boolean, MediaType) -> Unit,
) {
    val contentPosterUrl = BASE_ORIGINAL_IMAGE_URL + contentDetails?.posterPath

    BackgroundPoster(
        posterHeight = posterHeight,
        contentPosterUrl = contentPosterUrl,
        titlePositionY = currentTitlePosY,
        initialTitlePosY = initialTitlePosY,
    )
    contentDetails?.let { details ->
        DetailsComponent(
            posterHeight = posterHeight,
            mediaInfo = details,
            viewModel = viewModel,
            personContentList = personContentList,
            updateTitlePosition = updateTitlePosition,
            goToDetails = goToDetails,
            updateShowAllFlag = updateShowAllFlag,
        )
    }
}

@Composable
private fun DetailsComponent(
    posterHeight: Float,
    mediaInfo: DetailedContent,
    viewModel: DetailsViewModel,
    personContentList: List<GenericContent>,
    updateTitlePosition: (Float) -> Unit,
    goToDetails: (Int, MediaType) -> Unit,
    updateShowAllFlag: (Boolean, MediaType) -> Unit,
) {
    val contentCredits by viewModel.contentCredits.collectAsState()
    val videoList by viewModel.contentVideos.collectAsState()
    val contentSimilarList by viewModel.contentSimilar.collectAsState()
    val personImageList by viewModel.personImages.collectAsState()

    val titleScreenHeight = posterHeight * DETAILS_TITLE_IMAGE_OFFSET_PERCENT

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(titleScreenHeight.dp),
            )
        }
        item {
            DetailsDescriptionHeader(mediaInfo, updateTitlePosition)
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DEFAULT_MARGIN.dp, vertical = 0.dp),
                ) {
                    DetailsDescriptionBody(
                        contentDetails = mediaInfo,
                    )
                    if (contentCredits.isNotEmpty()) {
                        CastCarousel(
                            contentCredits = contentCredits,
                            goToDetails = goToDetails,
                        )
                        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))
                    }

                    when (mediaInfo.mediaType) {
                        MediaType.MOVIE, MediaType.SHOW -> {
                            MoreOptionsTab(
                                videoList = videoList,
                                contentSimilarList = contentSimilarList,
                                goToDetails = goToDetails,
                            )
                        }
                        MediaType.PERSON -> {
                            PersonMoreOptionsTab(
                                contentList = personContentList,
                                personImageList = personImageList,
                                goToDetails = goToDetails,
                                updateShowAllFlag = updateShowAllFlag,
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundPoster(
    posterHeight: Float,
    contentPosterUrl: String,
    titlePositionY: Float,
    initialTitlePosY: Float?,
) {
    val alpha = if (initialTitlePosY != null) {
        titlePositionY.mapValueToRange(initialTitlePosY)
    } else {
        1f
    }

    NetworkImage(
        imageUrl = contentPosterUrl,
        modifier = Modifier
            .fillMaxWidth()
            .height(posterHeight.dp)
            .zIndex(BACKGROUND_INDEX)
            .aspectRatio(POSTER_ASPECT_RATIO),
        alpha = alpha,
    )
}
