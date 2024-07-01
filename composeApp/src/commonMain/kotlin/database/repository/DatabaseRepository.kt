package database.repository

import common.domain.models.util.MediaType
import database.model.ContentEntity
import database.model.ListEntity

interface DatabaseRepository {
    suspend fun insertItem(contentId: Int, mediaType: MediaType, listId: Int)

    suspend fun deleteItem(contentId: Int, mediaType: MediaType, listId: Int): ContentEntity?

    suspend fun getAllItemsByListId(listId: Int): List<ContentEntity>

    suspend fun searchItems(contentId: Int, mediaType: MediaType): List<ContentEntity>

    suspend fun moveItemToList(
        contentId: Int,
        mediaType: MediaType,
        currentListId: Int,
        newListId: Int,
    ): ContentEntity?

    suspend fun reinsertItem(contentEntity: ContentEntity)

    suspend fun getAllLists(): List<ListEntity>

    suspend fun addNewList(listName: String): Boolean

    suspend fun deleteList(listId: Int)
}
