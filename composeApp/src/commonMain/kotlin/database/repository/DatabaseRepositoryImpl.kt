package database.repository

import common.domain.models.util.MediaType
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.model.ContentEntity
import database.model.ListEntity
import kotlinx.coroutines.flow.Flow

class DatabaseRepositoryImpl(private val contentEntityDao: ContentEntityDao, private val listEntityDao: ListEntityDao) :
    DatabaseRepository {

    override suspend fun insertItem(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
        title: String,
        posterPath: String?,
        voteAverage: Float
    ) {
        val item = ContentEntity(
            contentId = contentId,
            mediaType = mediaType.name,
            listId = listId,
            title = title,
            posterPath = posterPath,
            voteAverage = voteAverage
        )
        contentEntityDao.insert(item)
    }

    override suspend fun deleteItem(contentId: Int, mediaType: MediaType, listId: Int): ContentEntity? {
        val itemRemoved = contentEntityDao.getItem(
            contentId = contentId,
            mediaType = mediaType.name,
            listId = listId
        )
        if (itemRemoved != null) {
            contentEntityDao.delete(
                contentId = contentId,
                mediaType = mediaType.name,
                listId = listId
            )
        }
        return itemRemoved
    }

    override fun getAllItemsByListId(listId: Int): Flow<List<ContentEntity>> = contentEntityDao.getAllItems(listId)

    override fun searchItems(contentId: Int, mediaType: MediaType): Flow<List<ContentEntity>> =
        contentEntityDao.searchItems(
            contentId = contentId,
            mediaType = mediaType.name
        )

    override suspend fun moveItemToList(
        contentId: Int,
        mediaType: MediaType,
        currentListId: Int,
        newListId: Int
    ): ContentEntity? {
        val existingItem = contentEntityDao.getItem(contentId, mediaType.name, currentListId)
        deleteItem(contentId = contentId, mediaType = mediaType, listId = newListId)
        insertItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = newListId,
            title = existingItem?.title.orEmpty(),
            posterPath = existingItem?.posterPath,
            voteAverage = existingItem?.voteAverage ?: 0f
        )
        return deleteItem(contentId = contentId, mediaType = mediaType, listId = currentListId)
    }

    override suspend fun reinsertItem(contentEntity: ContentEntity) {
        contentEntityDao.insert(contentEntity)
    }

    override fun getAllLists(): Flow<List<ListEntity>> = listEntityDao.getAllLists()

    override suspend fun addNewList(listName: String): Boolean {
        val newListName = listName.lowercase()
        val isDuplicated = listEntityDao.getListCountByName(newListName) > 0

        return if (isDuplicated) {
            false
        } else {
            listEntityDao.insertList(
                listEntity = ListEntity(
                    listName = newListName
                )
            )
            true
        }
    }

    override suspend fun deleteList(listId: Int) {
        listEntityDao.deleteList(listId)
    }

    override suspend fun getEntitiesWithMissingCachedFields(): List<ContentEntity> =
        contentEntityDao.getEntitiesWithMissingCachedFields()

    override suspend fun updateCachedFields(
        contentId: Int,
        mediaType: MediaType,
        title: String,
        posterPath: String?,
        voteAverage: Float
    ) {
        contentEntityDao.updateCachedFields(
            contentId = contentId,
            mediaType = mediaType.name,
            title = title,
            posterPath = posterPath,
            voteAverage = voteAverage
        )
    }
}
