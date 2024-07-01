package features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.GenericContent
import common.domain.models.person.PersonDetails
import common.domain.models.util.DataLoadStatus
import features.home.domain.HomeInteractor
import features.home.events.HomeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
) : ViewModel() {
    private val _loadState: MutableStateFlow<DataLoadStatus> = MutableStateFlow(
        DataLoadStatus.Loading,
    )
    val loadState: StateFlow<DataLoadStatus> get() = _loadState

    private val _trendingMulti: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList(),
    )
    val trendingMulti: StateFlow<List<GenericContent>> get() = _trendingMulti

    private val _myWatchlist: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList(),
    )
    val myWatchlist: StateFlow<List<GenericContent>> get() = _myWatchlist

    private val _trendingPerson: MutableStateFlow<List<PersonDetails>> = MutableStateFlow(
        emptyList(),
    )
    val trendingPerson: StateFlow<List<PersonDetails>> get() = _trendingPerson

    private val _moviesComingSoon: MutableStateFlow<List<GenericContent>> = MutableStateFlow(
        emptyList(),
    )
    val moviesComingSoon: StateFlow<List<GenericContent>> get() = _moviesComingSoon

    init {
        loadHomeScreen()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadHome -> loadHomeScreen()
            HomeEvent.ReloadWatchlist -> loadWatchlist()
            HomeEvent.OnError -> resetHome()
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

            loadWatchlist()
            _trendingPerson.value = homeInteractor.getTrendingPerson()
            _moviesComingSoon.value = homeInteractor.getMoviesComingSoon()
            _loadState.value = DataLoadStatus.Success
        }
    }

    private fun loadWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            _myWatchlist.value = homeInteractor.getAllWatchlist()
        }
    }

    private fun resetHome() {
        _loadState.value = DataLoadStatus.Loading
        _trendingMulti.value = emptyList()
        _trendingPerson.value = emptyList()
        _moviesComingSoon.value = emptyList()
    }
}
