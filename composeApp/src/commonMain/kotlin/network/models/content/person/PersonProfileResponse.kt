package network.models.content.person

import kotlinx.serialization.Serializable

@Serializable
data class PersonProfileResponse(
    val aspect_ratio: Double? = null,
    val file_path: String? = null,
    val height: Int? = null,
    val vote_average: Double? = null,
    val vote_count: Int? = null,
    val width: Int? = null,
)
