package common.domain.models.content

import network.models.content.common.VideoResponse

data class Videos(
    val name: String,
    val key: String,
    val publishedAt: String,
)

fun VideoResponse.toVideos(): Videos =
    Videos(
        name = this.name,
        key = this.key,
        publishedAt = this.published_at,
    )
