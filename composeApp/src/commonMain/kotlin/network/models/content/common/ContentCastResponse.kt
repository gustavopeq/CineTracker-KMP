package network.models.content.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentCastResponse(
    val id: Int,
    val name: String,
    val character: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
    val adult: Boolean? = null,
    val credit_id: String? = null,
    val gender: Int? = null,
    val known_for_department: String? = null,
    val order: Int? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    val popularity: Double? = null,
    val roles: List<CastRoles>? = null
)

@Serializable
data class CastRoles(val credit_id: String? = null, val character: String? = null, val episode_count: Int? = null)
