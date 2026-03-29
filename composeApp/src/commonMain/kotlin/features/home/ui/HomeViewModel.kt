package features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.GenericContent
import common.domain.models.list.ListItem
import common.domain.models.person.PersonDetails
import common.domain.models.util.DataLoadStatus
import database.backfill.CachedFieldsBackfill
import features.home.domain.HomeInteractor
import features.home.events.HomeEvent
import features.watchlist.domain.ListInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
    private val listInteractor: ListInteractor,
    private val cachedFieldsBackfill: CachedFieldsBackfill
) : ViewModel() {
    private val _loadState: MutableStateFlow<DataLoadStatus> = MutableStateFlow(
        DataLoadStatus.Loading
    )
    val loadState: StateFlow<DataLoadStatus> get() = _loadState

    private val _trendingMulti: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList()
    )
    val trendingMulti: StateFlow<List<GenericContent>> get() = _trendingMulti

    private val _myWatchlist: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList()
    )
    val myWatchlist: StateFlow<List<GenericContent>> get() = _myWatchlist

    private val _trendingPerson: MutableStateFlow<List<PersonDetails>> = MutableStateFlow(
        emptyList()
    )
    val trendingPerson: StateFlow<List<PersonDetails>> get() = _trendingPerson

    private val _moviesComingSoon: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList()
    )
    val moviesComingSoon: StateFlow<List<GenericContent>> get() = _moviesComingSoon

    private val _allLists: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val allLists: StateFlow<List<ListItem>> get() = _allLists

    private val _featuredContentInListStatus: MutableStateFlow<Map<Int, Boolean>> = MutableStateFlow(
        emptyMap()
    )
    val featuredContentInListStatus: StateFlow<Map<Int, Boolean>> get() = _featuredContentInListStatus

    private val _showListBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showListBottomSheet: StateFlow<Boolean> get() = _showListBottomSheet

    init {
        runBackfill()
        loadHomeScreen()
        collectWatchlist()
        collectAllLists()
    }

    private fun runBackfill() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedFieldsBackfill.backfillIfNeeded()
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadHome -> loadHomeScreen()
            HomeEvent.OnError -> resetHome()
            is HomeEvent.ToggleFeaturedFromList -> toggleFeaturedFromList(event.listId)
            HomeEvent.OpenListBottomSheet -> _showListBottomSheet.value = true
            HomeEvent.CloseListBottomSheet -> {
                _showListBottomSheet.value = false
            }
        }
    }

    private fun loadHomeScreen() {
        viewModelScope.launch {
            _loadState.value = DataLoadStatus.Loading
            val homeState = homeInteractor.getTrendingMulti()
            if (homeState.isFailed()) {
                _loadState.value = DataLoadStatus.Failed
                return@launch
            } else {
                _trendingMulti.value = homeState.trendingList.value
            }

            loadFeaturedListStatus()
            _trendingPerson.value = homeInteractor.getTrendingPerson()
            _moviesComingSoon.value = homeInteractor.getMoviesComingSoon()
            _loadState.value = DataLoadStatus.Success
        }
    }

    private fun collectWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            homeInteractor.getWatchlistFlow().collectLatest { watchlist ->
                _myWatchlist.value = watchlist
            }
        }
    }

    private fun collectAllLists() {
        viewModelScope.launch(Dispatchers.IO) {
            listInteractor.getAllLists().collectLatest { lists ->
                _allLists.value = lists
            }
        }
    }

    private fun loadFeaturedListStatus() {
        val featured = _trendingMulti.value.firstOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            listInteractor.verifyContentInLists(
                contentId = featured.id,
                mediaType = featured.mediaType
            ).collectLatest { status ->
                _featuredContentInListStatus.value = status
            }
        }
    }

    private fun toggleFeaturedFromList(listId: Int) {
        val featured = _trendingMulti.value.firstOrNull() ?: return
        val currentStatus = _featuredContentInListStatus.value[listId] ?: false
        viewModelScope.launch(Dispatchers.IO) {
            listInteractor.toggleWatchlist(
                currentStatus = currentStatus,
                contentId = featured.id,
                mediaType = featured.mediaType,
                listId = listId,
                title = featured.name,
                posterPath = featured.posterPath,
                voteAverage = featured.rating.toFloat()
            )
        }
    }

    private fun resetHome() {
        _loadState.value = DataLoadStatus.Loading
        _trendingMulti.value = emptyList()
        _trendingPerson.value = emptyList()
        _moviesComingSoon.value = emptyList()
        _featuredContentInListStatus.value = emptyMap()
    }
}
