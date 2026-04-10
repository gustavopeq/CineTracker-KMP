package auth.model

import database.model.ContentEntity
import database.model.ListEntity
import database.model.PersonalRatingEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// region Upload DTOs (sent to Supabase RPC)

@Serializable
data class CloudListUpload(
    @SerialName("local_list_id") val localListId: Int,
    @SerialName("list_name") val listName: String,
    @SerialName("is_default") val isDefault: Boolean
)

@Serializable
data class CloudContentUpload(
    @SerialName("content_id") val contentId: Int,
    @SerialName("media_type") val mediaType: String,
    @SerialName("local_list_id") val localListId: Int,
    @SerialName("created_at") val createdAt: Long,
    val title: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0f
)

@Serializable
data class CloudRatingUpload(
    @SerialName("content_id") val contentId: Int,
    @SerialName("media_type") val mediaType: String,
    val rating: Float
)

@Serializable
data class UploadSnapshotRequest(
    @SerialName("p_lists") val lists: List<CloudListUpload>,
    @SerialName("p_content") val content: List<CloudContentUpload>,
    @SerialName("p_ratings") val ratings: List<CloudRatingUpload>
)

// endregion

// region Download DTOs (received from Supabase REST)

@Serializable
data class CloudListDownload(
    val id: String,
    @SerialName("local_list_id") val localListId: Int,
    @SerialName("list_name") val listName: String,
    @SerialName("is_default") val isDefault: Boolean
)

@Serializable
data class CloudContentDownload(
    @SerialName("content_id") val contentId: Int,
    @SerialName("media_type") val mediaType: String,
    @SerialName("list_id") val listId: String,
    @SerialName("created_at") val createdAt: Long,
    val title: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0f
)

@Serializable
data class CloudRatingDownload(
    @SerialName("content_id") val contentId: Int,
    @SerialName("media_type") val mediaType: String,
    val rating: Float
)

// endregion

// region Entity Mapping Extensions

fun ListEntity.toCloudListUpload() = CloudListUpload(
    localListId = listId,
    listName = listName,
    isDefault = isDefault
)

fun ContentEntity.toCloudContentUpload() = CloudContentUpload(
    contentId = contentId,
    mediaType = mediaType,
    localListId = listId,
    createdAt = createdAt,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage
)

fun PersonalRatingEntity.toCloudRatingUpload() = CloudRatingUpload(
    contentId = contentId,
    mediaType = mediaType,
    rating = rating
)

fun CloudListDownload.toListEntity() = ListEntity(
    listId = localListId,
    listName = listName,
    isDefault = isDefault
)

fun CloudContentDownload.toContentEntity(localListId: Int) = ContentEntity(
    contentId = contentId,
    mediaType = mediaType,
    listId = localListId,
    createdAt = createdAt,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage
)

fun CloudRatingDownload.toPersonalRatingEntity() = PersonalRatingEntity(
    contentId = contentId,
    mediaType = mediaType,
    rating = rating
)

// endregion
