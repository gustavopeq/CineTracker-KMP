package common.domain.models.content

import network.models.content.common.ContentCastResponse

data class ContentCast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePoster: String,
    val order: Int?,
)

fun ContentCastResponse.toContentCast(): ContentCast =
    ContentCast(
        id = this.id,
        name = this.name,
        character =
            this.character ?: this.roles
                ?.firstOrNull()
                ?.character
                .orEmpty(),
        profilePoster = this.profile_path.orEmpty(),
        order = this.order,
    )
