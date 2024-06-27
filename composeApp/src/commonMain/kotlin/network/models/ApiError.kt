package network.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String? = null,
    @Contextual val exception: Throwable? = null,
)
