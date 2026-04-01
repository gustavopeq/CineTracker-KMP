package network.models.content.person

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonProfileResponse(
    val aspect_ratio: Double? = null,
    val file_path: String? = null,
    val height: Int? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    val vote_count: Int? = null,
    val width: Int? = null
)
