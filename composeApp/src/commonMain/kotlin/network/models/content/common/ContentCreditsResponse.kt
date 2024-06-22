package network.models.content.common

import kotlinx.serialization.Serializable

@Serializable
data class ContentCreditsResponse(
    val id: Int = 0,
    val cast: List<ContentCastResponse>? = emptyList(),
)
