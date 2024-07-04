package common.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import database.repository.DatabaseRepository
import features.home.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    private val _movieSortType = MutableStateFlow<SortTypeItem>(SortTypeItem.NowPlaying)
    val movieSortType: StateFlow<SortTypeItem> get() = _movieSortType

    private val _showSortType = MutableStateFlow<SortTypeItem>(SortTypeItem.AiringToday)
    val showSortType: StateFlow<SortTypeItem> get() = _showSortType

    private val _currentMediaTypeSelected = MutableStateFlow(MediaType.MOVIE)
    val currentMediaTypeSelected: StateFlow<MediaType> get() = _currentMediaTypeSelected

    private val _currentScreen = MutableStateFlow(HomeScreen.route())
    val currentScreen: StateFlow<String> get() = _currentScreen

    private val _watchlistSort = MutableStateFlow<MediaType?>(null)
    val watchlistSort: StateFlow<MediaType?> get() = _watchlistSort

    /* Create New List  */
    private val _displayCreateNewList = MutableStateFlow(false)
    val displayCreateNewList: StateFlow<Boolean> get() = _displayCreateNewList

    private val _newListTextFieldValue = mutableStateOf("")
    val newListTextFieldValue: MutableState<String> get() = _newListTextFieldValue

    private val _isDuplicatedListName = MutableStateFlow(false)
    val isDuplicatedListName: StateFlow<Boolean> get() = _isDuplicatedListName

    private val _refreshLists = MutableStateFlow(false)
    val refreshLists: StateFlow<Boolean> get() = _refreshLists

    fun updateSortType(
        sortTypeItem: SortTypeItem,
    ) {
        when (_currentMediaTypeSelected.value) {
            MediaType.MOVIE -> _movieSortType.value = sortTypeItem
            MediaType.SHOW -> _showSortType.value = sortTypeItem
            else -> {}
        }
    }

    fun updateMediaType(
        mediaType: MediaType,
    ) {
        _currentMediaTypeSelected.value = mediaType
    }

    fun updateCurrentScreen(
        screen: String,
    ) {
        _currentScreen.value = screen
    }

    fun updateWatchlistSort(
        mediaType: MediaType?,
    ) {
        _watchlistSort.value = mediaType
    }

    fun updateDisplayCreateNewList(
        open: Boolean,
    ) {
        _newListTextFieldValue.value = ""
        _isDuplicatedListName.value = false
        _displayCreateNewList.value = open
    }

    fun updateCreateNewListTextField(
        listName: String,
    ) {
        _newListTextFieldValue.value = listName
        if (_isDuplicatedListName.value) {
            _isDuplicatedListName.value = false
        }
    }

    suspend fun createNewList(
        closeSheet: suspend () -> Unit,
    ) {
        val listCreated = databaseRepository.addNewList(
            listName = _newListTextFieldValue.value,
        )

        if (listCreated) {
            closeSheet()
        } else {
            _isDuplicatedListName.value = true
        }
    }

    fun setRefreshLists(
        shouldRefresh: Boolean,
    ) {
        _refreshLists.value = shouldRefresh
    }
}
