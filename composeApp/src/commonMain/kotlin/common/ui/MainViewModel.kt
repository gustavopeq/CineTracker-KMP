package common.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import database.repository.DatabaseRepository
import database.repository.SettingsRepository
import features.watchlist.ui.model.WatchlistRatingSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WatchlistSort(val mediaType: MediaType? = null, val ratingSort: WatchlistRatingSort? = null)

class MainViewModel(
    private val databaseRepository: DatabaseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _hasSeenOnboarding = MutableStateFlow<Boolean?>(null)
    val hasSeenOnboarding: StateFlow<Boolean?> get() = _hasSeenOnboarding

    init {
        _hasSeenOnboarding.value = settingsRepository.hasCompletedOnboarding()
    }

    fun updateOnboardingUiState() {
        _hasSeenOnboarding.value = true
    }

    private val _movieSortType = MutableStateFlow<SortTypeItem>(SortTypeItem.NowPlaying)
    val movieSortType: StateFlow<SortTypeItem> get() = _movieSortType

    private val _showSortType = MutableStateFlow<SortTypeItem>(SortTypeItem.AiringToday)
    val showSortType: StateFlow<SortTypeItem> get() = _showSortType

    private val _currentMediaTypeSelected = MutableStateFlow(MediaType.MOVIE)
    val currentMediaTypeSelected: StateFlow<MediaType> get() = _currentMediaTypeSelected

    private val _watchlistSort = MutableStateFlow(WatchlistSort())
    val watchlistSort: StateFlow<WatchlistSort> get() = _watchlistSort

    /* Create New List  */
    private val _displayCreateNewList = MutableStateFlow(false)
    val displayCreateNewList: StateFlow<Boolean> get() = _displayCreateNewList

    private val _newListTextFieldValue = mutableStateOf(TextFieldValue())
    val newListTextFieldValue: MutableState<TextFieldValue> get() = _newListTextFieldValue

    private val _isDuplicatedListName = MutableStateFlow(false)
    val isDuplicatedListName: StateFlow<Boolean> get() = _isDuplicatedListName

    fun updateSortType(sortTypeItem: SortTypeItem) {
        when (_currentMediaTypeSelected.value) {
            MediaType.MOVIE -> _movieSortType.value = sortTypeItem
            MediaType.SHOW -> _showSortType.value = sortTypeItem
            else -> {}
        }
    }

    fun updateMediaType(mediaType: MediaType) {
        _currentMediaTypeSelected.value = mediaType
    }

    fun updateWatchlistSort(mediaType: MediaType?) {
        _watchlistSort.value = _watchlistSort.value.copy(mediaType = mediaType)
    }

    fun updateWatchlistRatingSort(ratingSort: WatchlistRatingSort?) {
        _watchlistSort.value = _watchlistSort.value.copy(ratingSort = ratingSort)
    }

    fun updateDisplayCreateNewList(open: Boolean) {
        _newListTextFieldValue.value = TextFieldValue()
        _isDuplicatedListName.value = false
        _displayCreateNewList.value = open
    }

    fun updateCreateNewListTextField(value: TextFieldValue) {
        _newListTextFieldValue.value = value
        if (_isDuplicatedListName.value) {
            _isDuplicatedListName.value = false
        }
    }

    suspend fun createNewList(closeSheet: suspend () -> Unit) {
        val listCreated = databaseRepository.addNewList(
            listName = _newListTextFieldValue.value.text
        )

        if (listCreated) {
            closeSheet()
        } else {
            _isDuplicatedListName.value = true
        }
    }
}
