package network.models.content.common

import kotlinx.serialization.Serializable

@Serializable
data class ContentGenre(
    val id: Int? = null,
    val name: String? = null,
)
