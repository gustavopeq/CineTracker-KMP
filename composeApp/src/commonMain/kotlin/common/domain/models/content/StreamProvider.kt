package common.domain.models.content

import network.models.content.common.ProviderResponse

data class StreamProvider(
    val providerName: String,
    val logoPath: String,
)

fun ProviderResponse.toStreamProvider(): StreamProvider =
    StreamProvider(
        providerName = this.provider_name.orEmpty(),
        logoPath = this.logo_path.orEmpty(),
    )
