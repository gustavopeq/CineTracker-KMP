package common.domain.models.content

import common.domain.models.util.MediaType
import common.domain.util.UiConstants.EMPTY_RATINGS
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
)

fun BaseContentResponse.toGenericContent(): GenericContent? {
    val posterPath: String? = this.poster_path ?: this.profile_path
    val backdropPath: String? = this.backdrop_path
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

    if (mediaType == MediaType.UNKNOWN || posterPath.isNullOrEmpty()) {
        return null
    }

    return GenericContent(
        id = this.id,
        name = name.orEmpty(),
        rating = this.vote_average ?: EMPTY_RATINGS,
        overview = this.overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath.orEmpty(),
        mediaType = mediaType,
    )
}

fun List<CastResponse>?.toGenericContentList(): List<GenericContent> =
    this?.map { castResponse ->
        GenericContent(
            id = castResponse.id,
            name = castResponse.title,
            posterPath = castResponse.poster_path.orEmpty(),
            backdropPath = castResponse.backdrop_path.orEmpty(),
            overview = castResponse.overview.orEmpty(),
            mediaType = castResponse.mediaType,
            rating = castResponse.vote_average ?: EMPTY_RATINGS,
        )
    } ?: emptyList()
