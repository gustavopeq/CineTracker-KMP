package network.models.content.common

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    val name: String,
    val key: String,
    val published_at: String,
    val id: String? = null,
    val iso_3166_1: String? = null,
    val iso_639_1: String? = null,
    val official: Boolean? = null,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null,
)
