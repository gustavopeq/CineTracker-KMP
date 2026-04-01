package common.domain.models.content

import common.domain.models.util.MediaType
import common.util.UiConstants.EMPTY_RATINGS
import network.models.content.common.BaseContentResponse
import network.models.content.common.CastResponse
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse

data class GenericContent(
    val id: Int,
    val name: String,
    val rating: Double,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val mediaType: MediaType,
    val personalRating: Float? = null
)

fun BaseContentResponse.toGenericContent(): GenericContent? {
    val resolvedPosterPath: String? = this.posterPath ?: this.profilePath
    val resolvedBackdropPath: String? = this.backdropPath
    val name: String? = this.title ?: this.name
    val mediaType =
        when (this) {
            is MovieResponse -> MediaType.MOVIE
            is ShowResponse -> MediaType.SHOW
            is PersonResponse -> MediaType.PERSON
            is MultiResponse -> MediaType.getType(this.media_type)
            else -> {
                MediaType.UNKNOWN
            }
        }

    if (mediaType == MediaType.UNKNOWN || resolvedPosterPath.isNullOrEmpty()) {
        return null
    }

    return GenericContent(
        id = this.id,
        name = name.orEmpty(),
        rating = this.voteAverage ?: EMPTY_RATINGS,
        overview = this.overview.orEmpty(),
        posterPath = resolvedPosterPath,
        backdropPath = resolvedBackdropPath.orEmpty(),
        mediaType = mediaType
    )
}

fun List<CastResponse>?.toGenericContentList(): List<GenericContent> = this?.map { castResponse ->
    GenericContent(
        id = castResponse.id,
        name = castResponse.title,
        posterPath = castResponse.posterPath.orEmpty(),
        backdropPath = castResponse.backdropPath.orEmpty(),
        overview = castResponse.overview.orEmpty(),
        mediaType = castResponse.mediaType,
        rating = castResponse.voteAverage ?: EMPTY_RATINGS
    )
} ?: emptyList()
