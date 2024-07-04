package features.browse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import common.domain.models.content.GenericContent
import common.domain.models.util.ContentListType
import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import features.browse.domain.BrowseInteractor
import features.browse.events.BrowseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val interactor: BrowseInteractor,
) : ViewModel() {

    private val _moviePager: MutableStateFlow<PagingData<GenericContent>> = MutableStateFlow(
        PagingData.empty(),
    )
    val moviePager: StateFlow<PagingData<GenericContent>> get() = _moviePager

    private val _showPager: MutableStateFlow<PagingData<GenericContent>> = MutableStateFlow(
        PagingData.empty(),
    )
    val showPager: StateFlow<PagingData<GenericContent>> get() = _showPager

    private var movieSortTypeSelected: ContentListType? = null
    private var showSortTypeSelected: ContentListType? = null

    private val _mediaTypeSelected = MutableStateFlow(MediaType.MOVIE)

    fun onEvent(event: BrowseEvent) {
        when (event) {
            is BrowseEvent.UpdateSortType -> updateSortType(event.movieListType, event.mediaType)
            is BrowseEvent.UpdateMediaType -> updateMediaType(event.mediaType)
            is BrowseEvent.OnError -> resetBrowse()
        }
    }

    private fun updateSortType(
        sortTypeItem: SortTypeItem,
        mediaType: MediaType,
    ) {
        val currentSortType = when (mediaType) {
            MediaType.MOVIE -> movieSortTypeSelected
            MediaType.SHOW -> showSortTypeSelected
            else -> return
        }

        if (sortTypeItem.listType != currentSortType) {
            viewModelScope.launch {
                interactor.getMediaContentListPager(
                    sortTypeItem.listType,
                    mediaType,
                )
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        when (mediaType) {
                            MediaType.MOVIE -> {
                                movieSortTypeSelected = sortTypeItem.listType
                                _moviePager.value = it
                            }
                            MediaType.SHOW -> {
                                showSortTypeSelected = sortTypeItem.listType
                                _showPager.value = it
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    private fun updateMediaType(
        mediaType: MediaType,
    ) {
        _mediaTypeSelected.value = mediaType
    }

    private fun resetBrowse() {
        movieSortTypeSelected = null
        showSortTypeSelected = null
        _moviePager.value = PagingData.empty()
        _showPager.value = PagingData.empty()
    }
}
