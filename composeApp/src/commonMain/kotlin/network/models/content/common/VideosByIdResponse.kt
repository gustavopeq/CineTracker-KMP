package network.models.content.common

import kotlinx.serialization.Serializable

@Serializable
data class VideosByIdResponse(
    val id: Int? = null,
    val results: List<VideoResponse>? = null,
)
