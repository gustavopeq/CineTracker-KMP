package network.models.content.search

import kotlinx.serialization.Serializable
import network.models.content.common.BaseContentResponse

@Serializable
data class ContentPagingResponse<T : BaseContentResponse>(
    val page: Int,
    val results: List<T> = emptyList(),
    val total_pages: Int,
    val total_results: Int,
)
