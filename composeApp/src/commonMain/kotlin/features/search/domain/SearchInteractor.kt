package features.search.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.util.Constants
import features.search.ui.paging.SearchPagingSource
import kotlinx.coroutines.flow.Flow
import network.repository.search.SearchRepository

class SearchInteractor(
    private val searchRepository: SearchRepository,
) {
    fun onSearchQuery(
        query: String,
        mediaType: MediaType?,
    ): Flow<PagingData<GenericContent>> {
        return Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
            SearchPagingSource(
                searchRepository,
                query,
                mediaType,
            )
        }.flow
    }
}
