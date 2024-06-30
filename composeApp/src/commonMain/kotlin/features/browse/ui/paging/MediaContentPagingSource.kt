package features.browse.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.util.ContentListType
import common.domain.models.util.MediaType
import kotlinx.coroutines.flow.first
import network.models.content.common.MovieResponse
import network.models.content.common.ShowResponse
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import network.util.Left
import network.util.Right

class MediaContentPagingSource(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val contentListType: ContentListType,
    private val mediaType: MediaType,
) : PagingSource<Int, GenericContent>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GenericContent> {
        return try {
            val pageNumber = params.key ?: 1
            val previousKey = if (pageNumber == 1) {
                null
            } else {
                pageNumber - 1
            }

            val apiResponse = when (mediaType) {
                MediaType.MOVIE -> movieRepository.getMovieList(contentListType, pageNumber).first()
                MediaType.SHOW -> {
                    showRepository.getShowList(contentListType.type, pageNumber).first()
                }
                else -> {
                    throw IllegalStateException("Invalid media type for paging source: $mediaType")
                }
            }

            return when (apiResponse) {
                is Right -> {
                    println("Paging source error: ${apiResponse.error.exception}")
                    LoadResult.Error(
                        apiResponse.error.exception ?: Exception("Unknown error"),
                    )
                }

                is Left -> {
                    val data = apiResponse.value.results.mapNotNull {
                        when (it) {
                            is MovieResponse -> it.toGenericContent()
                            is ShowResponse -> it.toGenericContent()
                            else -> {
                                throw IllegalStateException(
                                    "Invalid media type for paging source: $mediaType",
                                )
                            }
                        }
                    }
                    LoadResult.Page(
                        data = data,
                        prevKey = previousKey,
                        nextKey = pageNumber + 1,
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GenericContent>): Int =
        ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
}
