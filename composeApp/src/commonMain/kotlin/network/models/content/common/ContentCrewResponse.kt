package network.models.content.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentCrewResponse(
    val id: Int,
    val name: String = "",
    val job: String? = null,
    val department: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)
