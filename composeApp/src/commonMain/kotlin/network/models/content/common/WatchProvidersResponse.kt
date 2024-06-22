package network.models.content.common

import kotlinx.serialization.Serializable

@Serializable
data class WatchProvidersResponse(
    val id: Int,
    val results: Map<String, CountryProviderResponse>? = null,
)

@Serializable
data class CountryProviderResponse(
    val flatrate: List<ProviderResponse>? = null,
)

@Serializable
data class ProviderResponse(
    val logo_path: String? = null,
    val provider_name: String? = null,
)
