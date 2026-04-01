package common.util

import common.domain.models.util.MediaType
import database.model.ContentEntity
import database.model.ListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import network.models.ApiError
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.util.Either
import network.util.Left
import network.util.Right

// ── Flow helpers ──────────────────────────────────────────────────────────────

fun <T> successFlow(value: T): Flow<Either<T, ApiError>> = flow { emit(Left(value)) }

fun <T> errorFlow(code: String? = "404"): Flow<Either<T, ApiError>> = flow { emit(Right(ApiError(code = code))) }

// ── Response factories ────────────────────────────────────────────────────────

fun fakeMovieResponse(id: Int = 1, title: String = "Test Movie") = MovieResponse(
    id = id,
    title = title,
    posterPath = "/poster.jpg",
    backdropPath = "/backdrop.jpg",
    voteAverage = 7.5,
    overview = "Test overview"
)

fun fakeShowResponse(id: Int = 1, name: String = "Test Show") = ShowResponse(
    id = id,
    name = name,
    posterPath = "/poster.jpg",
    backdropPath = "/backdrop.jpg",
    voteAverage = 7.5,
    overview = "Test overview"
)

fun fakeMultiResponse(id: Int = 1, title: String = "Test Movie", mediaType: String = "movie") = MultiResponse(
    id = id,
    title = title,
    posterPath = "/poster.jpg",
    backdropPath = "/backdrop.jpg",
    voteAverage = 7.5,
    overview = "Overview",
    media_type = mediaType
)

fun fakeMoviePagingResponse(vararg movies: MovieResponse) = ContentPagingResponse(
    page = 1,
    results = movies.toList(),
    total_pages = 1,
    total_results = movies.size
)

fun fakeMultiPagingResponse(vararg items: MultiResponse) = ContentPagingResponse(
    page = 1,
    results = items.toList(),
    total_pages = 1,
    total_results = items.size
)

fun fakePersonPagingResponse(vararg people: PersonResponse) = ContentPagingResponse(
    page = 1,
    results = people.toList(),
    total_pages = 1,
    total_results = people.size
)

fun fakeShowPagingResponse(vararg shows: ShowResponse) = ContentPagingResponse(
    page = 1,
    results = shows.toList(),
    total_pages = 1,
    total_results = shows.size
)

// ── Database entity factories ─────────────────────────────────────────────────

fun fakeListEntity(listId: Int, name: String = "List $listId", isDefault: Boolean = false) = ListEntity(
    listId = listId,
    listName = name,
    isDefault = isDefault
)

fun fakeContentEntity(
    contentId: Int = 1,
    listId: Int = 1,
    mediaType: String = MediaType.MOVIE.name,
    title: String = "Fake Movie",
    posterPath: String? = "/fake_poster.jpg",
    voteAverage: Float = 7.5f
) = ContentEntity(
    contentId = contentId,
    listId = listId,
    mediaType = mediaType,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage
)
