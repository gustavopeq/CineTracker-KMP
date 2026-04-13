package database.repository

import common.domain.models.util.MediaType
import database.model.ContentEntity
import database.model.ListEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertItem(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
        title: String = "",
        posterPath: String? = null,
        voteAverage: Float = 0f
    )

    suspend fun deleteItem(contentId: Int, mediaType: MediaType, listId: Int): ContentEntity?

    fun getAllItemsByListId(listId: Int): Flow<List<ContentEntity>>

    fun searchItems(contentId: Int, mediaType: MediaType): Flow<List<ContentEntity>>

    suspend fun moveItemToList(contentId: Int, mediaType: MediaType, currentListId: Int, newListId: Int): ContentEntity?

    suspend fun reinsertItem(contentEntity: ContentEntity)

    fun getAllLists(): Flow<List<ListEntity>>

    suspend fun addNewList(listName: String): Boolean

    suspend fun clearList(listId: Int)

    suspend fun deleteList(listId: Int)

    suspend fun resetToDefaults()

    suspend fun getEntitiesWithMissingCachedFields(): List<ContentEntity>

    suspend fun updateCachedFields(
        contentId: Int,
        mediaType: MediaType,
        title: String,
        posterPath: String?,
        voteAverage: Float
    )
}
