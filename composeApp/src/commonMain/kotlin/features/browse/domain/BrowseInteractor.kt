package features.browse.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import common.domain.models.content.GenericContent
import common.domain.models.util.ContentListType
import common.domain.models.util.MediaType
import common.domain.util.Constants.PAGE_SIZE
import features.browse.ui.paging.MediaContentPagingSource
import kotlinx.coroutines.flow.Flow
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository

class BrowseInteractor(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
) {
    fun getMediaContentListPager(
        contentListType: ContentListType,
        mediaType: MediaType,
    ): Flow<PagingData<GenericContent>> {
        return Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            MediaContentPagingSource(
                movieRepository,
                showRepository,
                contentListType,
                mediaType,
            )
        }.flow
    }
}
