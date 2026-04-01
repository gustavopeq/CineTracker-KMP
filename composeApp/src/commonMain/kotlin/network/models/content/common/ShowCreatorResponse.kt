package network.models.content.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowCreatorResponse(
    val id: Int,
    val name: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)
