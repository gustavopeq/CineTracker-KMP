package features.details.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.ContentCast
import common.domain.models.content.DetailedContent
import common.domain.models.content.GenericContent
import common.domain.models.content.Videos
import common.domain.models.list.ListItem
import common.domain.models.person.PersonImage
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import database.repository.SettingsRepository
import features.details.domain.DetailsInteractor
import features.details.events.DetailsEvents
import features.details.state.DetailsSnackbarState
import features.watchlist.ui.model.DefaultLists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsViewModel(
    val contentId: Int,
    val mediaType: MediaType,
    private val detailsInteractor: DetailsInteractor,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _loadState: MutableStateFlow<DataLoadStatus> = MutableStateFlow(
        DataLoadStatus.Loading
    )
    val loadState: StateFlow<DataLoadStatus> get() = _loadState

    private val _contentDetails: MutableStateFlow<DetailedContent?> =
        MutableStateFlow(null)
    val contentDetails: StateFlow<DetailedContent?> get() = _contentDetails

    private val _contentCredits: MutableStateFlow<List<ContentCast>> = MutableStateFlow(emptyList())
    val contentCredits: StateFlow<List<ContentCast>> get() = _contentCredits

    private val _contentVideos: MutableStateFlow<List<Videos>> = MutableStateFlow(emptyList())
    val contentVideos: StateFlow<List<Videos>> get() = _contentVideos

    private val _contentSimilar: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList()
    )
    val contentSimilar: StateFlow<List<GenericContent>> get() = _contentSimilar

    private val _personCredits: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList()
    )
    val personCredits: StateFlow<List<GenericContent>> get() = _personCredits

    private val _personImages: MutableStateFlow<List<PersonImage>> = MutableStateFlow(emptyList())
    val personImages: StateFlow<List<PersonImage>> get() = _personImages

    private val _contentInListStatus = MutableStateFlow(
        mapOf(
            Pair(DefaultLists.WATCHLIST.listId, false),
            Pair(DefaultLists.WATCHED.listId, false)
        )
    )
    val contentInListStatus: StateFlow<Map<Int, Boolean>> get() = _contentInListStatus

    private val _personalRating = MutableStateFlow<Float?>(null)
    val personalRating: StateFlow<Float?> get() = _personalRating

    private val _detailsFailedLoading: MutableState<Boolean> = mutableStateOf(false)
    val detailsFailedLoading: MutableState<Boolean> get() = _detailsFailedLoading

    private val _snackbarState: MutableState<DetailsSnackbarState> = mutableStateOf(
        DetailsSnackbarState()
    )
    val snackbarState: MutableState<DetailsSnackbarState> get() = _snackbarState

    private val _showDetailsOverlay = MutableStateFlow<Boolean?>(null)
    val showDetailsOverlay: StateFlow<Boolean?> get() = _showDetailsOverlay

    private var allLists: List<ListItem> = emptyList()

    init {
        collectAllLists()
        collectPersonalRating()
        collectContentInListStatus()
        initFetchDetails()
    }

    private fun collectAllLists() {
        viewModelScope.launch(Dispatchers.IO) {
            detailsInteractor.getAllLists().collectLatest { lists ->
                allLists = lists
            }
        }
    }

    private fun collectPersonalRating() {
        viewModelScope.launch(Dispatchers.IO) {
            detailsInteractor.getPersonalRating(contentId).collectLatest { rating ->
                _personalRating.value = rating
            }
        }
    }

    private fun collectContentInListStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            detailsInteractor.verifyContentInLists(contentId, mediaType).collectLatest { status ->
                _contentInListStatus.value = status
            }
        }
    }

    fun onEvent(event: DetailsEvents) {
        when (event) {
            is DetailsEvents.FetchDetails -> initFetchDetails()
            is DetailsEvents.ToggleContentFromList -> {
                toggleContentFromList(
                    listId = event.listId
                )
            }
            is DetailsEvents.OnError -> resetDetails()
            is DetailsEvents.OnSnackbarDismiss -> {
                snackbarDismiss()
            }
            is DetailsEvents.DismissDetailsOverlay -> dismissDetailsOverlay()
        }
    }

    fun setPersonalRating(rating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            detailsInteractor.setPersonalRating(contentId, mediaType, rating)
        }
    }

    fun removePersonalRating() {
        viewModelScope.launch(Dispatchers.IO) {
            detailsInteractor.removePersonalRating(contentId, mediaType)
        }
    }

    private fun initFetchDetails() {
        _detailsFailedLoading.value = false
        viewModelScope.launch {
            fetchDetails()
            detailsInteractor.getStreamingProviders(contentId, mediaType)
            if (_loadState.value == DataLoadStatus.Success) {
                fetchAdditionalInfo()
            }
        }
    }

    private suspend fun fetchDetails() {
        val detailsState = detailsInteractor.getContentDetailsById(contentId, mediaType)

        if (detailsState.isFailed()) {
            _loadState.value = DataLoadStatus.Failed
        } else {
            _contentDetails.value = detailsState.detailsInfo.value
            updateCachedFieldsInDb()
            fetchCastDetails()
        }
    }

    private suspend fun updateCachedFieldsInDb() {
        val details = _contentDetails.value ?: return
        detailsInteractor.updateCachedFields(
            contentId = contentId,
            mediaType = mediaType,
            title = details.name,
            posterPath = details.posterPath,
            voteAverage = details.rating.toFloat()
        )
    }

    private suspend fun fetchCastDetails() {
        val castDetailsState = detailsInteractor.getContentCastById(contentId, mediaType)
        if (castDetailsState.isFailed()) {
            _loadState.value = DataLoadStatus.Failed
        } else {
            _contentCredits.value = castDetailsState.detailsCast.value
            _loadState.value = DataLoadStatus.Success
            checkDetailsOverlay()
        }
    }

    private suspend fun fetchAdditionalInfo() {
        when (mediaType) {
            MediaType.MOVIE, MediaType.SHOW -> {
                _contentVideos.value = detailsInteractor.getContentVideosById(
                    contentId,
                    mediaType
                )
                _contentSimilar.value = detailsInteractor.getRecommendationsContentById(
                    contentId,
                    mediaType
                )
            }
            MediaType.PERSON -> {
                _personCredits.value = detailsInteractor.getPersonCreditsById(contentId)
                _personImages.value = detailsInteractor.getPersonImages(contentId)
            }
            else -> {}
        }
    }

    private fun toggleContentFromList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentStatus = _contentInListStatus.value[listId] ?: false
            val details = _contentDetails.value

            detailsInteractor.toggleWatchlist(
                currentStatus = currentStatus,
                contentId = contentId,
                mediaType = mediaType,
                listId = listId,
                title = details?.name.orEmpty(),
                posterPath = details?.posterPath,
                voteAverage = details?.rating?.toFloat() ?: 0f
            )
            _snackbarState.value = DetailsSnackbarState(
                listId = listId,
                addedItem = !currentStatus
            ).apply { setSnackbarVisible() }
        }
    }

    private fun snackbarDismiss() {
        _snackbarState.value.setSnackbarGone()
    }

    private fun resetDetails() {
        _loadState.value = DataLoadStatus.Loading
        _detailsFailedLoading.value = true
    }

    private fun checkDetailsOverlay() {
        if (mediaType != MediaType.PERSON && !settingsRepository.hasSeenDetailsOverlay()) {
            _showDetailsOverlay.value = true
        } else {
            _showDetailsOverlay.value = false
        }
    }

    private fun dismissDetailsOverlay() {
        settingsRepository.setDetailsOverlaySeen()
        _showDetailsOverlay.value = false
    }

    fun getAllLists(): List<ListItem> = allLists
}
