package network.models.content.person

import kotlinx.serialization.Serializable
import network.models.content.common.CastResponse

@Serializable
data class PersonCreditsResponse(
    val cast: List<CastResponse>? = null,
    val crew: List<CrewResponse?>? = null,
    val id: Int? = null,
)
