package features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.GenericContent
import common.domain.models.person.PersonDetails
import common.domain.models.util.DataLoadStatus
import features.home.ui.domain.HomeInteractor
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

//            loadWatchlist()
            _trendingPerson.value = homeInteractor.getTrendingPerson()
            _moviesComingSoon.value = homeInteractor.getMoviesComingSoon()
            _loadState.value = DataLoadStatus.Success
        }
    }
}
