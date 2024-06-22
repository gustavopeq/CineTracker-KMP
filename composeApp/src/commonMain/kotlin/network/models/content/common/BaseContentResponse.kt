package network.models.content.common

import domain.models.util.MediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface BaseContentResponse {
    val id: Int
    val adult: Boolean?
    val popularity: Double?
    val poster_path: String?
    val profile_path: String?
    val backdrop_path: String?
    val title: String?
    val name: String?
    val original_title: String?
    val original_name: String?
    val vote_average: Double?
    val overview: String?
}

@Serializable
data class MultiResponse(
    override val id: Int,
    override val adult: Boolean? = null,
    override val original_title: String? = null,
    override val popularity: Double? = null,
    override val poster_path: String? = null,
    override val profile_path: String? = null,
    override val backdrop_path: String? = null,
    override val title: String? = null,
    override val name: String? = null,
    override val original_name: String? = null,
    override val vote_average: Double? = null,
    override val overview: String? = null,
    val genre_ids: List<Int>? = null,
    val media_type: String? = null,
    val original_language: String? = null,
    val release_date: String? = null,
    val video: Boolean? = null,
    val vote_count: Int? = null,
) : BaseContentResponse

@Serializable
data class MovieResponse(
    override val id: Int,
    override val adult: Boolean? = null,
    override val original_title: String? = null,
    override val popularity: Double? = null,
    override val poster_path: String? = null,
    override val profile_path: String? = null,
    override val backdrop_path: String? = null,
    override val title: String? = null,
    override val name: String? = null,
    override val original_name: String? = null,
    override val vote_average: Double? = null,
    override val overview: String? = null,
    val genre_ids: List<Int>? = null,
    val original_language: String? = null,
    val release_date: String? = null,
    val video: Boolean? = null,
    val vote_count: Int? = null,
    val production_countries: List<ProductionCountry?>? = null,
    val genres: List<ContentGenre?>? = null,
    val runtime: Int? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
) : BaseContentResponse

@Serializable
data class ShowResponse(
    override val id: Int,
    override val adult: Boolean? = null,
    override val popularity: Double? = null,
    override val poster_path: String? = null,
    override val profile_path: String? = null,
    override val backdrop_path: String? = null,
    override val original_title: String? = null,
    override val name: String? = null,
    override val original_name: String? = null,
    override val vote_average: Double? = null,
    override val overview: String? = null,
    val genre_ids: List<Int>? = null,
    val original_language: String? = null,
    val vote_count: Int? = null,
    val first_air_date: String? = null,
    val last_air_date: String? = null,
    val origin_country: List<String>? = null,
    val production_countries: List<ProductionCountry?>? = null,
    val genres: List<ContentGenre?>? = null,
    val number_of_seasons: Int? = null,
    val number_of_episodes: Int? = null,
) : BaseContentResponse {
    override val title: String
        get() = name.orEmpty()
}

@Serializable
data class PersonResponse(
    override val id: Int,
    override val adult: Boolean? = null,
    override val popularity: Double? = null,
    override val title: String? = null,
    override val original_title: String? = null,
    override val poster_path: String? = null,
    override val backdrop_path: String? = null,
    override val profile_path: String? = null,
    override val name: String? = null,
    override val original_name: String? = null,
    override val vote_average: Double? = null,
    override val overview: String? = null,
    val genre_ids: List<Int>? = null,
    val original_language: String? = null,
    val gender: Int? = null,
    val known_for_department: String? = null,
    val known_for: List<MultiResponse>? = null,
    val biography: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val place_of_birth: String? = null,
) : BaseContentResponse

@Serializable
data class CastResponse(
    override val id: Int,
    override val adult: Boolean? = null,
    override val popularity: Double? = null,
    override val poster_path: String? = null,
    override val profile_path: String? = null,
    override val backdrop_path: String? = null,
    override val name: String? = null,
    override val original_title: String? = null,
    override val original_name: String? = null,
    override val vote_average: Double? = null,
    override val overview: String? = null,
    @SerialName("title")
    val _title: String? = null,
    val media_type: String? = null,
) : BaseContentResponse {
    val mediaType: MediaType
        get() =
            when (media_type) {
                "tv" -> MediaType.SHOW
                else -> MediaType.MOVIE
            }
    override val title: String
        get() = _title ?: name.orEmpty()
}
