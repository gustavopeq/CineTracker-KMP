package network.models.content.person

import kotlinx.serialization.Serializable

@Serializable
data class PersonImagesResponse(
    val id: Int? = null,
    val profiles: List<PersonProfileResponse?>? = null,
)
