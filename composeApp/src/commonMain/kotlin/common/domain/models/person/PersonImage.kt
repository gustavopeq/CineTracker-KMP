package common.domain.models.person

import network.models.content.person.PersonProfileResponse

data class PersonImage(
    val aspectRatio: Double?,
    val filePath: String?,
    val height: Int?,
    val width: Int?,
)

fun PersonProfileResponse.toPersonImage(): PersonImage =
    PersonImage(
        aspectRatio = this.aspect_ratio,
        filePath = this.file_path,
        height = this.height,
        width = this.width,
    )
