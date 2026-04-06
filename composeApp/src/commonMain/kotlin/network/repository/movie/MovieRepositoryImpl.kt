package network.repository.movie

import common.domain.models.util.ContentListType
import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.MovieResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.services.movie.MovieService
import network.util.Either
import network.util.asFlow

class MovieRepositoryImpl(private val movieService: MovieService) : MovieRepository {
    override suspend fun getMovieList(
        contentListType: ContentListType,
        pageIndex: Int,
        language: String,
        region: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> = movieService.getMovieList(
        movieListType = contentListType.type,
        pageIndex = pageIndex,
        language = language,
        region = region
    ).asFlow()

    override suspend fun getMovieDetailsById(
        movieId: Int,
        language: String
    ): Flow<Either<MovieResponse, ApiError>> =
        movieService.getMovieDetailsById(
            movieId = movieId,
            language = language
        ).asFlow()

    override suspend fun getMovieCreditsById(
        movieId: Int,
        language: String
    ): Flow<Either<ContentCreditsResponse, ApiError>> =
        movieService.getMovieCreditsById(
            movieId = movieId,
            language = language
        ).asFlow()

    override suspend fun getMovieVideosById(
        movieId: Int,
        language: String
    ): Flow<Either<VideosByIdResponse, ApiError>> =
        movieService.getMovieVideosById(
            movieId = movieId,
            language = language
        ).asFlow()

    override suspend fun getRecommendationsMoviesById(
        movieId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> = movieService.getRecommendationsMoviesById(
        movieId = movieId,
        language = language
    ).asFlow()

    override suspend fun getSimilarMoviesById(
        movieId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> = movieService.getSimilarMoviesById(
        movieId = movieId,
        language = language
    ).asFlow()

    override suspend fun getStreamingProviders(movieId: Int): Flow<Either<WatchProvidersResponse, ApiError>> =
        movieService.getStreamingProviders(
            movieId = movieId
        ).asFlow()
}
