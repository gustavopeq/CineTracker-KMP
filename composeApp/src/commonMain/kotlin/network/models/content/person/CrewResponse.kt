package network.models.content.person

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrewResponse(
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    val credit_id: String? = null,
    val department: String? = null,
    val genre_ids: List<Int?>? = null,
    val id: Int? = null,
    val job: String? = null,
    val original_language: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    val release_date: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    val vote_count: Int? = null
)
